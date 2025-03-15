package com.example.demo.notifications.system.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MessageDto {
    Long uid;
    String content;
    MessageMethod messageMethod;
}

