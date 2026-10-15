package org.example.dto.response;

import java.time.LocalDateTime;

public record ChatSessionResponse(
        Integer id,
        String title,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}