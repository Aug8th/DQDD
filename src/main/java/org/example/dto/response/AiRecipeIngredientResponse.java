package org.example.dto.response;

public record AiRecipeIngredientResponse(
        Integer id,
        String ingredientName,
        String quantity
) {
}