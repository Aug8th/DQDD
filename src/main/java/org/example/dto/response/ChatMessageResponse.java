package org.example.dto.response;

import java.time.LocalDateTime;

public record ChatMessageResponse(
        Integer id,
        String sender,
        String content,
        LocalDateTime createdAt,
        java.util.List<AiRecipeResponse> generatedRecipes
) {
}