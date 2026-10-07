package org.example.dto.request;

public record RecipeRequest(
        String name,
        Integer categoryId,
        Integer prepTime,
        Integer servings,
        String difficulty,
        String instructions,
        String videoUrl,
        String imageUrl
) {
}