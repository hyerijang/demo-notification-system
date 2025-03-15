package com.example.demo.notifications.system.service;

import com.example.demo.notifications.system.domain.MessageResult;
import com.example.demo.notifications.system.domain.MessageStatus;
import com.example.demo.notifications.system.dto.MessageResultDto;
import com.example.demo.notifications.system.repository.MessageResultRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Slf4j
@Service
//@Transactional(readOnly = true)
public class MessageResultService {

    private final MessageResultRepository messageResultRepository;

    LocalDate today = LocalDate.now();
    LocalDateTime startOfDay = (today).atStartOfDay();
    LocalDateTime endOfDay = today.atTime(23, 59, 59);

    public boolean sentToday(Long uid) {
        return messageResultRepository.existsByUidAndCreatedAtBetween(uid, startOfDay, endOfDay);
    }

//    @Transactional
    public void init(Long uid) {
        // 중복발송 방지 위해 메세지 발송 전 상태 저장
        MessageResult result = new MessageResult(uid);
        messageResultRepository.save(result);
    }

    void changeToSuccess(MessageResult result) {
        changeStatus(result, true);
    }

    void changeToFail(MessageResult result) {
        changeStatus(result,false);
    }

    @Transactional
    private void changeStatus(MessageResult messageResult, boolean isSuccess) {
        Long uid = messageResult.getUid();
        try {
            if (isSuccess) {
                messageResult.setStatusWithSuccess(); // 성공

                messageResult.setMessageId("message_"+ uid);
            } else {
                messageResult.setStatusWithFail(); // 실패
            }
        } catch (Exception e) {
            log.error("Failed to update status for UID: {}", uid, e);
        }
    }

    @Transactional
    private void changeStatus(Long uid, boolean isSuccess) {
            try {
                MessageResult messageResult = getFindResult(uid).get();
                if (isSuccess) {
                    messageResult.setStatusWithSuccess(); // 성공
                    messageResult.setMessageId("message_"+uid);
                } else {
                    messageResult.setStatusWithFail(); // 실패
                }
            } catch (Exception e) {
                log.error("Failed to update status for UID: {}", uid, e);
            }
    }

    Optional<MessageResult> getFindResult(Long uid) {
        return messageResultRepository.findFirstByUidAndCreatedAtBetweenOrderByCreatedAtDesc(uid, startOfDay, endOfDay);
    }

    @Async("messageResultThreadPoolTaskExecutor")
    public void handleResult(MessageResultDto messageResultDto) {
        Long uid = messageResultDto.getUid();

        Optional<MessageResult> result = getFindResult(uid);
        if (result.isEmpty()) {
            log.error("시작정보 조회 실패 : 유저 {}", uid);
            return;
        }

        if (messageResultDto.getMessageStatus() == MessageStatus.SUCCESS) {
            changeToSuccess(result.get());
            log.info("발송완료 [{}] : 유저 {}", Thread.currentThread().getName(), uid);
        } else {
            changeToFail(result.get());
            log.error("발송실패 [{}] : 유저 {}", Thread.currentThread().getName(), uid);
        }
    }
}
