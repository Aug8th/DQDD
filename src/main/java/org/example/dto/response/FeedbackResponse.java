package org.example.dto.response;

import java.time.LocalDateTime;

public record FeedbackResponse(
        Integer id,
        Integer userId,
        Integer rating,
        String comment,
        String moderationStatus,
        LocalDateTime createdAt
) {
}
