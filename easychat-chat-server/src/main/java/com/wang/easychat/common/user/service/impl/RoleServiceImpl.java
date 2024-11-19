package com.wang.easychat.common.user.service.impl;

import com.wang.easychat.common.user.domain.entity.Role;
import com.wang.easychat.common.user.domain.enums.RoleEnum;
import com.wang.easychat.common.user.mapper.RoleMapper;
import com.wang.easychat.common.user.service.IRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wang.easychat.common.user.service.cache.UserCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * <p>
 * 角色表 服务实现类
 * </p>
 *
 * @author wang
 * @since 2024-11-19
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements IRoleService {
    @Autowired
    private UserCache userCache;


    /**
     * 是否拥有某个权限 临时写法
     */
    @Override
    public boolean hasPower(Long uid, RoleEnum roleEnum) {
        Set<Long> roleSet = userCache.getRoleSetByUid(uid);
        return isAdmin(roleSet) || roleSet.contains((roleEnum.getId()));
    }

    private boolean isAdmin(Set<Long> roleSet){
        return roleSet.contains(RoleEnum.ADMIN.getId());
    }
}
