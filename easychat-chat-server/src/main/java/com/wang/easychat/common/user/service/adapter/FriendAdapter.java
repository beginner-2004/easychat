package com.wang.easychat.common.user.service.adapter;

import com.wang.easychat.common.user.domain.entity.User;
import com.wang.easychat.common.user.domain.entity.UserApply;
import com.wang.easychat.common.user.domain.entity.UserFriend;
import com.wang.easychat.common.user.domain.enums.ApplyReadStatusEnum;
import com.wang.easychat.common.user.domain.enums.ApplyStatusEnum;
import com.wang.easychat.common.user.domain.enums.ApplyTypeEnum;
import com.wang.easychat.common.user.domain.vo.req.friend.FriendApplyReq;
import com.wang.easychat.common.user.domain.vo.resp.friend.FriendApplyResp;
import com.wang.easychat.common.user.domain.vo.resp.friend.FriendResp;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @ClassDescription: 联系人包装器
 * @Author:Wangzd
 * @Date: 2024/11/25
 **/
public class FriendAdapter {

    /**
     *
     * @param list  UserFriend表信息
     * @param friendList 朋友的信息 （id 昵称 头像)
     * @return
     */
    public static List<FriendResp> buildFriend(List<UserFriend> list, List<User> friendList) {
        Map<Long, User> userMap = friendList.stream().collect(Collectors.toMap(User::getId, user -> user));
        return list.stream()
                .map(userFriend -> {
                    FriendResp friendResp = new FriendResp();
                    friendResp.setId(userFriend.getId());
                    User user = userMap.get(userFriend.getFriendUid());
                    if (Objects.nonNull(user)){
                        friendResp.setActiveStatus(user.getActiveStatus());
                    }
                    return friendResp;
                }).collect(Collectors.toList());
    }

    public static List<FriendApplyResp> buildFriendApplyList(List<UserApply> records) {
        return records.stream().map(userApply -> {
            FriendApplyResp friendApplyResp = new FriendApplyResp();
            friendApplyResp.setUid(userApply.getUid());
            friendApplyResp.setType(userApply.getType());
            friendApplyResp.setApplyId(userApply.getId());
            friendApplyResp.setMsg(userApply.getMsg());
            friendApplyResp.setStatus(userApply.getStatus());
            return friendApplyResp;
        }).collect(Collectors.toList());
    }

    public static UserApply buildFriendApply(Long uid, FriendApplyReq request) {
        UserApply userApplyNew = new UserApply();
        userApplyNew.setUid(uid);
        userApplyNew.setMsg(request.getMsg());
        userApplyNew.setType(ApplyTypeEnum.ADD_FRIEND.getType());
        userApplyNew.setTargetId(request.getTargetUid());
        userApplyNew.setStatus(ApplyStatusEnum.WAIT_APPROVAL.getCode());
        userApplyNew.setReadStatus(ApplyReadStatusEnum.UNREAD.getCode());
        return userApplyNew;
    }
}
