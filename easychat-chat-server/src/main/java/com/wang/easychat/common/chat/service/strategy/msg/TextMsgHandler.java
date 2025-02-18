package com.wang.easychat.common.chat.service.strategy.msg;

import cn.hutool.core.collection.CollectionUtil;
import com.wang.easychat.common.chat.domain.entity.Message;
import com.wang.easychat.common.chat.domain.entity.msg.MessageExtra;
import com.wang.easychat.common.chat.domain.enums.MessageStatusEnum;
import com.wang.easychat.common.chat.domain.enums.MessageTypeEnum;
import com.wang.easychat.common.chat.domain.vo.req.ChatMessageReq;
import com.wang.easychat.common.chat.domain.vo.req.msg.TextMsgReq;
import com.wang.easychat.common.chat.domain.vo.resp.msg.TextMsgResp;
import com.wang.easychat.common.chat.service.ChatService;
import com.wang.easychat.common.chat.service.IMessageService;
import com.wang.easychat.common.chat.service.adapter.MessageAdapter;
import com.wang.easychat.common.chat.service.cache.MsgCache;
import com.wang.easychat.common.chatai.domain.dto.DeepSeekRequest;
import com.wang.easychat.common.chatai.domain.dto.DeepSeekResponse;
import com.wang.easychat.common.chatai.service.DeepSeekService;
import com.wang.easychat.common.common.config.ThreadPoolConfig;
import com.wang.easychat.common.common.domain.enums.YesOrNoEnum;
import com.wang.easychat.common.common.utils.AssertUtil;
import com.wang.easychat.common.common.utils.discover.PrioritizedUrlDiscover;
import com.wang.easychat.common.common.utils.discover.domain.UrlInfo;
import com.wang.easychat.common.user.domain.entity.User;
import com.wang.easychat.common.user.domain.enums.RoleEnum;
import com.wang.easychat.common.user.service.IRoleService;
import com.wang.easychat.common.user.service.cache.UserCache;
import com.wang.easychat.common.user.service.cache.UserInfoCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassDescription: 普通文本消息
 * @Author:Wangzd
 * @Date: 2024/12/1
 **/
@Component
public class TextMsgHandler extends AbstractMsgHandler<TextMsgReq> {

    @Autowired
    private IMessageService messageService;
    @Autowired
    private MsgCache msgCache;
    @Autowired
    private UserInfoCache userInfoCache;
    @Autowired
    private IRoleService roleService;
    @Value("${chatai.deepseek.api.AIUserId}")
    private Long AIUserId;

    @Autowired
    @Qualifier("aichatExecutor")
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;
    @Autowired
    private DeepSeekService deepSeekService;
    @Autowired
    private ChatService chatService;

    private static final PrioritizedUrlDiscover URL_TITLE_DISCOVER = new PrioritizedUrlDiscover();

    /**
     * 消息类型
     */
    @Override
    MessageTypeEnum getMsgTypeEnum() {
        return MessageTypeEnum.TEXT;
    }

    /**
     *
     */
    @Override
    protected void checkMsg(TextMsgReq body, Long roomId, Long uid) {
        // 检验回复消息
        if (Objects.nonNull(body.getReplyMsgId())){
            Message replyMsg = messageService.getById(body.getReplyMsgId());
            AssertUtil.isNotEmpty(replyMsg, "回复消息不存在");
            AssertUtil.equal(replyMsg.getRoomId(), roomId, "只能回复相同会话下消息");
        }
        if (CollectionUtil.isNotEmpty(body.getAtUidList())) {
            // 前端传入的@列表去重
            List<Long> atUidList = body.getAtUidList().stream().distinct().collect(Collectors.toList());
            Map<Long, User> userBatch = userInfoCache.getBatch(atUidList);
            // 如果@用户不存在则过滤
            long batchCount = atUidList.stream().filter(Objects::nonNull).count();
            AssertUtil.equal((long) atUidList.size(), batchCount, "@用户不存在");
            boolean atAll = body.getAtUidList().contains(0L);

            if (atAll) {
                AssertUtil.isTrue(roleService.hasPower(uid, RoleEnum.CHAT_MANAGER), "没有权限");
            }

        }
    }

