package com.example.demo.notifications.system.kafka;

import com.example.demo.notifications.system.service.MessageService;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaSimulator {
    private final MessageService messageService;
    public final Queue<Long> queue = new ConcurrentLinkedQueue<>();
//    public final Queue<Long> queue = new ArrayBlockingQueue<>(20000);


    public void produce(Long uid) {
        queue.offer(uid);
    }

    @Async("consumerThreadPoolTaskExecutor") // 카프카 파티션 수에 맞춰서 3개의 쓰레드로 처리
    public void consume() {
        try {
            Thread.sleep(20); // 네트워크 통신에 이정도 걸린다고 가정
        }
        catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        Long uid = queue.remove(); // FIMXE : take로 변경
        log.info("send message to kafka. uid = {}", uid);
        messageService.sendMessage(uid);
    }

}
