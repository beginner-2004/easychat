package com.wang.easychat.common.user.service.impl;

import cn.hutool.core.util.StrUtil;
import com.wang.easychat.common.common.constant.RedisKey;
import com.wang.easychat.common.common.utils.JwtUtils;
import com.wang.easychat.common.common.utils.RedisUtils;
import com.wang.easychat.common.user.service.LoginService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @ClassDescription:
 * @Author:Wangzd
 * @Date: 2024/11/12
 **/
@Service
@Slf4j
public class LoginServiceImpl implements LoginService {

    public static final int TOKEN_EXPIRE_DAYS = 3;
    public static final int TOKEN_RENEW_DAYS = 1;
    @Autowired
    private JwtUtils jwtUtils;

    /**
     * 刷新token有效期
     *
     * @param token
     */
    @Override
    @Async
    public void renewalTokenIfNecessary(String token) {
        Long uid = getValidUid(token);
        String userTokenKey = getUserTokenKey(uid);
        Long expireDays = RedisUtils.getExpire(userTokenKey, TimeUnit.DAYS);
        if (expireDays == -2){  // 不存在当前key
            return;
        }
        if (expireDays < TOKEN_RENEW_DAYS){
            RedisUtils.expire(getUserTokenKey(uid), TOKEN_EXPIRE_DAYS, TimeUnit.DAYS);
        }
    }

    /**
     * 登录成功，获取token
     *
     * @param uid
     * @return 返回token
     */
    @Override
    public String login(Long uid) {
        String token = jwtUtils.createToken(uid);
        RedisUtils.set(getUserTokenKey(uid), token, TOKEN_EXPIRE_DAYS, TimeUnit.DAYS);
        return token;
    }

    /**
     * 如果token有效，返回uid
     *
     * @param token
     * @return
     */
    @Override
    public Long getValidUid(String token) {
        Long uid = jwtUtils.getUidOrNull(token);
        if (Objects.isNull(uid)){
            return null;
        }
        String oldToken = RedisUtils.getStr(getUserTokenKey(uid));
        if (StrUtil.isBlank(oldToken)){
            return null;
        }
        return Objects.equals(oldToken, token) ? uid : null;
    }

    private String getUserTokenKey(Long uid){
        return RedisKey.getKey(RedisKey.USER_TOKEN_STRING, uid);
    }
}
