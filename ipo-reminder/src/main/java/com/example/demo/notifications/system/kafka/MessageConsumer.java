package com.example.demo.notifications.system.kafka;

import com.example.demo.notifications.system.dto.MessageDto;
import com.example.demo.notifications.system.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MessageConsumer {

    private final MessageService messageService;

    @KafkaListener(topics = "dev.ipo-message-request.v1", groupId = "${spring.kafka.consumer.group-id}", containerFactory = "kafkaListenerContainerFactory")
    public void consume(MessageDto message) {
        log.info("Consumed message: {}", message);
        messageService.sendMessage(message);
    }
}