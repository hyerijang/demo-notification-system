package com.example.demo.notifications.system.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class KafkaConsumer implements Runnable {

    private final KafkaMessageQueue kafkaMessageQueue;

    @Override
    public void run() {
        try {
            while (true) {
                kafkaMessageQueue.consume();
            }
        } catch (InterruptedException e) {
            log.error("Pring Log Thread Interrupted Exception", e);
            Thread.currentThread().interrupt();
        }
    }
}
