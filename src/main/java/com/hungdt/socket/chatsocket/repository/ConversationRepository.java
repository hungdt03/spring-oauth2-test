package com.hungdt.socket.chatsocket.repository;

import com.hungdt.socket.chatsocket.model.Conversation;
import com.hungdt.socket.chatsocket.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    Optional<Conversation> findBySenderIdAndRecipientId(Long senderId, Long userId);
}
