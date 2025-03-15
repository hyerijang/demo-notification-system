package com.example.demo.notifications.system.iporeminder.kafka;

import com.example.demo.notifications.system.iporeminder.dto.MessageResultDto;
import com.example.demo.notifications.system.iporeminder.service.MessageResultService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MessageResultConsumer {
    private final MessageResultService messageResultService;

    @KafkaListener(topics = "dev.ipo-message-platform-result.v1", groupId = "${spring.kafka.consumer.group-id}", containerFactory = "kafkaResultListenerContainerFactory")
    public void consume(MessageResultDto messageResultDto) {
        log.info("[전송 결과 수신] Consumed message: {} {} {}", messageResultDto.getUid(), messageResultDto.getUid(),
            messageResultDto.getMessageStatus());
        messageResultService.handleResult(messageResultDto);
    }
}