package com.wang.easychat.transaction.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wang.easychat.transaction.annotation.SecureInvoke;
import com.wang.easychat.transaction.domain.dto.SecureInvokeDTO;
import com.wang.easychat.transaction.domain.entity.SecureInvokeRecord;
import com.wang.easychat.transaction.mapper.SecureInvokeRecordMapper;
import com.wang.easychat.transaction.service.SecureInvokeHolder;
import com.wang.easychat.transaction.service.SecureInvokeRecordService;
import com.wang.easychat.transaction.service.SecureinvokeService;
import com.wang.easychat.transaction.utils.JsonUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;

/**
 * @ClassDescription:
 * @Author:Wangzd
 * @Date: 2024/12/3
 **/
@Service
public class SecureInvokeServiceImpl extends ServiceImpl<SecureInvokeRecordMapper, SecureInvokeRecord> implements SecureInvokeRecordService {


    /**
     * 查询需要重试的方法
     */
    @Override
    public List<SecureInvokeRecord> getWaitRetryRecords() {
        Date now = new Date();
        // 查两分钟之前失效的数据
        DateTime afterTime = DateUtil.offsetMinute(now, (int) SecureinvokeService.RETRY_INTERVAL_MINUTES);
        return lambdaQuery()
                .eq(SecureInvokeRecord::getStatus, SecureInvokeRecord.STATUS_WAIT)
                .lt(SecureInvokeRecord::getNextRetryTime, new Date())
                .lt(SecureInvokeRecord::getCreateTime, afterTime)
                .list();
    }
}
