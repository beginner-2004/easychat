package com.wang.easychat.transaction.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wang.easychat.transaction.domain.entity.SecureInvokeRecord;
import com.wang.easychat.transaction.mapper.SecureInvokeRecordMapper;

import java.util.List;

/**
 * @ClassDescription:
 * @Author:Wangzd
 * @Date: 2024/12/3
 **/
public interface SecureInvokeRecordService extends IService<SecureInvokeRecord> {

    /**
     * 查询需要重试的方法
     * @return
     */
    List<SecureInvokeRecord> getWaitRetryRecords();
}
