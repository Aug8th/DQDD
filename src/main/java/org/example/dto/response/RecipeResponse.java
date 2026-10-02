package org.example.dto.response;

import java.time.LocalDateTime;

public record RecipeResponse(
        Integer id,
        Integer categoryId,
        String name,
        Integer prepTime,
        Integer servings,
        String difficulty,
        String instructions,
        String videoUrl,
        String imageUrl,
        String dishType,
        String region,
        String country,
        Integer submittedByUserId,
        String approvalStatus,
        LocalDateTime createdAt
) {
}
