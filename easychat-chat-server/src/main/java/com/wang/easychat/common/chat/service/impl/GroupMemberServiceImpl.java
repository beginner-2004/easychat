package com.wang.easychat.common.chat.service.impl;

import com.wang.easychat.common.chat.domain.entity.GroupMember;
import com.wang.easychat.common.chat.mapper.GroupMemberMapper;
import com.wang.easychat.common.chat.service.IGroupMemberService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 群成员表 服务实现类
 * </p>
 *
 * @author wang
 * @since 2024-11-29
 */
@Service
public class GroupMemberServiceImpl extends ServiceImpl<GroupMemberMapper, GroupMember> implements IGroupMemberService {

    /**
     * 查询群成员
     */
    @Override
    public GroupMember getMember(Long groupId, Long uid) {
        return lambdaQuery()
                .eq(GroupMember::getGroupId, groupId)
                .eq(GroupMember::getUid, uid)
                .one();
    }
}
