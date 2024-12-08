package com.wang.easychat.transaction.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.wang.easychat.transaction.domain.dto.SecureInvokeDTO;
import com.wang.easychat.transaction.domain.entity.SecureInvokeRecord;
import com.wang.easychat.transaction.utils.JsonUtils;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import com.wang.easychat.transaction.utils.JsonUtils;

/**
 * @ClassDescription: 安全执行处理器
 * @Author:Wangzd
 * @Date: 2024/12/3
 **/
@Slf4j
@AllArgsConstructor
public class SecureinvokeService {
    public static final double RETRY_INTERVAL_MINUTES = 2D;

    private final Executor executor;

    private SecureInvokeRecordService secureInvokeRecordService;

    /**
     * 处理需要重试的方法(5分钟执行一次)
     */
    @Scheduled(cron = "*/5 * * * * ?")
    public void retry() {
        List<SecureInvokeRecord> secureInvokeRecords = secureInvokeRecordService.getWaitRetryRecords();
        for (SecureInvokeRecord secureInvokeRecord : secureInvokeRecords) {
            doAsyncInvoke(secureInvokeRecord);
        }
    }

    public void invoke(SecureInvokeRecord record, boolean async) {
        boolean inTransaction = TransactionSynchronizationManager.isActualTransactionActive();
        //非事务状态，直接执行，不做任何保证。
        if (!inTransaction) {
            return;
        }
        //保存执行数据
        save(record);
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @SneakyThrows
            @Override
            public void afterCommit() {
                //事务后执行
                if (async) {
                    doAsyncInvoke(record);
                } else {
                    doInvoke(record);
                }
            }
        });
    }

    private void doInvoke(SecureInvokeRecord record) {
        SecureInvokeDTO secureInvokeDTO = record.getSecureInvokeDTO();
        try {
            SecureInvokeHolder.setInvoking();
            Class<?> beanClass = Class.forName(secureInvokeDTO.getClassName());
            Object bean = SpringUtil.getBean(beanClass);
            List<String> parameterStrings = JsonUtils.toList(secureInvokeDTO.getParameterTypes(), String.class);
            List<Class<?>> parameterClasses = getParameterClasses(parameterStrings);
            Method method = ReflectUtil.getMethod(beanClass, secureInvokeDTO.getMethodName(), parameterClasses.toArray(new Class[]{}));
            Object[] args = getArgs(secureInvokeDTO, parameterClasses);
            // 执行方法
            method.invoke(bean, args);
            // 执行结束更新状态
            removeRecord(record.getId());
        } catch (Throwable e) {
            log.error("SecureInvokeService invoke fail", e);
            //执行失败，等待下次执行
            retryRecord(record, e.getMessage());
        } finally {
            SecureInvokeHolder.invoked();
        }
    }

    private void doAsyncInvoke(SecureInvokeRecord record) {
        executor.execute(() -> {
            log.info("{}:执行消息记录 => {}", Thread.currentThread().getName(), record);
            doInvoke(record);
        });
    }

    private void retryRecord(SecureInvokeRecord record, String errorMsg) {
        Integer retryTimes = record.getRetryTimes() + 1;
        SecureInvokeRecord update = new SecureInvokeRecord();
        update.setId(record.getId());
        update.setFailReason(errorMsg);
        update.setNextRetryTime(getNextRetryTime(retryTimes));
        if (retryTimes > record.getMaxRetryTimes()){
            update.setStatus(SecureInvokeRecord.STATUS_FAIL);
        }else {
            update.setRetryTimes(retryTimes);
        }
        secureInvokeRecordService.updateById(update);
    }

    private Date getNextRetryTime(Integer retryTimes) {
        double waitMinutes = Math.pow(RETRY_INTERVAL_MINUTES, retryTimes);
        return DateUtil.offsetMinute(new Date(), (int) waitMinutes);
    }

    private void removeRecord(Long id) {
        secureInvokeRecordService.removeById(id);
    }

    private Object[] getArgs(SecureInvokeDTO secureInvokeDTO, List<Class<?>> parameterClasses) {
        JsonNode jsonNode = JsonUtils.toJsonNode(secureInvokeDTO.getArgs());
        Object[] args = new Object[jsonNode.size()];
        for (int i = 0; i < jsonNode.size(); i++) {
            Class<?> aClass = parameterClasses.get(i);
            args[i] = JsonUtils.nodeToValue(jsonNode.get(i), aClass);
        }
        return args;
    }

    private List<Class<?>> getParameterClasses(List<String> parameterStrings) {
        return parameterStrings.stream().map(name -> {
            try {
                return Class.forName(name);
            } catch (ClassNotFoundException e) {
                log.error("SecureInvokeService class not fund", e);
            }
            return null;
        }).collect(Collectors.toList());
    }

    private void save(SecureInvokeRecord record) {
        secureInvokeRecordService.save(record);
    }
}
