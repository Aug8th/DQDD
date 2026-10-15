package org.example.mapper;

import org.example.dto.response.ChatSessionResponse;
import org.example.entity.chatbot.ChatSession;

@lombok.experimental.UtilityClass
public class ChatSessionMapper {



    public static ChatSessionResponse toResponse(ChatSession session) {
        return new ChatSessionResponse(
                session.getId(),
                session.getTitle(),
                session.getCreatedAt(),
                session.getUpdatedAt()
        );
    }
}