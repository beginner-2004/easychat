package com.wang.easychat.common.user.service;

import com.wang.easychat.common.common.domain.vo.req.CursorPageBaseReq;
import com.wang.easychat.common.common.domain.vo.req.PageBaseReq;
import com.wang.easychat.common.common.domain.vo.resp.CursorPageBaseResp;
import com.wang.easychat.common.common.domain.vo.resp.PageBaseResp;
import com.wang.easychat.common.user.domain.entity.UserFriend;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wang.easychat.common.user.domain.vo.req.friend.FriendApplyReq;
import com.wang.easychat.common.user.domain.vo.req.friend.FriendApproveReq;
import com.wang.easychat.common.user.domain.vo.resp.friend.FriendApplyResp;
import com.wang.easychat.common.user.domain.vo.resp.friend.FriendResp;
import com.wang.easychat.common.user.domain.vo.resp.friend.FriendUnreadResp;

/**
 * <p>
 * 用户联系人表 服务类
 * </p>
 *
 * @author wang
 * @since 2024-11-25
 */
public interface IUserFriendService extends IService<UserFriend> {

    /**
     * 分页查询uid用户的联系人列表
     */
    CursorPageBaseResp<FriendResp> friendList(Long uid, CursorPageBaseReq request);

    /**
     * 查询uid用户的好友申请列表
     */
    PageBaseResp<FriendApplyResp> pageApplyFriend(Long uid, PageBaseReq request);

    /**
     * 获取 uid用户 的未读好友申请
     * @param uid
     * @return
     */
    FriendUnreadResp unread(Long uid);

    /**
     * 添加好友
     * @param uid
     * @param request
     */
    void apply(Long uid, FriendApplyReq request);

    void applyApprove(Long uid, FriendApproveReq friendApproveReq);

}
