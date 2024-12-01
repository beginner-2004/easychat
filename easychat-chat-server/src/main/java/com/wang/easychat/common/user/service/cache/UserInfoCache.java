package com.wang.easychat.common.user.service.cache;

import com.wang.easychat.common.common.constant.RedisKey;
import com.wang.easychat.common.common.service.cache.AbstractRedisStringCache;
import com.wang.easychat.common.user.domain.entity.User;
import com.wang.easychat.common.user.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @ClassDescription:
 * @Author:Wangzd
 * @Date: 2024/11/28
 **/
@Component
public class UserInfoCache extends AbstractRedisStringCache<Long, User> {
    @Autowired
    private IUserService userService;

    @Override
    protected String getKey(Long uid) {
        return RedisKey.getKey(RedisKey.USER_INTO_STRING, uid);
    }

    @Override
    protected Long getExpireSeconds() {
        return 5 * 60L;
    }

    @Override
    protected Map<Long, User> load(List<Long> uidList) {
        List<User> needLoadUserList = userService.listByIds(uidList);
        return needLoadUserList.stream().collect(Collectors.toMap(User::getId, Function.identity()));
    }
}
