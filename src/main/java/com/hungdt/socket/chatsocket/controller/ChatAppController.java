package com.hungdt.socket.chatsocket.controller;

import com.hungdt.socket.chatsocket.model.ChatMessage;
import com.hungdt.socket.chatsocket.model.User;
import com.hungdt.socket.chatsocket.payload.DisconnectRequest;
import com.hungdt.socket.chatsocket.payload.Message;
import com.hungdt.socket.chatsocket.payload.MessageNotification;
import com.hungdt.socket.chatsocket.security.CustomUserDetails;
import com.hungdt.socket.chatsocket.service.ChatMessageService;
import com.hungdt.socket.chatsocket.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;


@Controller
@Slf4j
@RequiredArgsConstructor
public class ChatAppController {

    private final ChatMessageService chatMessageService;
    private final SimpMessagingTemplate messagingTemplate;
    private final UserService userService;

    @MessageMapping("/chat")
    public void sendMessage(@Payload Message message) {
        ChatMessage savedMsg = chatMessageService.processMessage(message);
        log.info("Send for: " + message.getRecipient() + "/queue/messages");
        messagingTemplate.convertAndSendToUser(
                String.valueOf(message.getRecipient()), "/queue/messages",
                new MessageNotification(
                        savedMsg.getId(),
                        savedMsg.getSenderId(),
                        savedMsg.getRecipientId(),
                        savedMsg.getContent()
                )
        );

        log.info("Sending for user");
    }

    @GetMapping("/messages/{senderId}/{recipientId}")
    public ResponseEntity<List<ChatMessage>> findChatMessages(@PathVariable Long senderId,
                                                              @PathVariable Long recipientId) {
        return ResponseEntity
                .ok(chatMessageService.findChatMessages(senderId, recipientId));
    }

    @GetMapping("/login")
    public String showLoginPage(@AuthenticationPrincipal UserDetails userDetails) {

        CustomUserDetails current = (CustomUserDetails) userDetails;
        if(current != null) {
            log.info(current.getUsername());
            return "redirect:/";
        }

        return "login";
    }


    @GetMapping("/register")
    public String showRegisterPage() {
        return "register";
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }



}
