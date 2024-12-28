package com.wang.easychat.common.chat.service;

import com.wang.easychat.common.chat.domain.entity.GroupMember;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wang.easychat.common.chat.domain.vo.req.MemberExitReq;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 群成员表 服务类
 * </p>
 *
 * @author wang
 * @since 2024-11-29
 */
public interface IGroupMemberService extends IService<GroupMember> {
    /**
     * 查询群成员
     */
    GroupMember getMember(Long groupId, Long uid);

    /**
     * 查询群组所有成员
     */
    List<Long> getMemberUidList(Long groupId);

    /**
     * 查询群组传入的成员
     */
    List<Long> getMemberUidList(Long groupId, List<Long> uidList);


    GroupMember getByUid(Long uid);

    GroupMember getByUidAndGroupId(Long uid, Long groupId);

    /**
     * 退出群聊
     * @param uid
     * @param request
     */
    void exitGroup(Long uid, MemberExitReq request);

    /**
     * 根据id删除群成员
     * @param groupId
     * @param uidList
     * @return
     */
    Boolean removeByGroupId(Long groupId, List<Object> uidList);

    /**
     * 查询成员的身份
     * @param groupId
     * @param uidList
     * @return
     */
    Map<Long, Integer> getMemberMapRole(Long groupId, List<Long> uidList);
}
