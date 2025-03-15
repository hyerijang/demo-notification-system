package com.example.demo.notifications.system.repository;

import com.example.demo.notifications.system.domain.MessageResult;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageResultRepository extends JpaRepository<MessageResult, Long> {

    Boolean existsByUidAndCreatedAtBetween(Long uid, LocalDateTime start, LocalDateTime end);

    Optional<MessageResult>  findFirstByUidAndCreatedAtBetweenOrderByCreatedAtDesc(Long uid, LocalDateTime start, LocalDateTime end);

    Boolean existsByUid(Long uid);
}
