package com.wang.easychat.common.user.service;

import com.wang.easychat.common.user.domain.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author wang
 * @since 2024-11-08
 */
@Service
public interface IUserService extends IService<User> {
    User getByOpenId(String openId);

    Long register(User insert);
}
