package com.wang.easychat.common;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

/**
 * @author zhongzb
 * @date 2021/05/27
 */
@SpringBootApplication(scanBasePackages = {"com.wang.easychat"})
@MapperScan({"com.wang.easychat.common.**.mapper"})
public class EasychatCustomApplication {

    public static void main(String[] args) {
        SpringApplication.run(EasychatCustomApplication.class,args);
    }

}