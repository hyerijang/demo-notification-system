package com.example.demo.notifications.system.messageplatform.kafka;



import com.example.demo.notifications.system.messageplatform.dto.MessageResultDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class MessageResultProducer {
    private static final String TOPIC = "dev.ipo-message-platform-result.v1";

    private final KafkaTemplate<String, MessageResultDto> kafkaTemplate;

    public void sendMessageResult(MessageResultDto message) {
        log.info("Produce message : {}", message);
        this.kafkaTemplate.send(TOPIC, message);
    }
}
