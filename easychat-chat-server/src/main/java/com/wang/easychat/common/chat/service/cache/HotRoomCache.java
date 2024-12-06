package com.wang.easychat.common.chat.service.cache;

import com.wang.easychat.common.common.constant.RedisKey;
import com.wang.easychat.common.common.utils.RedisUtils;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @ClassDescription: 全员大群缓存
 * @Author:Wangzd
 * @Date: 2024/12/4
 **/
@Component
public class HotRoomCache {

    /**
     * 更新热门群聊最新事件
     */
    public void refreshActiveTime(Long roomId, Date refreshTime) {
        RedisUtils.zAdd(RedisKey.getKey(RedisKey.HOT_ROOM_ZET), roomId, (double) refreshTime.getTime());
    }

}
