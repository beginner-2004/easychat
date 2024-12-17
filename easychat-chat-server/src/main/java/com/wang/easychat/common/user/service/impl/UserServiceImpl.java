package com.wang.easychat.common.user.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.wang.easychat.common.common.annotation.RedissonLock;
import com.wang.easychat.common.common.domain.enums.NormalOrNoEnum;
import com.wang.easychat.common.common.domain.enums.YesOrNoEnum;
import com.wang.easychat.common.common.event.UserBlackEvent;
import com.wang.easychat.common.common.event.UserRegisterEvent;
import com.wang.easychat.common.common.utils.AssertUtil;
import com.wang.easychat.common.user.domain.dto.ItemInfoDTO;
import com.wang.easychat.common.user.domain.dto.SummeryInfoDTO;
import com.wang.easychat.common.user.domain.entity.*;
import com.wang.easychat.common.user.domain.enums.BlackTypeEnum;
import com.wang.easychat.common.user.domain.enums.ChatActiveStatusEnum;
import com.wang.easychat.common.user.domain.enums.ItemEnum;
import com.wang.easychat.common.user.domain.enums.ItemTypeEnum;
import com.wang.easychat.common.user.domain.vo.req.user.BlackReq;
import com.wang.easychat.common.user.domain.vo.req.user.ItemInfoReq;
import com.wang.easychat.common.user.domain.vo.req.user.SummeryInfoReq;
import com.wang.easychat.common.user.domain.vo.resp.user.BadgeResp;
import com.wang.easychat.common.user.domain.vo.resp.user.UserInfoResp;
import com.wang.easychat.common.user.mapper.UserMapper;
import com.wang.easychat.common.user.service.IBlackService;
import com.wang.easychat.common.user.service.IItemConfigService;
import com.wang.easychat.common.user.service.IUserBackpackService;
import com.wang.easychat.common.user.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wang.easychat.common.user.service.adapter.UserAdapter;
import com.wang.easychat.common.user.service.cache.ItemCache;
import com.wang.easychat.common.user.service.cache.UserCache;
import com.wang.easychat.common.user.service.cache.UserSummaryCache;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author wang
 * @since 2024-11-08
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Autowired
    private IUserBackpackService userBackpackService;
    @Autowired
    private ItemCache itemCache;
    @Autowired
    private IItemConfigService itemConfigService;
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;
    @Autowired
    private IBlackService blackService;
    @Autowired
    private UserCache userCache;
    @Autowired
    private UserSummaryCache userSummaryCache;

    public User getByOpenId(String openId) {
        return lambdaQuery()
                .eq(User::getOpenId, openId)
                .one();
    }

    @Override
    @Transactional
    public Long register(User insert) {
        save(insert);
        // 用户注册事件
        applicationEventPublisher.publishEvent(new UserRegisterEvent(this, insert));
        return insert.getId();

    }

    @Override
    public UserInfoResp getUserInfo(Long uid) {
        User user = getById(uid);
        Integer modifyNameCount = userBackpackService.getCountByValidItemId(uid, ItemEnum.MODIFY_NAME_CARD.getId());
        return UserAdapter.buildUserInfo(user, modifyNameCount);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @RedissonLock(key = "#uid")
    public void modifyName(Long uid, String name) {
        User oldUser = getbyName(name);
        AssertUtil.isEmpty(oldUser, "名字已经被抢占了，请换一个！");
        UserBackpack modifyNameItem = userBackpackService.getFirstValidItem(uid, ItemEnum.MODIFY_NAME_CARD.getId());
        AssertUtil.isNotEmpty(modifyNameItem, "改名卡数量不足，等待后续活动发放改名卡吧");
        // 使用改名卡
        // 数据库级别乐观锁
        boolean isSuccess = userBackpackService.useItem(modifyNameItem);
        if (isSuccess) {
            // 改名
            lambdaUpdate()
                    .eq(User::getId, uid)
                    .set(User::getName, name)
                    .update();
        }
    }

    @Override
    public List<BadgeResp> badges(Long uid) {
        // 查询所有徽章
        List<ItemConfig> itemConfigs = itemCache.getByType(ItemTypeEnum.BADGE.getType());
        // 查询用户拥有的徽章
        List<UserBackpack> backpacks = userBackpackService.getByItemIds(uid,
                itemConfigs.stream().map(ItemConfig::getId).collect(Collectors.toList()));
        // 用户当前佩戴的徽章
        User user = getById(uid);
        return UserAdapter.buidBadgeResp(itemConfigs, backpacks, user);
    }

    @Override
    public void wearingBadge(Long uid, Long itemId) {
        // 确保有徽章
        UserBackpack firstValidItem = userBackpackService.getFirstValidItem(uid, itemId);
        AssertUtil.isNotEmpty(firstValidItem, "你还没有这个徽章，快去获得吧");
        // 确保这个物品是徽章
        ItemConfig itemConfig = itemConfigService.getById(firstValidItem.getItemId());
        AssertUtil.equal(itemConfig.getType(), ItemTypeEnum.BADGE.getType(), "只有徽章才能佩戴");

        lambdaUpdate()
                .eq(User::getId, uid)
                .set(User::getItemId, itemId)
                .update();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void black(BlackReq req) {
        Long uid = req.getUid();
        Black blackUser = new Black();
        blackUser.setType(BlackTypeEnum.UID.getType());
        blackUser.setTarget(uid.toString());
        blackService.save(blackUser);
        User byId = getById(uid);
        blackIp(Optional.ofNullable(byId.getIpInfo()).map(IpInfo::getCreateIp).orElse(null));
        blackIp(Optional.ofNullable(byId.getIpInfo()).map(IpInfo::getUpdateIp).orElse(null));

        applicationEventPublisher.publishEvent(new UserBlackEvent(this, byId));
    }

    @Override
    public void invalidUid(Long id) {
        lambdaUpdate()
                .eq(User::getId, id)
                .set(User::getStatus, YesOrNoEnum.YES.getStatus())
                .update();
    }

    @Override
    public List<User> getFriendList(List<Long> friendUids) {
        return lambdaQuery()
                .in(User::getId, friendUids)
                .select(User::getId, User::getActiveStatus)
                .list();
    }

    @Override
    public List<User> getByIds(List<Long> userIds) {
        return lambdaQuery()
                .in(User::getId, userIds)
                .list();
    }

    @Override
    public void setUserActiveStatus(Long uid, Integer status) {
        lambdaUpdate()
                .eq(User::getId, uid)
                .set(User::getActiveStatus, status)
                .update();
    }

    /**
     * 获取用户汇总信息
     */
    @Override
    public List<SummeryInfoDTO> getSummeryUserInfo(SummeryInfoReq req) {
        // 获取前端需要同步的uid
        List<Long> uidList = getNeedSyncUidList(req.getReqList());
        // 加载用户信息
        Map<Long, SummeryInfoDTO> batch = userSummaryCache.getBatch(uidList);
        return req.getReqList().stream()
                .map(a -> batch.containsKey(a.getUid()) ? batch.get(a.getUid()) : SummeryInfoDTO.skip(a.getUid()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemInfoDTO> getItemInfo(ItemInfoReq req) { // item相关数据较少，简单做
        return req.getReqList().stream().map(a -> {
            ItemConfig itemConfig = itemCache.getById(a.getItemId());
            if (Objects.nonNull(a.getLastModifyTime()) && a.getLastModifyTime() >= itemConfig.getUpdateTime().getTime()) {
                return ItemInfoDTO.skip(a.getItemId());
            }
            ItemInfoDTO dto = new ItemInfoDTO();
            dto.setItemId(itemConfig.getId());
            dto.setImg(itemConfig.getImg());
            dto.setDescribe(itemConfig.getDescribe());
            return dto;
        }).collect(Collectors.toList());
    }

    /**
     * 获取传入列表中在线人数
     *
     * @param memberUidList
     * @return
     */
    @Override
    public Integer getOnlineCount(List<Long> memberUidList) {
        return lambdaQuery()
                .eq(User::getActiveStatus, ChatActiveStatusEnum.ONLINE.getStatus())
                .in(CollectionUtil.isNotEmpty(memberUidList), User::getId, memberUidList)
                .count();
    }

    /**
     * 查找用户
     */
    @Override
    public List<User> getMemberList() {
        return lambdaQuery()
                .eq(User::getStatus, NormalOrNoEnum.NORMAL.getStatus())
                .orderByDesc(User::getLastOptTime)  //最近活跃的1000个人，可以用lastOptTime字段，但是该字段没索引，updateTime可平替
                .last("limit 1000") //毕竟是大群聊，人数需要做个限制
                .select(User::getId, User::getName, User::getAvatar)
                .list();
    }

    private List<Long> getNeedSyncUidList(List<SummeryInfoReq.infoReq> reqList) {
        List<Long> needSyncUidList = new ArrayList<>();
        List<Long> userModifyTime = userCache.getUserModifyTime(reqList.stream().map(SummeryInfoReq.infoReq::getUid).collect(Collectors.toList()));
        for (int i = 0; i < reqList.size(); i++) {
            SummeryInfoReq.infoReq infoReq = reqList.get(i);
            Long modifyTime = userModifyTime.get(i);
            // 筛选需要刷新信息的用户
            if (Objects.isNull(infoReq.getLastModifyTime()) || (Objects.nonNull(modifyTime) && modifyTime > infoReq.getLastModifyTime())){
                needSyncUidList.add(infoReq.getUid());
            }
        }
        return needSyncUidList;
    }

    /**
     * 拉黑ip
     * @param ip
     */
    private void blackIp(String ip) {
        if (StringUtils.isBlank(ip)){
            return;
        }
        try {
            Black blackIp = new Black();
            blackIp.setType(BlackTypeEnum.IP.getType());
            blackIp.setTarget(ip);
            blackService.save(blackIp);
        }catch (Exception e){
            log.error("duplicate black ip:{}", ip);
        }
    }

    private User getbyName(String name) {
        return lambdaQuery().eq(User::getName, name).one();
    }

}

