package com.example.demo.notifications.system.controller;

import com.example.demo.notifications.system.kafka.KafkaConsumer;
import com.example.demo.notifications.system.kafka.KafkaMessageQueue;
import com.example.demo.notifications.system.kafka.KafkaProducer;
import com.example.demo.notifications.system.service.MessageResultService;
import com.example.demo.notifications.system.service.MessageService;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public class BatchController {

    private final MessageService messageService;
    private final MessageResultService messageResultService;
    private final KafkaMessageQueue kafkaMessageQueue;
    @GetMapping("/start/{size}")
    public String start(@PathVariable Long size) throws ExecutionException, InterruptedException {
        ExecutorService producerParentExecutorService = Executors.newFixedThreadPool(1);
        ExecutorService consumerParentExecutorService = Executors.newFixedThreadPool(1);

        KafkaProducer producer = new KafkaProducer(kafkaMessageQueue, size);
        KafkaConsumer consumer = new KafkaConsumer(kafkaMessageQueue);

        producerParentExecutorService.submit(producer);
        consumerParentExecutorService.submit(consumer);
        
        // TODO : 우아한 종료
        producerParentExecutorService.shutdown();
        consumerParentExecutorService.shutdown();

        return "메세지 발송 시작" + LocalDateTime.now();
    }

}
