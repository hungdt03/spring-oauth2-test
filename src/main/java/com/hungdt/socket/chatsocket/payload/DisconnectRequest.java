package com.hungdt.socket.chatsocket.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class DisconnectRequest {
    private Long id;
}
