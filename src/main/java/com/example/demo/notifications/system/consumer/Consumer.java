package com.example.demo.notifications.system.consumer;


import com.example.demo.notifications.system.service.MessageResultService;
import com.example.demo.notifications.system.service.MessageService;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
public class Consumer {
    private final MessageService messageService;
    private final MessageResultService messageResultService;

    @Async("consumerThreadPoolTaskExecutor")
    public void sendMessage(long uid){
        if (messageResultService.sentToday(uid)) {
            return;
        }
        messageService.send(uid);
    }
}
