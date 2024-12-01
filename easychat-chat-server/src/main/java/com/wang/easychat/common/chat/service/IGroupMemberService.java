package com.wang.easychat.common.chat.service;

import com.wang.easychat.common.chat.domain.entity.GroupMember;
import com.baomidou.mybatisplus.extension.service.IService;

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
    GroupMember getMember(Long id, Long uid);
}
