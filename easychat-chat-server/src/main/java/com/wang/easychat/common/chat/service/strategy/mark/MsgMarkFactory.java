package com.wang.easychat.common.chat.service.strategy.mark;

import com.wang.easychat.common.common.exception.CommonErrorEnum;
import com.wang.easychat.common.common.utils.AssertUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassDescription: 消息标记工厂
 * @Author:Wangzd
 * @Date: 2024/12/9
 **/
public class MsgMarkFactory {
    private static final Map<Integer, AbstractMsgMarkStrategy> STRATEGY_MAP = new HashMap<>();

    public static void register(Integer markType, AbstractMsgMarkStrategy strategy){
        STRATEGY_MAP.put(markType, strategy);
    }

    public static AbstractMsgMarkStrategy getStrategyNoNull(Integer markType){
        AbstractMsgMarkStrategy strategy = STRATEGY_MAP.get(markType);
        AssertUtil.isNotEmpty(strategy, CommonErrorEnum.PARAM_INVALID);
        return strategy;
    }
}
