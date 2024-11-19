package com.example.demo.notifications.system.controller;

import com.example.demo.notifications.system.consumer.Consumer;
import com.example.demo.notifications.system.repository.MessageResultRepository;
import com.example.demo.notifications.system.service.MessageResultService;
import com.example.demo.notifications.system.service.MessageService;
import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest
class MessageControllerTest {


    private static final Logger log = LoggerFactory.getLogger(MessageControllerTest.class);
    @Autowired
    private Consumer consumer;

    @Autowired
    private MessageResultRepository messageResultRepository;

    @Autowired
    private MessageResultService messageResultService;

    @Autowired
    private MessageService messageService;

    private static final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);


    @BeforeEach
    void init() {
        messageResultRepository.deleteAllInBatch();
    }


    @DisplayName("메세지 발송 테스트")
    @ParameterizedTest(name = "{index} 메세지 수={0}")
    @ValueSource(longs = {10000})
        //100만
    void send(long size) throws InterruptedException {

        log.info("test start (size =  {})", size);
        for (long uid = 0; uid < size; uid++) {
            consumer.sendMessage(uid); //broker에서 데이터 가져올 때 10ms 걸린다고 가정
            Thread.sleep(3);
        }

        int waitTime = 10; // 10분

        // Awaitility로 비동기 작업 완료 대기
        Awaitility.await()
            .atMost(Duration.ofMinutes(waitTime))
            .pollInterval(Duration.ofMillis(500)) // 0.5초마다 상태 확인
            .until(() -> messageResultRepository.count() == size); // 저장된 메시지 수가 예상 크기와 같아질 때까지 대기

        // 검증
        assertEquals(size, messageResultRepository.count());
    }

}