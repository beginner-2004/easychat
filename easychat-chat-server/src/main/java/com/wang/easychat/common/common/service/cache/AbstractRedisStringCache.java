package com.wang.easychat.common.common.service.cache;

import cn.hutool.core.collection.CollectionUtil;
import com.wang.easychat.common.common.utils.RedisUtils;
import io.swagger.models.auth.In;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassDescription: String类型redis批量缓存框架
 * @Author:Wangzd
 * @Date: 2024/11/28
 **/
@Component
public abstract class AbstractRedisStringCache<IN, OUT> implements BatchCache<IN, OUT> {

    private Class<OUT> outClass;

    // 将OUT的class在加载时就记录
    protected AbstractRedisStringCache(){
        ParameterizedType genericSuperclass = (ParameterizedType) this.getClass().getGenericSuperclass();
        this.outClass = (Class<OUT>) genericSuperclass.getActualTypeArguments()[1];
    }


    protected abstract String getKey(IN req);

    protected abstract Map<IN, OUT> load(List<IN> req);

    protected abstract Long getExpireSeconds();


    /**
     * 获取单个
     *
     * @param req
     */
    @Override
    public OUT get(IN req) {
        return null;
    }

    /**
     * 获取批量
     *
     * @param req
     */
    @Override
    public Map<IN, OUT> getBatch(List<IN> req) {
        // 防御性编程
        if (CollectionUtil.isEmpty(req)){
            return new HashMap<>();
        }
        // 去重
        req = req.stream().distinct().collect(Collectors.toList());
        // 组装key
        List<String> keys = req.stream().map(this::getKey).collect(Collectors.toList());
        // 批量get
        List<OUT> valueList = RedisUtils.mget(keys, outClass);
        // 差集计算
        List<IN> loadReqs = new ArrayList<>();
        for (int i = 0; i < valueList.size(); i++) {
            if (Objects.isNull(valueList.get(i))){
                loadReqs.add(req.get(i));
            }
        }
        Map<IN, OUT> load = new HashMap<>();
        // 需要重新加载的批量加载
        if (CollectionUtil.isNotEmpty(loadReqs)){
            // 批量加载
            load = load(loadReqs);
            Map<String, OUT> loadMap = load.entrySet().stream()
                    .map(item -> Pair.of(getKey(item.getKey()), item.getValue()))
                    .collect(Collectors.toMap(Pair::getFirst, Pair::getSecond));
            RedisUtils.mset(loadMap, getExpireSeconds());
        }

        // 组装结果
        HashMap<IN, OUT> resultMap = new HashMap<>();
        for (int i = 0; i < req.size(); i++) {
            IN in = req.get(i);
            OUT out = Optional.ofNullable(valueList.get(i))
                    .orElse(load.get(in));
            resultMap.put(in, out);
        }
        return resultMap;
    }

    /**
     * 修改删除单个
     *
     * @param req
     */
    @Override
    public void delete(IN req) {

    }

    /**
     * 修改删除多个
     *
     * @param req
     */
    @Override
    public void deleteBatch(List<IN> req) {

    }
}
