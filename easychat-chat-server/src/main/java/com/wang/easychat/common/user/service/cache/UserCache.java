package com.wang.easychat.common.user.service.cache;

import com.wang.easychat.common.user.domain.entity.ItemConfig;
import com.wang.easychat.common.user.domain.entity.UserRole;
import com.wang.easychat.common.user.service.IItemConfigService;
import com.wang.easychat.common.user.service.IUserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @ClassDescription: 用户相关缓存
 * @Author:Wangzd
 * @Date: 2024/11/16
 **/
@Component
public class UserCache {
    @Autowired
    private IUserRoleService userRoleService;

    @Cacheable(cacheNames = "user", key = "'roles:' + #uid")
    public Set<Long> getRoleSetByUid(Long uid) {
        List<UserRole> userRoles = userRoleService.listByUid(uid);
        return userRoles.stream()
                .map(UserRole::getRoleId)
                .collect(Collectors.toSet());
    }
}
