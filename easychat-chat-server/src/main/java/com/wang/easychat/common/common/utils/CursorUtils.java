package com.wang.easychat.common.common.utils;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Pair;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wang.easychat.common.common.domain.vo.req.CursorPageBaseReq;
import com.wang.easychat.common.common.domain.vo.resp.CursorPageBaseResp;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @ClassDescription: 游标分页工具类
 * @Author:Wangzd
 * @Date: 2024/11/25
 **/
public class CursorUtils {
    /**
     * 通过redis获取需要查询的游标
     */
    public static <T> CursorPageBaseResp<Pair<T, Double>> gerCurSorPageByRedis(CursorPageBaseReq cursorPageBaseReq, String redisKey, Function<String, T> typeConvert){
        Set<ZSetOperations.TypedTuple<String>> typedTuples;
        if (StringUtils.isBlank(cursorPageBaseReq.getCursor())) {
            // 第一次获取
            typedTuples = RedisUtils.zReverseRangeWithScores(redisKey, cursorPageBaseReq.getPageSize());
        } else {
            typedTuples = RedisUtils.zReverseRangeByScoreWithScores(redisKey, Double.parseDouble(cursorPageBaseReq.getCursor()), cursorPageBaseReq.getPageSize());
        }
        List<Pair<T, Double>> result = typedTuples
                .stream()
                .map(a -> Pair.of(typeConvert.apply(a.getValue()), a.getScore()))
                .sorted((o1, o2) -> o2.getValue().compareTo(o1.getValue()))
                .collect(Collectors.toList());
        String cursor = Optional.ofNullable(CollectionUtil.getLast(result))
                .map(Pair::getValue)
                .map(String::valueOf)
                .orElse(null);
        Boolean isLast = result.size() != cursorPageBaseReq.getPageSize();
        return new CursorPageBaseResp<>(cursor, isLast, result);
    }

    /**
     * 通过mysql获取需要查询的游标
     */
    public static <T> CursorPageBaseResp<T> getCursorPageByMysql(
            IService<T> service, CursorPageBaseReq request,
            Consumer<LambdaQueryWrapper<T>> initWrapper, SFunction<T, ?> cursorColumn){
        // todo 通过 XXX::getXxx 获得 该字段类型原理
        Class<?> cursorType = MyLambdaUtils.getReturnType(cursorColumn);
        LambdaQueryWrapper<T> wrapper = new LambdaQueryWrapper<>();
        // 传入的额外条件
        initWrapper.accept(wrapper);
        // 游标条件
        if (StringUtils.isNotBlank(request.getCursor())){
            wrapper.lt(cursorColumn, parseCursor(request.getCursor(), cursorType));
        }
        // 游标方向
        wrapper.orderByDesc(cursorColumn);
        Page pageReq = request.plusPage();
        pageReq.setSearchCount(false);
        Page<T> page = service.page(pageReq, wrapper);
        // 取出游标
        String cursor = Optional.ofNullable(CollectionUtil.getLast(page.getRecords()))
                .map(cursorColumn)
                .map(CursorUtils::toCursor)
                .orElse(null);

        // 判断是否最后一页
        Boolean isLast = page.getRecords().size() != request.getPageSize();
        return new CursorPageBaseResp<>(cursor, isLast, page.getRecords());
    }

    /**
     * 如果是Date类型，将时间转化成毫秒
     */
    private static String toCursor(Object o) {
        if (o instanceof Date){
            return String.valueOf(((Date) o).getTime());
        }else {
            return o.toString();
        }
    }


    /**
     * 如果是Date类型，将时间转换成Long类型返回
     */
    private static Object parseCursor(String cursor, Class<?> cursorClass) {
        if (Date.class.isAssignableFrom(cursorClass)){
            return new Date(Long.parseLong(cursor));
        }else {
            return cursor;
        }
    }
}
