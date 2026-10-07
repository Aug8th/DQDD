package org.example.dto.request;

public record RecipeAdminRequest(
        String name,
        Integer categoryId,
        String dishType,
        String region,
        String country,
        Integer prepTime,
        Integer servings,
        String difficulty,
        String instructions,
        String videoUrl,
        java.util.List<RecipeIngredientRequest> ingredients,
        String description,
        Integer caloriesKcal,
        String cookingTip
) {
}
