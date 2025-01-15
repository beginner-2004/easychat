package com.wang.easychat.common.user.service.cache;

import cn.hutool.core.collection.CollUtil;
import com.wang.easychat.common.common.constant.RedisKey;
import com.wang.easychat.common.common.utils.RedisUtils;
import com.wang.easychat.common.user.domain.entity.Black;
import com.wang.easychat.common.user.domain.entity.ItemConfig;
import com.wang.easychat.common.user.domain.entity.User;
import com.wang.easychat.common.user.domain.entity.UserRole;
import com.wang.easychat.common.user.service.IBlackService;
import com.wang.easychat.common.user.service.IItemConfigService;
import com.wang.easychat.common.user.service.IUserRoleService;
import com.wang.easychat.common.user.service.IUserService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
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
    @Autowired
    private IBlackService blackService;
    @Autowired
    @Lazy
    private IUserService userService;

    @Cacheable(cacheNames = "user", key = "'blackList'")
    public Map<Integer, Set<String>> getBlackMap() {
        // 根据拉黑的类型分成两组  1：IP  2：UID
        Map<Integer, List<Black>> collect = blackService.list().stream().collect(Collectors.groupingBy(Black::getType));
        Map<Integer, Set<String>> result = new HashMap<>(collect.size());
        for (Map.Entry<Integer, List<Black>> entry : collect.entrySet()) {
            result.put(entry.getKey(), entry.getValue().stream().map(Black::getTarget).collect(Collectors.toSet()));
        }
        return result;
    }

    @CacheEvict(cacheNames = "user", key = "'blackList'")
    public Map<Integer, Set<String>> evictBlackMap() {
        return null;
    }

    @Cacheable(cacheNames = "user", key = "'roles:' + #uid")
    public Set<Long> getRoleSetByUid(Long uid) {
        List<UserRole> userRoles = userRoleService.listByUid(uid);
        return userRoles.stream()
                .map(UserRole::getRoleId)
                .collect(Collectors.toSet());
    }

    /**
     * 获取用户信息，盘路缓存模式
     */
    public Map<Long, User> getUserInfoBatch(Set<Long> uids){
        // 批量组装key
        List<String> keys = uids.stream().map(uid -> RedisKey.getKey(RedisKey.USER_INTO_STRING, uid)).collect(Collectors.toList());
        // 批量get
        List<User> mget = RedisUtils.mget(keys, User.class);
        Map<Long, User> map = mget.stream().filter(Objects::nonNull).collect(Collectors.toMap(User::getId, Function.identity()));
        // 计算差集（需要更新的id）
        List<Long> needLoadUidList = uids.stream().filter(uid -> !map.containsKey(uid)).collect(Collectors.toList());
        if (CollUtil.isNotEmpty(needLoadUidList)){
            // 批量load
            List<User> needLoadUserList = userService.listByIds(needLoadUidList);
            Map<String, User> redisMap = needLoadUserList.stream().collect(Collectors.toMap(user -> RedisKey.getKey(RedisKey.USER_INTO_STRING, user.getId()), Function.identity()));
            RedisUtils.mset(redisMap, 5 * 60);
            // 加载回redis
            map.putAll(needLoadUserList.stream().collect(Collectors.toMap(User::getId, Function.identity())));
        }
        return map;
    }

    public List<Long> getUserModifyTime(List<Long> uidList) {
        List<String> keys = uidList.stream().map(uid -> RedisKey.getKey(RedisKey.USER_MODIFY_STRING, uid)).collect(Collectors.toList());
        return RedisUtils.mget(keys, Long.class);

    }

    /**
     * 获取在线人数
     * @return
     */
    public Long getOnlineNum() {
        String onlineKey = RedisKey.getKey(RedisKey.ONLINE_UID_ZET);
        return RedisUtils.zCard(onlineKey);
    }

    /**
     * 判断用户是否在线
     * @param uid
     * @return
     */
    public boolean isOnline(Long uid) {
        String onlineKey = RedisKey.getKey(RedisKey.ONLINE_UID_ZET);
        return RedisUtils.zIsMember(onlineKey, uid);
    }

    /**
     * 用户下线
     * @param id
     * @param lastOptTime
     */
    public void offline(Long id, Date lastOptTime) {
        String onlineKey = RedisKey.getKey(RedisKey.ONLINE_UID_ZET);
        RedisKey.getKey(RedisKey.OFFLINE_UID_ZET);
    }

    /**
     * 用户上线
     * @param uid
     * @param optTime
     */
    public void online(Long uid, Date optTime) {
        String onlineKey = RedisKey.getKey(RedisKey.ONLINE_UID_ZET);
        String offlineKey = RedisKey.getKey(RedisKey.OFFLINE_UID_ZET);
        //移除离线表
        RedisUtils.zRemove(offlineKey, uid);
        //更新上线表
        RedisUtils.zAdd(onlineKey, uid, optTime.getTime());
    }
}
