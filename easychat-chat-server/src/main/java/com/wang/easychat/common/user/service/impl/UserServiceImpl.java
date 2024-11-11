package com.wang.easychat.common.user.service.impl;

import com.wang.easychat.common.user.domain.entity.User;
import com.wang.easychat.common.user.mapper.UserMapper;
import com.wang.easychat.common.user.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author wang
 * @since 2024-11-08
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

}
