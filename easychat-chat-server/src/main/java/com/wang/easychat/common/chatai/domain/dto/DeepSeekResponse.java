package com.wang.easychat.common.chatai.domain.dto;

import lombok.Data;

import java.util.List;

/**
 * @ClassDescription:
 * @Author:Wangzd
 * @Date: 2025/2/18
 **/
@Data
public class DeepSeekResponse {
    private List<Choice> choices;

    @Data
    public static class Choice {
        private Message message;
        private String finish_reason;

        public Message getMessage() {
            return message;
        }

        public void setMessage(Message message) {
            this.message = message;
        }

        public String getFinish_reason() {
            return finish_reason;
        }

        public void setFinish_reason(String finish_reason) {
            this.finish_reason = finish_reason;
        }
    }

    @Data
    public static class Message {
        private String role;
        private String content;
    }
}