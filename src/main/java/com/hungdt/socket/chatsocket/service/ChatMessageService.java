package com.hungdt.socket.chatsocket.service;

import com.hungdt.socket.chatsocket.model.ChatMessage;
import com.hungdt.socket.chatsocket.model.Conversation;
import com.hungdt.socket.chatsocket.payload.Message;
import com.hungdt.socket.chatsocket.repository.ChatMessageRepository;
import com.hungdt.socket.chatsocket.repository.ConversationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatMessageService {
    private final ChatMessageRepository chatMessageRepository;
    private final ConversationService conversationService;

    public ChatMessage processMessage(Message message) {
        var chatId = conversationService
                .getChatId(message.getSender(), message.getRecipient(), true)
                .orElseThrow(); // You can create your own dedicated exception
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setChatId(chatId);
        chatMessage.setSenderId(message.getSender());
        chatMessage.setRecipientId(message.getRecipient());
        chatMessage.setContent(message.getContent());
        chatMessage.setTimestamp(message.getTimestamp());

        return chatMessageRepository.save(chatMessage);
    }

    public List<ChatMessage> findChatMessages(Long senderId, Long recipientId) {
        var chatId = conversationService.getChatId(senderId, recipientId, false);
        return chatId.map(chatMessageRepository::findByChatId).orElse(new ArrayList<>());
    }


}
