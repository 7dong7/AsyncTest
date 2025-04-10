package org.asynctest.asynctest.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class AsyncConfig {

    @Bean
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5); // 기본 스레디의 수
        executor.setMaxPoolSize(10); // 최대 스레드 수
        executor.setQueueCapacity(40); // 대기 큐 크기
        executor.setThreadNamePrefix("testAsync-"); // 스레드 이름 접두사
        executor.initialize();
        return executor;
    }
}
