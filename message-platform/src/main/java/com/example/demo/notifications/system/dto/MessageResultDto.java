package com.example.demo.notifications.system.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageResultDto {
    Long uid;
    MessageMethod messageMethod;
    MessageStatus messageStatus;

    public MessageResultDto(MessageDto message , MessageStatus messageStatus) {
        this.uid = message.getUid();
        this.messageMethod = message.getMessageMethod();
        this.messageStatus = messageStatus;
    }
}
