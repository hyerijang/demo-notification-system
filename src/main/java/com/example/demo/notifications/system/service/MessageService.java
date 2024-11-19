package com.example.demo.notifications.system.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@RequiredArgsConstructor
@Slf4j
@Service
public class MessageService {

    private static final Logger log = LoggerFactory.getLogger(MessageService.class);
    private final MessageResultService messageResultService;

    @Transactional
    @Async("messageSenderThreadPoolTaskExecutor")
    public void send(Long uid) {
//        log.info("executing Thread Name .. [{}] : 유저 {}", Thread.currentThread().getName(), uid);
        messageResultService.init(uid);
        try {
            Thread.sleep(20); // 메세지 플랫폼과의 통신에 20ms 걸린다고 가정
            messageResultService.changeToSuccess(uid);
            Thread.sleep(10); // tps 조절 : 10ms = 0.01초 대기
//            log.info("발송완료 [{}] : 유저 {}", Thread.currentThread().getName(), uid);

        } catch (Exception e) {
            messageResultService.changeToFail(uid);
            log.error("발송실패 [{}] : 유저 {} , {}", Thread.currentThread().getName(), uid, e.getStackTrace());
        }
    }

}
