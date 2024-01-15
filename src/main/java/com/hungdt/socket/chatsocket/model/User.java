package com.hungdt.socket.chatsocket.model;

import com.hungdt.socket.chatsocket.enums.Provider;
import com.hungdt.socket.chatsocket.enums.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String username;
    private String password;

    @Enumerated(EnumType.STRING)
    private Provider provider;
    private Status status;
    private String role;

//    @OneToMany(mappedBy = "sender")
//    private List<Conversation> senderConversations;
//
//    @OneToMany(mappedBy = "recipient")
//    private List<Conversation> recipientConversations;
//
//    @OneToMany(mappedBy = "senderMessage")
//    private List<ChatMessage> senderChatMessage;
//
//    @OneToMany(mappedBy = "recipientMessage")
//    private List<ChatMessage> recipientChatMessage;
}
