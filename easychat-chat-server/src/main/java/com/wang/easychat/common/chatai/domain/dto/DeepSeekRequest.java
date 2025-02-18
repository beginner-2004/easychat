package com.wang.easychat.common.chatai.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
public class DeepSeekRequest {
    private String model;
    private List<Message> messages;

    // 根据API文档需要的其他参数（temperature, max_tokens等）
    private double temperature = 0.7;
    private int max_tokens = 1000;

    @Data
    @AllArgsConstructor
    public static class Message {
        private String role;
        private String content;
    }

}