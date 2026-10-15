package org.example.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record AiRecipeResponse(
        Integer id,
        Integer sourceMessageId,
        String name,
        String description,
        Integer prepTime,
        Integer servings,
        String difficulty,
        String instructions,
        List<RecipeIngredientResponse> ingredients,
        LocalDateTime createdAt,
        Integer categoryId,
        String dishType,
        String region,
        String country,
        String imageUrl,
        String videoUrl,
        Integer publishedRecipeId,
        boolean recommended,
        Integer caloriesKcal,
        String cookingTip,
        java.util.List<String> steps
) {
}