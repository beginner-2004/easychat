package com.wang.easychat.common;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;


/**
 * @ClassDescription:
 * @Author:Wangzd
 * @Date: 2024/11/15
 **/
@SpringBootApplication(scanBasePackages = {"com.wang.easychat"})
@MapperScan({"com.wang.easychat.common.**.mapper"})
@EnableAspectJAutoProxy(exposeProxy = true)
public class EasychatCustomApplication {

    public static void main(String[] args) {
        SpringApplication.run(EasychatCustomApplication.class,args);
    }

}