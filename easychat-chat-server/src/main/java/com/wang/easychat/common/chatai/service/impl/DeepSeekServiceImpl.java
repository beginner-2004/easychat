package com.wang.easychat.common.chatai.service.impl;

import com.wang.easychat.common.chatai.config.DeepSeekProperties;
import com.wang.easychat.common.chatai.domain.dto.DeepSeekRequest;
import com.wang.easychat.common.chatai.domain.dto.DeepSeekResponse;
import com.wang.easychat.common.chatai.service.DeepSeekService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * @ClassDescription:
 * @Author:Wangzd
 * @Date: 2025/2/18
 **/
@Service
public class DeepSeekServiceImpl implements DeepSeekService {

    @Autowired
    private RestTemplate restTemplate;

    private final DeepSeekProperties deepSeekProperties;
    @Autowired
    public DeepSeekServiceImpl(DeepSeekProperties deepSeekProperties) {
        this.deepSeekProperties = deepSeekProperties;
    }

    /**
     * 调用deepseek接口
     * @param request
     * @return
     */
    @Override
    public String getCompletion(DeepSeekRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(deepSeekProperties.getKey());

        HttpEntity<DeepSeekRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<DeepSeekResponse> response = restTemplate.exchange(
                deepSeekProperties.getUrl(),
                HttpMethod.POST,
                entity,
                DeepSeekResponse.class
        );

        if (response.getStatusCode().is2xxSuccessful() &&
                response.getBody() != null &&
                !response.getBody().getChoices().isEmpty()) {
            return response.getBody().getChoices().get(0).getMessage().getContent();
        }
        throw new RuntimeException("API request failed: " + response.getStatusCode());
    }
}
