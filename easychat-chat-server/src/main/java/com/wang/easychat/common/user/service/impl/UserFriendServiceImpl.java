package com.wang.easychat.common.user.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.google.common.collect.Lists;
import com.wang.easychat.common.chat.service.ChatService;
import com.wang.easychat.common.chat.service.adapter.MessageAdapter;
import com.wang.easychat.common.common.annotation.RedissonLock;
import com.wang.easychat.common.common.domain.vo.req.CursorPageBaseReq;
import com.wang.easychat.common.common.domain.vo.req.PageBaseReq;
import com.wang.easychat.common.common.domain.vo.resp.CursorPageBaseResp;
import com.wang.easychat.common.common.domain.vo.resp.PageBaseResp;
import com.wang.easychat.common.common.event.FriendApplyEvent;
import com.wang.easychat.common.common.utils.AssertUtil;
import com.wang.easychat.common.common.utils.CursorUtils;
import com.wang.easychat.common.chat.domain.entity.RoomFriend;
import com.wang.easychat.common.user.domain.dto.FriendApplyDTO;
import com.wang.easychat.common.user.domain.entity.User;
import com.wang.easychat.common.user.domain.entity.UserApply;
import com.wang.easychat.common.user.domain.entity.UserFriend;
import com.wang.easychat.common.user.domain.enums.ApplyStatusEnum;
import com.wang.easychat.common.user.domain.vo.req.friend.FriendApplyReq;
import com.wang.easychat.common.user.domain.vo.req.friend.FriendApproveReq;
import com.wang.easychat.common.user.domain.vo.req.friend.FriendCheckReq;
import com.wang.easychat.common.user.domain.vo.resp.friend.FriendApplyResp;
import com.wang.easychat.common.user.domain.vo.resp.friend.FriendCheckResp;
import com.wang.easychat.common.user.domain.vo.resp.friend.FriendResp;
import com.wang.easychat.common.user.domain.vo.resp.friend.FriendUnreadResp;
import com.wang.easychat.common.user.mapper.UserFriendMapper;
import com.wang.easychat.common.chat.service.IRoomFriendService;
import com.wang.easychat.common.user.service.IUserApplyService;
import com.wang.easychat.common.user.service.IUserFriendService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wang.easychat.common.user.service.IUserService;
import com.wang.easychat.common.user.service.adapter.FriendAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 * 用户联系人表 服务实现类
 * </p>
 *
 * @author wang
 * @since 2024-11-25
 */
@Service
@Slf4j
public class UserFriendServiceImpl extends ServiceImpl<UserFriendMapper, UserFriend> implements IUserFriendService {

    @Autowired
    private IUserService userService;
    @Autowired
    private IUserApplyService userApplyService;
    @Autowired
    private IRoomFriendService roomFriendService;
    @Autowired
    private ChatService chatService;
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;


    /**
     * 分页查询uid用户的联系人列表
     */
    @Override
    public CursorPageBaseResp<FriendResp> friendList(Long uid, CursorPageBaseReq request) {
        CursorPageBaseResp<UserFriend> friendPage = getFriendPage(uid, request);
        if (CollectionUtil.isEmpty(friendPage.getList())){
            return CursorPageBaseResp.empty();
        }

        // 获取所有的联系人id
        List<Long> friendUids = friendPage.getList().stream()
                .map(UserFriend::getFriendUid)
                .collect(Collectors.toList());
        List<User> friendList = getFriendList(friendUids);
        return CursorPageBaseResp.init(friendPage, FriendAdapter.buildFriend(friendPage.getList(), friendList));
    }

    /**
     * 分页查询uid用户的好友申请列表
     */
    @Override
    public PageBaseResp<FriendApplyResp> pageApplyFriend(Long uid, PageBaseReq request) {
        IPage<UserApply> userApplyIPage = userApplyService.friendApplyPage(uid, request.plusPage());
        if (CollectionUtil.isEmpty(userApplyIPage.getRecords())){
            return PageBaseResp.empty();
        }
        readApples(uid, userApplyIPage);

        return PageBaseResp.init(userApplyIPage, FriendAdapter.buildFriendApplyList(userApplyIPage.getRecords()));
    }

    /**
     * 获取 uid用户 的未读好友申请
     *
     * @param uid
     * @return
     */
    @Override
    public FriendUnreadResp unread(Long uid) {
        Integer unReadCount = userApplyService.getUnReadCount(uid);
        return new FriendUnreadResp(unReadCount);
    }

