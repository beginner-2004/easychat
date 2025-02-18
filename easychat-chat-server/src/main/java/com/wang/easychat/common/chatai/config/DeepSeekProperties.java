package com.wang.easychat.common.chatai.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
 * @ClassDescription:
 * @Author:Wangzd
 * @Date: 2025/2/18
 **/
@Data
@ConfigurationProperties(prefix = "chatai.deepseek.api")
public class DeepSeekProperties {
    /**
     * 请求地址
     */
    private String url;

    /**
     * deepseek api密钥
     */
    private String key;
}
