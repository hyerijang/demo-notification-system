package com.example.demo.notifications.system.messageplatform.controller;


import com.example.demo.notifications.system.messageplatform.dto.MessageDto;
import com.example.demo.notifications.system.messageplatform.dto.MessageResultDto;
import com.example.demo.notifications.system.messageplatform.dto.MessageStatus;
import com.example.demo.notifications.system.messageplatform.kafka.MessageResultProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/message-platform")
@Slf4j
public class MessagePlatformController {

    private final MessageResultProducer producer;

    @Autowired
    MessagePlatformController(MessageResultProducer producer) {
        this.producer = producer;
    }

    @PostMapping
    public String receive(@RequestBody MessageDto message) {
        this.producer.sendMessageResult(new MessageResultDto(message, MessageStatus.SUCCESS));
        return "success";
    }

}