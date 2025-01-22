package com.example.demo.notifications.system;

import com.example.demo.notifications.system.kafka.KafkaSimulator;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class Simulator {
    private final KafkaSimulator kafkaSimulator;

    @PostConstruct
    public void start() {
        Long size = 1000L;

        // 메세지 생성
        for (Long uid = 0L; uid < size; uid++) {
            kafkaSimulator.produce(uid);
        }

        // 메세지 소비
        for (Long uid = 0L; uid < size; uid++) {
            kafkaSimulator.consume();
        }
    }
}