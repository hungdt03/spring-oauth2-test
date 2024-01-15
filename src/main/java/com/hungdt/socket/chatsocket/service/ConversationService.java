package com.hungdt.socket.chatsocket.service;

import com.hungdt.socket.chatsocket.model.ChatMessage;
import com.hungdt.socket.chatsocket.model.Conversation;
import com.hungdt.socket.chatsocket.repository.ConversationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ConversationService {

    private final ConversationRepository conversationRepository;

    public Optional<String> getChatId(Long senderId, Long recipientId, boolean createIfNotExist) {
        return conversationRepository
                .findBySenderIdAndRecipientId(senderId, recipientId)
                .map(Conversation::getChatId)
                .or(() -> {
                    if(createIfNotExist) {
                        String chatId = createChatId(senderId, recipientId);
                        return Optional.of(chatId);
                    }

                    return Optional.empty();
                });
    }

    private String createChatId(Long senderId, Long recipientId) {
        String chatId = String.format("%s_%s", senderId, recipientId);
        Conversation senderRecipient = new Conversation();
        senderRecipient.setSenderId(senderId);
        senderRecipient.setChatId(chatId);
        senderRecipient.setRecipientId(recipientId);
        conversationRepository.save(senderRecipient);

        Conversation recipientSender = new Conversation();
        recipientSender.setSenderId(recipientId);
        recipientSender.setChatId(chatId);
        recipientSender.setRecipientId(senderId);
        conversationRepository.save(recipientSender);

        return chatId;

    }

}
