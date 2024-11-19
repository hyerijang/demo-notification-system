package com.example.demo.notifications.system.config;

import java.util.concurrent.ThreadPoolExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Slf4j
@Configuration
public class AsyncConfig {

    final int MAX_THREAD_SIZE = 50;

    @Bean
    public ThreadPoolTaskExecutor messageSenderThreadPoolTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(MAX_THREAD_SIZE); // 코어 스레드 풀 크기 설정
        executor.setMaxPoolSize(MAX_THREAD_SIZE); // 최대 스레드 풀 크기 설정
        executor.setQueueCapacity(Integer.MAX_VALUE); // 작업 큐 용량 설정
        executor.setThreadNamePrefix("message-sender-child-executor"); // 스레드 이름 접두사 설정
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy() {
                                                 @Override
                                                 public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
                                                     log.warn("Message Sender Task Queue is Full");
                                                     super.rejectedExecution(r, e);
                                                 }
                                             }
        );
        executor.initialize();
        return executor;
    }


    @Bean
    public ThreadPoolTaskExecutor consumerThreadPoolTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(3); // 코어 스레드 풀 크기 설정
        executor.setMaxPoolSize(3); // 최대 스레드 풀 크기 설정
        executor.setQueueCapacity(Integer.MAX_VALUE); // 작업 큐 용량 설정
        executor.setThreadNamePrefix("consumer-child-executor"); // 스레드 이름 접두사 설정
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy() {
                                                 @Override
                                                 public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
                                                     log.warn("Consumer Task Queue is Full");
                                                     super.rejectedExecution(r, e);
                                                 }
                                             }
        );
        executor.initialize();
        return executor;
    }
}
