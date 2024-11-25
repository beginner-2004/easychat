package com.wang.easychat.common.user.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wang.easychat.common.user.domain.entity.UserApply;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 用户申请表 服务类
 * </p>
 *
 * @author wang
 * @since 2024-11-25
 */
public interface IUserApplyService extends IService<UserApply> {

    /**
     * 分页查找对 uid用户 的好友申请
     */
    IPage<UserApply> friendApplyPage(Long uid, Page plusPage);

    /**
     * 将传入申请标为已读
     */
    void readApples(Long uid, List<Long> applyIds);

    /**
     * 获取 uid用户 的未读好友申请数量
     */
    Integer getUnReadCount(Long uid);

    /**
     * 查找 uid用户 是否有待审批 targetUid用户 的好友申请
     * @param uid
     * @param targetUid
     * @return
     */
    UserApply getFriendApproving(Long uid, Long targetUid);

    /**
     * 同意好友申请
     * @param applyId
     */
    void agree(Long applyId);
}
