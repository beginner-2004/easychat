package com.wang.easychat.common.chatai.service;

import com.wang.easychat.common.chatai.domain.dto.DeepSeekRequest;

/**
 * @ClassDescription:
 * @Author:Wangzd
 * @Date: 2025/2/18
 **/
public interface DeepSeekService {
    /**
     * 调用deepseek接口
     * @param request
     * @return
     */
    String getCompletion(DeepSeekRequest request);
}
