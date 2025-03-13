package com.example.demo.notifications.system.service;


import com.example.demo.notifications.system.domain.MessageResult;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Slf4j
@Service
public class MessageService {

    private static final Logger log = LoggerFactory.getLogger(MessageService.class);
    private final MessageResultService messageResultService;


    @Async("messageSenderThreadPoolTaskExecutor")
    public void sendMessage(long uid){
        if (messageResultService.sentToday(uid)) {
            log.warn("이미 발송한 유저 : {}", uid);
            return;
        }

        messageResultService.init(uid);

        //FIXME 메세지 발송 결과 수신
        try {
            Thread.sleep(20); // 네트워크 통신 20ms 가정
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        handleResult(uid, "success");
    }

    @Async("messageResultThreadPoolTaskExecutor")
    public void handleResult(Long uid, String status) {
        Optional<MessageResult> result = messageResultService.getFindResult(uid);
        if (result.isEmpty()) {
            log.error("시작정보 조회 실패 : 유저 {}", uid);
            return;
        }

        try {
            Thread.sleep(10); // tps 조절 : 10ms = 0.01초 대기
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (status.equals("success")) {
            messageResultService.changeToSuccess(result.get());
            log.info("발송완료 [{}] : 유저 {}", Thread.currentThread().getName(), uid);
            return;
        } else {
            messageResultService.changeToFail(result.get());
            log.error("발송실패 [{}] : 유저 {}", Thread.currentThread().getName(), uid);
            return;
        }
    }

}
