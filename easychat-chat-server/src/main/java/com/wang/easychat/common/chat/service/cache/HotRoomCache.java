package com.wang.easychat.common.chat.service.cache;

import cn.hutool.core.lang.Pair;
import com.wang.easychat.common.common.constant.RedisKey;
import com.wang.easychat.common.common.domain.vo.req.CursorPageBaseReq;
import com.wang.easychat.common.common.domain.vo.resp.CursorPageBaseResp;
import com.wang.easychat.common.common.utils.CursorUtils;
import com.wang.easychat.common.common.utils.RedisUtils;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Set;

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

    /**
     * 按顺序获取热门房间
     */
    public Set<ZSetOperations.TypedTuple<String>> getRoomRange(Double hotStart, Double hotEnd) {
        return RedisUtils.zRangeByScoreWithScores(RedisKey.getKey(RedisKey.HOT_ROOM_ZET), hotStart, hotEnd);
    }

    /**
     * 获取全员房间返回体
     * @param request
     * @return
     */
    public CursorPageBaseResp<Pair<Long, Double>> getRoomCursorPage(CursorPageBaseReq request) {
        return CursorUtils.gerCurSorPageByRedis(request, RedisKey.getKey(RedisKey.HOT_ROOM_ZET), Long::parseLong);
    }
}
