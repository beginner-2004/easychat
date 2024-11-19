package com.wang.easychat.common.common.system;

import com.wang.easychat.common.common.constant.RedisKey;
import com.wang.easychat.common.common.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * @ClassDescription:
 * @Author:Wangzd
 * @Date: 2024/11/18
 **/
@Component
@Slf4j
public class LifeLogic implements DisposableBean {

    // 项目终止前执行逻辑
    @Override
    public void destroy() throws Exception {
        log.info("spirngboot项目即将关闭...");
    }
}
