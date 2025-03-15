package com.example.demo.notifications.system.iporeminder.dto;

import com.example.demo.notifications.system.iporeminder.domain.MessageStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageResultDto {
    Long uid;
    MessageMethod messageMethod;
    MessageStatus messageStatus;
}
