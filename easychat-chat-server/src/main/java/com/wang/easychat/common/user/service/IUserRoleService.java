package com.wang.easychat.common.user.service;

import com.wang.easychat.common.user.domain.entity.UserRole;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 用户角色关系表 服务类
 * </p>
 *
 * @author wang
 * @since 2024-11-19
 */
public interface IUserRoleService extends IService<UserRole> {

    List<UserRole> listByUid(Long uid);
}
