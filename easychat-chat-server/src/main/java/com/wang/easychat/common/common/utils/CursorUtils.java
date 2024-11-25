package com.wang.easychat.common.common.utils;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wang.easychat.common.common.domain.vo.req.CursorPageBaseReq;
import com.wang.easychat.common.common.domain.vo.resp.CursorPageBaseResp;

import java.util.Date;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * @ClassDescription: 游标分页工具类
 * @Author:Wangzd
 * @Date: 2024/11/25
 **/
public class CursorUtils {

    /**
     * 获取需要查询的游标
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

        Page<T> page = service.page(request.plusPage(), wrapper);
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
