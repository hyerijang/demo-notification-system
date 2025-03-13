package com.example.demo.notifications.system.kafka;

import com.example.demo.notifications.system.service.MessageService;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaMessageQueue {
    private final MessageService messageService;
    public BlockingQueue<Long> queue = new LinkedBlockingQueue<>(); // 생산자- 소비자 간 데이터 교환이므로 blocking queue 사용

    public void produce(Long uid) {
        try {
            queue.put(uid);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Async("consumerThreadPoolTaskExecutor") // 카프카 파티션 수에 맞춰서 3개의 쓰레드로 처리
    public void consume() throws InterruptedException {
        Long uid = queue.take();
        log.info("send message to kafka. uid = {}", uid);
        messageService.sendMessage(uid);
    }

}
