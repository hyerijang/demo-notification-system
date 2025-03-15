package com.example.demo.notifications.system.batch.controller;

import com.example.demo.notifications.system.batch.dto.MessageDto;
import com.example.demo.notifications.system.batch.dto.MessageMethod;
import com.example.demo.notifications.system.batch.kafka.MessageProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/batch")
@Slf4j
@RequiredArgsConstructor
public class BatchController {

    private final MessageProducer producer;

    @PostMapping()
    public String batch(@RequestParam Long size) {
        // size 명에게 메시지 전송
        for (long i = 0; i < size; i++) {
            this.producer.sendMessage(MessageDto.builder()
                .uid(i)
                .content("오늘 청약 예정인 공모주 3개")
                .messageMethod(MessageMethod.TALK)
                .build());
        }
        return "success";
    }


}