package org.example.dto.request;

public record FeedbackRequest(
        Integer userId,
        Integer rating,
        String comment
) {
}