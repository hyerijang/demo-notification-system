package com.example.demo.notifications.system.kafka;

import com.example.demo.notifications.system.repository.MessageResultRepository;
import java.time.Duration;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
class KafkaSimulatorTest {

    @Autowired
    private MessageResultRepository messageResultRepository;

    @Autowired
    private KafkaSimulator kafkaSimulator;

    @BeforeEach
    void init() {
        messageResultRepository.deleteAllInBatch();
    }

    @DisplayName("메세지 발송 테스트")
    @Test
    void sendMessages() {
        Long size = 5L;
        kafkaSimulator.send(size);
        Awaitility.await().atMost(Duration.ofSeconds(5));
    }
}