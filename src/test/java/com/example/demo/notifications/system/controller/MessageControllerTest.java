package com.example.demo.notifications.system.controller;

import com.example.demo.notifications.system.consumer.Consumer;
import com.example.demo.notifications.system.repository.MessageResultRepository;
import java.time.Duration;
import java.time.LocalDate;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest
class MessageControllerTest {


    private static final Logger log = LoggerFactory.getLogger(MessageControllerTest.class);
    @Autowired
    private Consumer consumer;

    @Autowired
    private MessageResultRepository messageResultRepository;

    private static final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);


    @BeforeEach
    void init(){
        messageResultRepository.deleteAllInBatch();
    }


    @DisplayName("메세지 발송 테스트")
    @ParameterizedTest(name = "{index} {displayName} 메세지 수={0}")
    @ValueSource(longs = {1000000})
    void send(long size) {

        log.info("test start (size =  {})", size);
        for (long uid = 0; uid < size; uid++) {
            long finalUid = uid;
            executor.schedule(() -> consumer.sendMessage(finalUid), 10 * uid, TimeUnit.MILLISECONDS); //broker에서 데이터 가져올 때 10ms 걸린다고 가정
        }

        // Awaitility로 비동기 작업 완료 대기
        Awaitility.await()
            .atMost(Duration.ofMillis(30000 + size * 10))
            .pollInterval(Duration.ofMillis(500)) // 500ms마다 상태 확인
            .until(() -> messageResultRepository.count() == size); // 저장된 메시지 수가 예상 크기와 같아질 때까지 대기

        // 검증
        assertEquals(size, messageResultRepository.count());
    }

}