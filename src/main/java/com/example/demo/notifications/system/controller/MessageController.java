package com.example.demo.notifications.system.controller;

import com.example.demo.notifications.system.consumer.Consumer;
import java.time.LocalDate;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RequiredArgsConstructor
@RestController
public class MessageController {

    private final Consumer consumer;
    private static final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

    @GetMapping("/messages/send")
    public void send(@RequestParam(defaultValue = "1") Long size) {
        for (long uid = 0; uid < size; uid++) {
            long finalUid = uid;
            executor.schedule(() -> consumer.sendMessage(finalUid), 20 * uid, TimeUnit.MILLISECONDS);
        }
    }
}
