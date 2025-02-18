package com.wang.easychat.common.chatai.service;

import com.wang.easychat.common.chatai.domain.dto.DeepSeekRequest;
import com.wang.easychat.common.chatai.domain.dto.DeepSeekResponse;
import org.springframework.http.ResponseEntity;

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
    ResponseEntity<DeepSeekResponse> getCompletion(DeepSeekRequest request);
}
