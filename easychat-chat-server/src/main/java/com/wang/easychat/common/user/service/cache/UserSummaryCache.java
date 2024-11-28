package com.wang.easychat.common.user.service.cache;

import com.wang.easychat.common.common.constant.RedisKey;
import com.wang.easychat.common.common.service.cache.AbstractRedisStringCache;
import com.wang.easychat.common.user.domain.dto.SummeryInfoDTO;
import com.wang.easychat.common.user.domain.entity.*;
import com.wang.easychat.common.user.domain.enums.ItemTypeEnum;
import com.wang.easychat.common.user.service.IUserBackpackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @ClassDescription:
 * @Author:Wangzd
 * @Date: 2024/11/28
 **/
@Component
public class UserSummaryCache extends AbstractRedisStringCache<Long, SummeryInfoDTO> {
    @Autowired
    @Lazy
    private UserInfoCache userInfoCache;
    @Autowired
    private ItemCache itemCache;
    @Autowired
    private IUserBackpackService userBackpackService;


    @Override
    protected String getKey(Long uid) {
        return RedisKey.getKey(RedisKey.USER_SUMMARY_STRING, uid);
    }

    @Override
    protected Long getExpireSeconds() { // 十分钟过期
        return 10 * 60L;
    }

    @Override
    protected Map<Long, SummeryInfoDTO> load(List<Long> uidList) {
        // 用户基本信息
        Map<Long, User> userMap = userInfoCache.getBatch(uidList);
        // 用户徽章信息
        List<ItemConfig> itemConfigs = itemCache.getByType(ItemTypeEnum.BADGE.getType());
        List<Long> itemIds = itemConfigs.stream().map(ItemConfig::getId).collect(Collectors.toList());
        List<UserBackpack> backpacks = userBackpackService.getByItemIds(uidList, itemIds);
        Map<Long, List<UserBackpack>> userBadgeMap = backpacks.stream().collect(Collectors.groupingBy(UserBackpack::getUid));
        // 用户最后一次更新时间
        return uidList.stream().map(uid -> {
            SummeryInfoDTO summeryInfoDTO = new SummeryInfoDTO();
            User user = userMap.get(uid);
            if (Objects.isNull(user)){
                return null;
            }
            List<UserBackpack> userBackpacks = userBadgeMap.getOrDefault(user.getId(), new ArrayList<>());
            summeryInfoDTO.setUid(user.getId());
            summeryInfoDTO.setName(user.getName());
            summeryInfoDTO.setAvatar(user.getAvatar());
            summeryInfoDTO.setLocPlace(Optional.ofNullable(user.getIpInfo()).map(IpInfo::getUpdateIpDetail).map(IpDetail::getCity).orElse(null));
            summeryInfoDTO.setWearingItemId(user.getItemId());
            summeryInfoDTO.setItemIds(userBackpacks.stream().map(UserBackpack::getItemId).collect(Collectors.toList()));
            return summeryInfoDTO;
        })
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(SummeryInfoDTO::getUid, Function.identity()));
    }
}
