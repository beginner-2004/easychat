package com.wang.easychat.common.chatai.config;

import com.wang.easychat.common.chatai.service.DeepSeekService;
import com.wang.easychat.common.chatai.service.impl.DeepSeekServiceImpl;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * @ClassDescription:
 * @Author:Wangzd
 * @Date: 2025/2/18
 **/
@Configuration
@EnableConfigurationProperties(DeepSeekProperties.class)
public class DeepSeekConfiguration {

    // 调用api使用的RestTemplate实例
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public DeepSeekService deepSeekService(DeepSeekProperties deepSeekProperties) {
        return new DeepSeekServiceImpl(deepSeekProperties);
    }
}
