package org.example.dto.response;

import java.time.LocalDateTime;

public record PublicFeedbackResponse(
        Integer id,
        Integer rating,
        String comment,
        LocalDateTime createdAt
) {
}
