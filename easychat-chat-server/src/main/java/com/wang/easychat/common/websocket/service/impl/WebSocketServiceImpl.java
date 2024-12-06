package com.wang.easychat.common.websocket.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.wang.easychat.common.common.constant.RedisKey;
import com.wang.easychat.common.common.event.UserOnLineEvent;
import com.wang.easychat.common.common.utils.RedisUtils;
import com.wang.easychat.common.user.domain.entity.IpInfo;
import com.wang.easychat.common.user.domain.entity.User;
import com.wang.easychat.common.user.domain.enums.ChatActiveStatusEnum;
import com.wang.easychat.common.user.domain.enums.RoleEnum;
import com.wang.easychat.common.user.service.IRoleService;
import com.wang.easychat.common.user.service.IUserRoleService;
import com.wang.easychat.common.user.service.IUserService;
import com.wang.easychat.common.user.service.LoginService;
import com.wang.easychat.common.websocket.NettyUtil;
import com.wang.easychat.common.websocket.domain.dto.WSChannelExtraDTO;
import com.wang.easychat.common.websocket.domain.enums.WSRespTypeEnum;
import com.wang.easychat.common.websocket.domain.vo.resp.WSBaseResp;
import com.wang.easychat.common.websocket.domain.vo.resp.WSLoginUrl;
import com.wang.easychat.common.websocket.service.WebSocketService;
import com.wang.easychat.common.websocket.service.adapter.WebSocektAdapter;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpQrCodeTicket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @ClassDescription: 专门管理websocket的逻辑，包括推送消息，拉取消息
 * @Author:Wangzd
 * @Date: 2024/11/11
 **/
@Service
@Slf4j
@Getter
public class WebSocketServiceImpl implements WebSocketService {

    @Autowired
    private WxMpService wxMpService;
    @Autowired
    private IUserService userService;
    @Autowired
    private LoginService loginService;
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;
    @Autowired
    private IRoleService roleService;
    @Autowired
    @Qualifier("websocketExecutor")
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    /**
     * 管理所有用户的连接(登录用户/游客)
     */
    private static final ConcurrentHashMap<Channel, WSChannelExtraDTO> ONLINE_WS_MAP  = new ConcurrentHashMap<>();

    /**
     * 所有在线的用户和对应的socket
     */
    private static final ConcurrentHashMap<Long, CopyOnWriteArrayList<Channel>> ONLINE_UID_MAP = new ConcurrentHashMap<>();

    public static final Duration DURATION = Duration.ofHours(1);
    public static final int MAXIMUM_SIZE = 1000;
    /**
     * 临时保存 登录code 和 channel 的映射关系
     */
    public static final Cache<Integer, Channel> WAIT_LOGIN_MAP = Caffeine.newBuilder()
            .maximumSize(MAXIMUM_SIZE)
            .expireAfterWrite(DURATION)
            .build();
    @Override
    public void connect(Channel channel) {
        ONLINE_WS_MAP.put(channel, new WSChannelExtraDTO());
    }

    @SneakyThrows
    @Override
    public void handleLoginReq(Channel channel) {
        // 生成随机码
        Integer code = generateLoginCode(channel);
        // 找微信申请带参二维码
        WxMpQrCodeTicket wxMpQrCodeTicket = wxMpService.getQrcodeService().qrCodeCreateTmpTicket(code, (int) DURATION.getSeconds());
        // 把二维码推送给前端
        sendMsg(channel, WebSocektAdapter.buildResp(wxMpQrCodeTicket));
    }

    /**
     * 将 随机码 和 channel 绑定在一起
     * @param channel
     * @return
     */
    private Integer generateLoginCode(Channel channel) {
        Integer code;
        do {
            code = RandomUtil.randomInt(Integer.MAX_VALUE);
        }while (Objects.nonNull(WAIT_LOGIN_MAP.asMap().putIfAbsent(code, channel))); // 如果不存在这个随机码，返回null，跳出循环

        return code;
    }

    private void sendMsg(Channel channel, WSBaseResp<?> resp) {
        channel.writeAndFlush(new TextWebSocketFrame(JSONUtil.toJsonStr(resp)));
    }

