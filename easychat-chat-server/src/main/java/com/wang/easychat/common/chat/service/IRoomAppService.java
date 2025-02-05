package com.wang.easychat.common.chat.service;

import com.wang.easychat.common.chat.domain.vo.req.*;
import com.wang.easychat.common.chat.domain.vo.resp.ChatRoomResp;
import com.wang.easychat.common.chat.domain.vo.resp.MemberResp;
import com.wang.easychat.common.common.domain.vo.req.CursorPageBaseReq;
import com.wang.easychat.common.common.domain.vo.resp.CursorPageBaseResp;
import com.wang.easychat.common.websocket.domain.vo.resp.ChatMemberResp;

import java.util.List;

/**
 * @ClassDescription: 会话列表业务类
 * @Author:Wangzd
 * @Date: 2024/12/10
 **/
public interface IRoomAppService {
    /**
     * 游标翻页方式，获取会话列表
     */
    CursorPageBaseResp<ChatRoomResp> getContactPage(CursorPageBaseReq request, Long uid);

    /**
     * 查找会话信息
     */
    ChatRoomResp getContactDetailByFriend(Long uid, Long friendUid);

    /**
     * 返回会话消息信息
     * @param uid
     * @param id
     * @return
     */
    ChatRoomResp getContactDetail(Long uid, Long id);

    /**
     * 创建群组
     * @param uid
     * @param request
     * @return
     */
    Long addGroup(Long uid, GroupAddReq request);

    /**
     * 获取群组信息
     * @param uid
     * @param roomId
     * @return
     */
    MemberResp getGroupDetail(Long uid, long roomId);

    /**
     * 邀请好友
     * @param uid
     * @param request
     */
    void addMember(Long uid, MemberAddReq request);

    /**
     *
     * @param request
     * @return
     */
    List<ChatMemberListResp> getMemberList(ChatMessageMemberReq request);

    /**
     * 查询成员列表
     * @param request
     * @return
     */
    CursorPageBaseResp<ChatMemberResp> getMemberPage(MemberReq request);

    /**
     * 移除群成员
     * @param uid
     * @param request
     */
    void delMember(Long uid, MemberDelReq request);
}
