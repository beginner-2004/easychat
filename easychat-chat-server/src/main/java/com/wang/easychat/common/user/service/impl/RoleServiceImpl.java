package com.wang.easychat.common.user.service.impl;

import com.wang.easychat.common.user.domain.entity.Role;
import com.wang.easychat.common.user.mapper.RoleMapper;
import com.wang.easychat.common.user.service.IRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

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

}
