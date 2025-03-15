package com.example.demo.notifications.system.iporeminder.controller;

import com.example.demo.notifications.system.iporeminder.dto.MessageDto;
import com.example.demo.notifications.system.iporeminder.kafka.MessageProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/kafka")
@Slf4j
@RequiredArgsConstructor
public class KafkaController {

    private final MessageProducer producer;

    // 테스트용 단건 메세지 생성 API
    @PostMapping
    public String sendMessage(@RequestBody MessageDto message) {
        this.producer.sendMessage(message);
        return "success";
    }

}