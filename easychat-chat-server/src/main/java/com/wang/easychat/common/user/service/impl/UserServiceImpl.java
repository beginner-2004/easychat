package com.wang.easychat.common.user.service.impl;

import com.wang.easychat.common.user.domain.entity.User;
import com.wang.easychat.common.user.mapper.UserMapper;
import com.wang.easychat.common.user.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public User getByOpenId(String openId) {
        return lambdaQuery()
                .eq(User::getOpenId, openId)
                .one();
    }

    @Override
    @Transactional
    public Long register(User insert) {
        save(insert);
        // todo 用户注册事件
        return insert.getId();

    }
}

