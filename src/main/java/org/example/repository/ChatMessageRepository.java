package org.example.repository;

import org.example.entity.chatbot.ChatMessage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

@org.springframework.stereotype.Repository
public interface ChatMessageRepository
        extends JpaRepository<ChatMessage, Integer> {

    List<ChatMessage> findBySessionIdAndSessionUserIdOrderByIdAsc(
            Integer sessionId,
            Integer userId
    );

    List<ChatMessage> findBySessionIdAndSessionUserIdOrderByIdDesc(
            Integer sessionId,
            Integer userId,
            Pageable pageable
    );
}