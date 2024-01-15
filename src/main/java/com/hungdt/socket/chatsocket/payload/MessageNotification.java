package com.hungdt.socket.chatsocket.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class MessageNotification {
    private Long id;
    private Long senderId;
    private Long recipientId;
    private String content;
}
