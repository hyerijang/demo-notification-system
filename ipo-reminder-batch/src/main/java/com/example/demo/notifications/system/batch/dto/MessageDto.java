package com.example.demo.notifications.system.batch.dto;

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

