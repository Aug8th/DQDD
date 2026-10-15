package org.example.dto.request;

public record ChatMessageRequest(
        String message,
        boolean useFridge
) {
}