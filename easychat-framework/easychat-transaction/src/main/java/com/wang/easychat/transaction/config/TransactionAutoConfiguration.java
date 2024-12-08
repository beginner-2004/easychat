package com.wang.easychat.transaction.config;

import com.wang.easychat.transaction.annotation.SecureInvokeConfigurer;
import com.wang.easychat.transaction.mapper.SecureInvokeRecordMapper;
import com.wang.easychat.transaction.service.MQProducer;
import com.wang.easychat.transaction.service.SecureInvokeRecordService;
import com.wang.easychat.transaction.service.SecureinvokeService;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.util.CollectionUtils;
import org.springframework.util.function.SingletonSupplier;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @ClassDescription: 配置类
 * @Author:Wangzd
 * @Date: 2024/12/3
 **/
@Configuration
@EnableScheduling
@MapperScan(basePackageClasses = SecureInvokeRecordMapper.class)
public class TransactionAutoConfiguration {

    @Nullable
    protected Executor executor;

    @Autowired
    void setConfigurers(ObjectProvider<SecureInvokeConfigurer> configurers) {
        Supplier<SecureInvokeConfigurer> configurer = SingletonSupplier.of(() -> {
            List<SecureInvokeConfigurer> candidates = configurers.stream().collect(Collectors.toList());
            if (CollectionUtils.isEmpty(candidates)) {
                return null;
            }
            if (candidates.size() > 1) {
                throw new IllegalStateException("Only one SecureInvokeConfigurer may exist");
            }
            return candidates.get(0);
        });
        executor = Optional.ofNullable(configurer.get()).map(SecureInvokeConfigurer::getSecureInvokeExecutor).orElse(ForkJoinPool.commonPool());
    }

    @Bean
    public SecureinvokeService getSecureInvokeService(SecureInvokeRecordService secureInvokeRecordService) {
        return new SecureinvokeService(executor, secureInvokeRecordService);
    }

    @Bean
    public MQProducer getMQProducer() {
        return new MQProducer();
    }

}
