package org.example.dto.response;

import java.time.LocalDateTime;

public record AiRecipeSummaryResponse(
        Integer id,
        String name,
        String description,
        Integer prepTime,
        Integer servings,
        String difficulty,
        LocalDateTime createdAt,
        String dishType,
        String region,
        String country,
        Integer caloriesKcal,
        Integer publishedRecipeId
) {
}