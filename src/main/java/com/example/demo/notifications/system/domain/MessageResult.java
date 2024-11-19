package com.example.demo.notifications.system.domain;

import com.example.demo.notifications.system.common.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(indexes = @Index(name = "idx_uid_created_at", columnList = "uid, createdAt"))
public class MessageResult extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    Long uid;
    @Enumerated(value = EnumType.STRING)
    MessageStatus status;

    String messageId;

    LocalDateTime notifiedAt;

    public MessageResult(Long uid) {
        this.uid = uid;
        this.status = MessageStatus.NOT_YET_SENT;
    }

    public void setStatusWithSuccess() {
        this.status = MessageStatus.SUCCESS;
    }

    public void setStatusWithFail() {
        this.status = MessageStatus.FAIL;
    }

    public void setMessageId(String messageId){
        this.messageId = messageId;
    }

    public void setNotifiedAt(){
        this.notifiedAt = LocalDateTime.now();
    }
}

