package com.example.demo.notifications.system.dto;

import com.example.demo.notifications.system.domain.MessageStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageResultDto {
    Long uid;
    MessageMethod messageMethod;
    MessageStatus messageStatus;
}
