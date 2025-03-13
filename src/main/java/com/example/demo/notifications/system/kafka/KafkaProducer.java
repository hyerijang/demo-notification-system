package com.example.demo.notifications.system.kafka;


import lombok.AllArgsConstructor;

@AllArgsConstructor
public class KafkaProducer implements Runnable {
    private final KafkaMessageQueue kafkaMessageQueue;
    private long size;

    @Override
    public void run() {
        for (long i = 0; i < size; i++) {
            kafkaMessageQueue.produce(i);
        }
    }
}
