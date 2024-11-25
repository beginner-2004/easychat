package com.wang.easychat.common.user.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wang.easychat.common.user.domain.entity.UserApply;
import com.wang.easychat.common.user.domain.enums.ApplyStatusEnum;
import com.wang.easychat.common.user.domain.enums.ApplyTypeEnum;
import com.wang.easychat.common.user.domain.enums.ApplyReadStatusEnum;
import com.wang.easychat.common.user.mapper.UserApplyMapper;
import com.wang.easychat.common.user.service.IUserApplyService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 用户申请表 服务实现类
 * </p>
 *
 * @author wang
 * @since 2024-11-25
 */
@Service
public class UserApplyServiceImpl extends ServiceImpl<UserApplyMapper, UserApply> implements IUserApplyService {

    @Override
    public IPage<UserApply> friendApplyPage(Long uid, Page page) {
        return lambdaQuery()
                .eq(UserApply::getTargetId, uid)
                .eq(UserApply::getType, ApplyTypeEnum.ADD_FRIEND.getType())
                .orderByDesc(UserApply::getCreateTime)
                .page(page);
    }

    /**
     * 将传入申请标为已读
     */
    @Override
    public void readApples(Long uid, List<Long> applyIds) {
        lambdaUpdate()
                .eq(UserApply::getTargetId, uid)
                .eq(UserApply::getReadStatus, ApplyReadStatusEnum.UNREAD.getCode())
                .in(UserApply::getId, applyIds)
                .set(UserApply::getReadStatus, ApplyReadStatusEnum.READ.getCode())
                .update();
    }

    /**
     * 获取 uid用户 的未读好友申请数量
     */
    @Override
    public Integer getUnReadCount(Long uid) {
        return lambdaQuery()
                .eq(UserApply::getTargetId, uid)
                .eq(UserApply::getReadStatus, ApplyReadStatusEnum.UNREAD.getCode())
                .eq(UserApply::getType, ApplyTypeEnum.ADD_FRIEND.getType())
                .count();
    }

    /**
     * 查找 targetUid用户 是否有待审批 uid用户 的好友申请
     * @param uid
     * @param targetUid
     * @return
     */
    @Override
    public UserApply getFriendApproving(Long uid, Long targetUid) {
        return lambdaQuery()
                .eq(UserApply::getTargetId, targetUid)
                .eq(UserApply::getUid, uid)
                .eq(UserApply::getType, ApplyTypeEnum.ADD_FRIEND.getType())
                .eq(UserApply::getStatus, ApplyStatusEnum.WAIT_APPROVAL.getCode())
                .one();
    }

    /**
     * 同意好友申请
     * @param applyId
     */
    @Override
    public void agree(Long applyId) {
        lambdaUpdate()
                .eq(UserApply::getId, applyId)
                .set(UserApply::getStatus, ApplyStatusEnum.AGREE.getCode())
                .update();
    }
}
