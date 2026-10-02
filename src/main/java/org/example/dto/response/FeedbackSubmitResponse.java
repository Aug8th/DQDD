package org.example.dto.response;

public record FeedbackSubmitResponse(
        Integer id,
        Integer rating,
        String comment
) {
}