    /**
     * 添加好友
     * @param uid
     * @param request
     */
    @Override
    @RedissonLock(key = "#uid")
    public void apply(Long uid, FriendApplyReq request) {
        // 是否已经是好友
        UserFriend friend = getByFriend(uid, request.getTargetUid());
        AssertUtil.isEmpty(friend, "你们已经是好友了！");
        // 是否有向对方的重复申请
        UserApply selfApproving = userApplyService.getFriendApproving(uid, request.getTargetUid());
        if (Objects.nonNull(selfApproving)){
            log.info("已有好友申请记录, uid:{}, targetId:{}", uid, request.getTargetUid());
            return;
        }

        // 是否有待审批对方向自己的申请记录
        UserApply friendApproving = userApplyService.getFriendApproving(request.getTargetUid(), uid);
        if (Objects.nonNull(friendApproving)) {
            ((IUserFriendService) AopContext.currentProxy()).applyApprove(uid, new FriendApproveReq(friendApproving.getId()));
            return;
        }
        // 入库
        UserApply insert = FriendAdapter.buildFriendApply(uid, request);
        userApplyService.save(insert);
        // todo 创建一个事件，用于推送消息给前端，刷新未读好友申请个数
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @RedissonLock(key = "#uid")
    public void applyApprove(Long uid, FriendApproveReq friendApproveReq) {
        UserApply userApply = userApplyService.getById(friendApproveReq.getApplyId());
        AssertUtil.isNotEmpty(userApply, "不存在申请记录！");
        AssertUtil.equal(userApply.getTargetId(), uid, "不存在申请记录！");
        AssertUtil.equal(userApply.getStatus(), ApplyStatusEnum.WAIT_APPROVAL.getCode(), "已同意好友申请");
        // 同意好友申请
        userApplyService.agree(friendApproveReq.getApplyId());
        // 创建好友关系
        createFriend(uid, userApply.getUid());
        //创建一个聊天房间
        RoomFriend roomFriend = roomFriendService.createFriendRoom(Arrays.asList(uid, userApply.getUid()));
        //发送一条同意消息。。我们已经是好友了，开始聊天吧
        FriendApplyDTO build = FriendApplyDTO.builder()
                .chatMessageReq(MessageAdapter.buildAgreeMsg(roomFriend.getRoomId()))
                .uid(uid)
                .build();
        applicationEventPublisher.publishEvent(new FriendApplyEvent(this, build));
    }

    /**
     * 删除好友
     * @param uid
     * @param friendUid
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteFriend(Long uid, Long friendUid) {
        List<UserFriend> userFriends = getUserFriend(uid, friendUid);
        if (CollectionUtil.isEmpty(userFriends)){
            log.info("没有好友关系:{},{}", uid, friendUid);
            return;
        }
        List<Long> friendRecordIds = userFriends.stream().map(UserFriend::getId).collect(Collectors.toList());
        // 逻辑删除记录
        removeByIds(friendRecordIds);
        // 禁用房间
        roomFriendService.disableFriendRoom(Arrays.asList(uid, friendUid));
    }

    /**
     * 批量查询是否是 uid用户的联系人
     *
     * @param uid
     * @param request
     * @return
     */
    @Override
    public FriendCheckResp check(Long uid, FriendCheckReq request) {
        List<UserFriend> friendList = getByFriends(uid, request.getUidList());
        Set<Long> friendUidSet = friendList.stream().map(UserFriend::getFriendUid).collect(Collectors.toSet());

        List<FriendCheckResp.FriendCheck> friendCheckList = request.getUidList().stream().map(friendUid -> {
            FriendCheckResp.FriendCheck friendCheck = new FriendCheckResp.FriendCheck();
            friendCheck.setUid(friendUid);
            friendCheck.setIsFriend(friendUidSet.contains(friendUid));
            return friendCheck;
        }).collect(Collectors.toList());

        return new FriendCheckResp(friendCheckList);
    }

    /**
     * 根据 id集合查找朋友关系
     */
    private List<UserFriend> getByFriends(Long uid, List<Long> uidList) {
        return lambdaQuery()
                .eq(UserFriend::getUid, uid)
                .in(UserFriend::getFriendUid, uidList)
                .list();
    }

    private List<UserFriend> getUserFriend(Long uid, Long friendUid) {
        return lambdaQuery()
                .eq(UserFriend::getUid, uid)
                .eq(UserFriend::getFriendUid, friendUid)
                .or()
                .eq(UserFriend::getFriendUid, uid)
                .eq(UserFriend::getUid, friendUid)
                .select(UserFriend::getId)
                .list();
    }

    /**
     * 创建好友关系
     */
    private void createFriend(Long uid, Long targetUid) {
        UserFriend userFriend1 = new UserFriend();
        userFriend1.setUid(uid);
        userFriend1.setFriendUid(targetUid);
        UserFriend userFriend2 = new UserFriend();
        userFriend2.setUid(targetUid);
        userFriend2.setFriendUid(uid);
        saveBatch(Lists.newArrayList(userFriend1, userFriend2));
    }


    /**
     * 获取朋友关系
     * @param uid
     * @param targetUid
     * @return
     */
    private UserFriend getByFriend(Long uid, Long targetUid) {
        return lambdaQuery()
                .eq(UserFriend::getUid, uid)
                .eq(UserFriend::getFriendUid, targetUid)
                .one();
    }

    /**
     * 将传入申请信息标为已读
     * @param uid
     * @param userApplyIPage
     */
    private void readApples(Long uid, IPage<UserApply> userApplyIPage) {
        List<Long> applyIds = userApplyIPage.getRecords().stream()
                .map(UserApply::getId)
                .collect(Collectors.toList());

        userApplyService.readApples(uid, applyIds);
    }

    /**
     * 查询用户的所有联系人
     */
    private CursorPageBaseResp<UserFriend> getFriendPage(Long uid, CursorPageBaseReq request) {
        return CursorUtils.getCursorPageByMysql(this, request,
                wrapper -> wrapper.eq(UserFriend::getUid, uid), UserFriend::getUid);
    }

    /**
     * 通过联系人id集合查询对应 id 昵称 头像
     */
    private List<User> getFriendList(List<Long> friendUids) {
        return userService.getFriendList(friendUids);
    }
}
