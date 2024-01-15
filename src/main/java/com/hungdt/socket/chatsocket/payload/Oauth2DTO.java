package com.hungdt.socket.chatsocket.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Oauth2DTO {
    private String name;
    private String username;
    private String password;
    private String oauthName;
}