    /**
     * ai回复拓展逻辑
     * @param body
     * @param uid
     * @param msgId
     * @param roomId
     */
    @Override
    protected void chatAi(TextMsgReq body, Long uid, Long msgId, Long roomId) {
        List<Long> atUidList = body.getAtUidList();
        boolean atRobot = false;
        if (atUidList.contains(AIUserId)){
            atRobot = true;
            atUidList.remove(AIUserId);
        }

        String question = body.getContent();
        if (atRobot){
            DeepSeekRequest request = new DeepSeekRequest();
            request.setModel("deepseek-chat"); // 根据实际模型名称修改
            DeepSeekRequest.Message msg = new DeepSeekRequest.Message("user", question);
            ArrayList<DeepSeekRequest.Message> messages = new ArrayList<>();
            messages.add(msg);
            request.setMessages(messages);


            threadPoolTaskExecutor.execute(() -> {
                ResponseEntity<DeepSeekResponse> response = deepSeekService.getCompletion(request);
                String answer;
                if (response.getStatusCode().is2xxSuccessful() &&
                        response.getBody() != null &&
                        !response.getBody().getChoices().isEmpty()) {
                    answer = response.getBody().getChoices().get(0).getMessage().getContent();
                }
                answer = "服务器开小差啦~稍等片刻再重试一下吧~";
                chatService.sendMsg(new ChatMessageReq(roomId, MessageTypeEnum.TEXT.getType(), new TextMsgReq(answer, msgId, Collections.singletonList(uid)) ), AIUserId);

            });
        }
    }

    /**
     * 子类拓展保存消息逻辑
     *
     * @param msg
     * @param body
     */
    @Override
    protected void saveMsg(Message msg, TextMsgReq body) {
        // 插入文本内容
        MessageExtra extra = Optional.ofNullable(msg.getExtra()).orElse(new MessageExtra());
        Message update = new Message();
        update.setId(msg.getId());
        // todo content铭感词监测
        update.setContent(body.getContent());
        update.setExtra(extra);
        // 如果有回复信息
        if (Objects.nonNull(body.getReplyMsgId())) {
            // todo 将gapCount收录到extra中
            Integer gapCount = messageService.getGapCount(msg.getRoomId(), body.getReplyMsgId(), msg.getId());
            update.setGapCount(gapCount);
            update.setReplyMsgId(body.getReplyMsgId());
        }
        // 艾特功能
        if (CollectionUtil.isNotEmpty(body.getAtUidList())) {
            extra.setAtUidList(body.getAtUidList());
        }
        // 解析并设置文本内容中的网址需要跳转的位置
        Map<String, UrlInfo> urlContentMap = URL_TITLE_DISCOVER.getUrlContentMap(body.getContent());
        extra.setUrlContentMap(urlContentMap);
        messageService.updateById(update);
    }

    /**
     * 展示消息
     * @param msg
     */
    @Override
    public Object showMsg(Message msg) {
        TextMsgResp resp = new TextMsgResp();
        resp.setContent(msg.getContent());
        resp.setUrlContentMap(Optional.ofNullable(msg.getExtra()).map(MessageExtra::getUrlContentMap).orElse(null));
        resp.setAtUidList(Optional.ofNullable(msg.getExtra()).map(MessageExtra::getAtUidList).orElse(null));

        // 回复的信息
        Optional<Message> reply = Optional.ofNullable(msg.getReplyMsgId())
                .map(msgCache::getMsg)
                .filter(a -> Objects.equals(a.getStatus(), MessageStatusEnum.NORMAL.getStatus()));

        if (reply.isPresent()){
            Message replyMessage = reply.get();
            TextMsgResp.ReplyMsg replyMsgVO = new TextMsgResp.ReplyMsg();
            replyMsgVO.setId(replyMessage.getId());
            replyMsgVO.setUid(replyMessage.getFromUid());
            replyMsgVO.setType(replyMessage.getType());
            replyMsgVO.setBody(MsgHandlerFactory.getStrategyNoNull(replyMessage.getType()).showReplyMsg(replyMessage));
            User replyUser = userInfoCache.get(reply.get().getFromUid());
            replyMsgVO.setUsername(replyUser.getName());
            // 间隔小于100条才可跳转
            replyMsgVO.setCanCallback(YesOrNoEnum.toStatus(Objects.nonNull(msg.getGapCount()) && msg.getGapCount() <= MessageAdapter.CAN_CALLBACK_GAP_COUNT));
            replyMsgVO.setGapCount(msg.getGapCount());
            resp.setReply(replyMsgVO);
        }

        return resp;
    }

    /**
     * 展示被回复的消息
     *
     * @param msg
     */
    @Override
    public Object showReplyMsg(Message msg) {
        return msg.getContent();
    }

    /**
     * 会话列表消息
     *
     * @param msg
     */
    @Override
    public String showContactMsg(Message msg) {
        return msg.getContent();
    }


}
