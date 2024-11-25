package com.wang.easychat.common.common.interceptor;

import cn.hutool.core.collection.CollectionUtil;
import com.wang.easychat.common.common.domain.dto.RequestInfo;
import com.wang.easychat.common.common.exception.HttpErrorEnum;
import com.wang.easychat.common.common.utils.RequestHolder;
import com.wang.easychat.common.user.domain.enums.BlackTypeEnum;
import com.wang.easychat.common.user.service.cache.UserCache;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @ClassDescription: 黑名单拦截
 * @Author:Wangzd
 * @Date: 2024/11/19
 **/
@Order(2)
@Slf4j
@Component
public class BlackInterceptor implements HandlerInterceptor {
    @Autowired
    private UserCache userCache;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Map<Integer, Set<String>> blackMap = userCache.getBlackMap();
        RequestInfo requestInfo = RequestHolder.get();
        if (inBlackList(requestInfo.getUid(), blackMap.get(BlackTypeEnum.UID.getType()))){
            HttpErrorEnum.ACCESS_DENIED.sendHttpError(response);
            return false;
        }

        if (inBlackList(requestInfo.getIp(), blackMap.get(BlackTypeEnum.IP.getType()))){
            HttpErrorEnum.ACCESS_DENIED.sendHttpError(response);
            return false;
        }

        return true;
    }

    private boolean inBlackList(Object target, Set<String> blackSet) {
        if (Objects.isNull(target) || CollectionUtil.isEmpty(blackSet)){
            return false;
        }
        return blackSet.contains(target.toString());
    }
}
