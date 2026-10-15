package org.example.mapper;

import org.example.dto.response.ChatMessageResponse;
import org.example.entity.chatbot.ChatMessage;

@lombok.experimental.UtilityClass
public class ChatMessageMapper {



    public static ChatMessageResponse toResponse(ChatMessage message) {
        return new ChatMessageResponse(
                message.getId(),
                message.getSender(),
                message.getContent(),
                message.getCreatedAt(),
                java.util.List.of()
        );
    }
}