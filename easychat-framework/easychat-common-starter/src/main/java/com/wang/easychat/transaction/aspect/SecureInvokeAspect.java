package com.wang.easychat.transaction.aspect;

import cn.hutool.core.date.DateUtil;
import com.wang.easychat.transaction.annotation.SecureInvoke;
import com.wang.easychat.transaction.domain.dto.SecureInvokeDTO;
import com.wang.easychat.transaction.domain.entity.SecureInvokeRecord;
import com.wang.easychat.transaction.service.SecureInvokeHolder;
import com.wang.easychat.transaction.service.SecureInvokeRecordService;
import com.wang.easychat.transaction.service.SecureinvokeService;
import com.wang.easychat.transaction.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @ClassDescription: 安全执行切面
 * @Author:Wangzd
 * @Date: 2024/12/3
 **/
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
@Aspect
@Component
public class SecureInvokeAspect {

    @Autowired
    private SecureinvokeService secureinvokeService;


    @Around("@annotation(secureInvoke)")
    public Object around(ProceedingJoinPoint joinPoint, SecureInvoke secureInvoke) throws Throwable {
        boolean async = secureInvoke.async();
        boolean isTransaction = TransactionSynchronizationManager.isActualTransactionActive();
        // 非事务状态，直接执行，不做任何保证
        if (SecureInvokeHolder.isInvoking() || !isTransaction){
            return joinPoint.proceed();
        }
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        List<String> parameters = Stream.of(method.getParameterTypes()).map(Class::getName).collect(Collectors.toList());
        SecureInvokeDTO dto = SecureInvokeDTO.builder()
                .className(method.getDeclaringClass().getName())
                .args(JsonUtils.toStr(joinPoint.getArgs()))
                .methodName(method.getName())
                .parameterTypes(JsonUtils.toStr(parameters))
                .build();
        SecureInvokeRecord record = SecureInvokeRecord.builder()
                .secureInvokeDTO(dto)
                .maxRetryTimes(secureInvoke.maxRetryTimes())
                .nextRetryTime(DateUtil.offsetMinute(new Date(), (int) SecureinvokeService.RETRY_INTERVAL_MINUTES))
                .build();
        secureinvokeService.invoke(record, async);
        return null;
    }
}
