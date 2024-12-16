package com.wang.easychat.common.chat.service;

import com.wang.easychat.common.chat.domain.entity.GroupMember;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

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

    GroupMember getByUid(Long uid);

    GroupMember getByUidAndGroupId(Long uid, Long groupId);

}
