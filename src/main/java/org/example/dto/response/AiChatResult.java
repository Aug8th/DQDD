package org.example.dto.response;

import java.util.List;

public record AiChatResult(
        String answer,
        List<Integer> suggestedRecipeIds,
        List<GeneratedRecipe> generatedRecipes
) {

    public record GeneratedRecipe(
            String name,
            String description,
            String dishType,
            String region,
            String country,
            Integer prepTime,
            Integer servings,
            String difficulty,
            List<String> steps,
            Integer caloriesKcal,
            String cookingTip,
            List<Ingredient> ingredients
    ) {
    }

    public record Ingredient(
            String ingredientName,
            String quantity
    ) {
    }
}