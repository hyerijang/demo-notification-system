package com.example.demo.notifications.system;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MessageDto {
    Long uid;
    String content;
    MessageMethod messageMethod;
}

enum MessageMethod {
    TALK,
    PUSH
}
