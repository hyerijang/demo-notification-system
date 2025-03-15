package com.example.demo.notifications.system.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class MessageDto {
    Long uid;
    String content;
    MessageMethod messageMethod;
}