    @Override
    public void scanLoginSuccess(Integer code, Long uid) {
        if (Objects.isNull(code)){
            return;
        }
        // 确认链接在机器上
        Channel channel = WAIT_LOGIN_MAP.getIfPresent(code);
        if (Objects.isNull(channel)){
            return;
        }
        User user = userService.getById(uid);
        // 移除code
        WAIT_LOGIN_MAP.invalidate(code);
        RedisUtils.del(RedisKey.getKey(RedisKey.WAIT_LOGIN_USER_CODE, uid));
        // 调用登录模块获取token
        String token = loginService.login(uid);
        // 用户登录
        loginSuccess(channel, user, token);
    }

    private void loginSuccess(Channel channel, User user, String token) {
        // 用户
        online(channel, user.getId());


        // 推送成功消息
        sendMsg(channel, WebSocektAdapter.buildResp(user, token,  roleService.hasPower(user.getId(), RoleEnum.CHAT_MANAGER)));
        RedisUtils.del(RedisKey.getKey(RedisKey.WAIT_LOGIN_USER_CODE, user.getId()));

        user.setActiveStatus(ChatActiveStatusEnum.ONLINE.getStatus());
        user.setLastOptTime(new Date());
        user.refreshIp(NettyUtil.getAttr(channel, NettyUtil.IP));
        applicationEventPublisher.publishEvent(new UserOnLineEvent(this, user));
    }

    /**
     * 用户上线执行逻辑
     */
    private void online(Channel channel, Long uid) {
        // 将用户连接存入在线列表
        getOrInitChannelExt(channel).setUid(uid);
        ONLINE_UID_MAP.putIfAbsent(uid, new CopyOnWriteArrayList<>());
        // 兼容多端登录场景
        ONLINE_UID_MAP.get(uid).add(channel);
        NettyUtil.setAttr(channel, NettyUtil.UID, uid);
    }

    /**
     * 如果在线列表不存在，就先把该channel放进在线列表
     */
    private WSChannelExtraDTO getOrInitChannelExt(Channel channel) {
        WSChannelExtraDTO wsChannelExtraDTO = ONLINE_WS_MAP.getOrDefault(channel, new WSChannelExtraDTO());
        WSChannelExtraDTO old = ONLINE_WS_MAP.putIfAbsent(channel, wsChannelExtraDTO);
        return Objects.isNull(old) ? wsChannelExtraDTO : old;
    }

    @Override
    public void authorize(Channel channel, String token) {
        Long validUid = loginService.getValidUid(token);
        if (Objects.nonNull(validUid)){
            User user = userService.getById(validUid);
            loginSuccess(channel, user, token);

        }else {
            sendMsg(channel, WebSocektAdapter.buildInvalidTokenResp());
        }
    }

    @Override
    public void remove(Channel channel) {
        WSChannelExtraDTO wsChannelExtraDTO = ONLINE_WS_MAP.get(channel);
        if (Objects.nonNull(wsChannelExtraDTO)){
            ONLINE_WS_MAP.remove(channel);
            Long uid = wsChannelExtraDTO.getUid();
            CopyOnWriteArrayList<Channel> channels = ONLINE_UID_MAP.get(uid);
            if (channels.size() == 1) {
                ONLINE_UID_MAP.remove(uid);
            }else {
                channels.remove(channel);
            }
            RedisUtils.del(RedisKey.getKey(RedisKey.USER_TOKEN_STRING, uid));
        }

        // todo 用户下线
    }

    @Override
    public void sendMsgToAll(WSBaseResp<?> msg) {
        ONLINE_WS_MAP.forEach(((channel, ext) -> {
            threadPoolTaskExecutor.execute(() -> sendMsg(channel, msg));
        }));
    }

    @Override
    public void sendToUid(WSBaseResp<?> wsBaseMsg, Long uid) {
        CopyOnWriteArrayList<Channel> channels = ONLINE_UID_MAP.get(uid);
        if (CollectionUtil.isEmpty(channels)){
            log.info("用户：{}不在线", uid);
            return;
        }
        channels.forEach(channel -> {
            threadPoolTaskExecutor.execute(() -> sendMsg(channel, wsBaseMsg));
        });
    }

    @Override
    public void sendToAllOnline(WSBaseResp<?> wsBaseMsg, Long skipUid) {
        ONLINE_WS_MAP.forEach((channel, ext) -> {
            if (Objects.nonNull(skipUid) && Objects.equals(ext.getUid(), skipUid)) {
                return;
            }
            threadPoolTaskExecutor.execute(() -> sendMsg(channel, wsBaseMsg));
        });
    }

    @Override
    public Long getOnLineUserMap(Channel channel) {
        return ONLINE_WS_MAP.get(channel).getUid();
    }
}
