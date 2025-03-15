package com.example.demo.notifications.system.iporeminder.service;


import com.example.demo.notifications.system.iporeminder.dto.MessageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Slf4j
@Service
public class MessageService {

    private static final Logger log = LoggerFactory.getLogger(MessageService.class);
    private final MessageResultService messageResultService;
    private final WebClient webClient;

    @Async("messageSenderThreadPoolTaskExecutor")
    public void sendMessage(MessageDto messageDto){
        Long uid = messageDto.getUid();
        if (messageResultService.sentToday(uid)) {
            log.warn("이미 발송한 유저 : {}", uid);
            return;
        }

        messageResultService.init(uid);

        webClient.post()
            .uri("http://localhost:8081/message-platform")
            .body(Mono.just(messageDto), MessageDto.class)
            .retrieve()
            .bodyToMono(Void.class)
            .doOnSuccess(response -> log.info("Message sent successfully for UID: {}", uid))
            .doOnError(error -> log.error("Failed to send message for UID: {}", uid, error))
            .subscribe();

    }



}
