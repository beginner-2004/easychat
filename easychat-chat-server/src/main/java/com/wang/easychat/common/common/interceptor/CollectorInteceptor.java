package com.wang.easychat.common.common.interceptor;

import cn.hutool.extra.servlet.ServletUtil;
import com.wang.easychat.common.common.domain.dto.RequestInfo;
import com.wang.easychat.common.common.utils.RequestHolder;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

/**
 * @ClassDescription:
 * @Author:Wangzd
 * @Date: 2024/11/15
 **/
@Component
@Order(1)
@Slf4j
public class CollectorInteceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Long uid = Optional.ofNullable(request.getAttribute(TokenInterceptor.UID))
                .map(Object::toString)
                .map(Long::parseLong)
                .orElse(null);
        RequestInfo requestInfo = new RequestInfo();
        String clientIP = ServletUtil.getClientIP(request);
        requestInfo.setIp(clientIP.equals("0:0:0:0:0:0:0:1") ? "127.0.0.1" : clientIP);    // 记录用户ip
        requestInfo.setUid(uid);
        RequestHolder.set(requestInfo);
        return true;
    }


    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        RequestHolder.remove();
    }
}
