package com.wang.easychat.common.user.service;

import com.wang.easychat.common.user.domain.entity.Role;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wang.easychat.common.user.domain.enums.RoleEnum;

/**
 * <p>
 * 角色表 服务类
 * </p>
 *
 * @author wang
 * @since 2024-11-19
 */
public interface IRoleService extends IService<Role> {
    /**
     * 是否拥有某个权限 临时写法
     */
    boolean hasPower(Long uid, RoleEnum roleEnum);
}
