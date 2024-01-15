package com.hungdt.socket.chatsocket.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Message {
    private String content;
    private Long sender;
    private Long recipient;
    private Date timestamp;
}
