package com.example.demo.notifications.system.controller;

import com.example.demo.notifications.system.MessageDto;
import com.example.demo.notifications.system.kafka.messageProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/kafka")
@Slf4j
public class KafkaController {

    private final messageProducer producer;

    @Autowired
    KafkaController(messageProducer producer) {
        this.producer = producer;
    }

    @PostMapping
    public String sendMessage(@RequestBody MessageDto message) {
        log.info("message : {}", message);
        this.producer.sendMessage(message);
        return "success";
    }

}