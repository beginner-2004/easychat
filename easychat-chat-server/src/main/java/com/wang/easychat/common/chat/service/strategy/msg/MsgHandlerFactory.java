package com.wang.easychat.common.chat.service.strategy.msg;

import com.wang.easychat.common.common.exception.CommonErrorEnum;
import com.wang.easychat.common.common.utils.AssertUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassDescription: 消息处理器工厂
 * @Author:Wangzd
 * @Date: 2024/11/29
 **/
public class MsgHandlerFactory {
    private static final Map<Integer, AbstractMsgHandler> STRATEGY_MAP = new HashMap<>();

    public static void register(Integer code, AbstractMsgHandler strategy){
        STRATEGY_MAP.put(code, strategy);
    }

    public static AbstractMsgHandler getStrategyNoNull(Integer code){
        AbstractMsgHandler strategy = STRATEGY_MAP.get(code);
        AssertUtil.isNotEmpty(strategy, CommonErrorEnum.PARAM_INVALID);
        return strategy;
    }
}
