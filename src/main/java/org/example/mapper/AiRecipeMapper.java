package org.example.mapper;

import org.example.dto.response.RecipeIngredientResponse;
import org.example.dto.response.AiRecipeResponse;
import org.example.dto.response.AiRecipeSummaryResponse;
import org.example.entity.chatbot.AiRecipe;
import org.example.entity.chatbot.AiRecipeIngredient;

import java.util.List;

@lombok.experimental.UtilityClass
public class AiRecipeMapper {



    public static AiRecipeSummaryResponse toSummaryResponse(
            AiRecipe recipe
    ) {
        return new AiRecipeSummaryResponse(
                recipe.getId(),
                recipe.getName(),
                recipe.getDescription(),
                recipe.getPrepTime(),
                recipe.getServings(),
                recipe.getDifficulty(),
                recipe.getCreatedAt(),
                recipe.getDishType(),
                recipe.getRegion(),
                recipe.getCountry(),
                recipe.getCaloriesKcal(),
                recipe.getPublishedRecipe() == null ? null : recipe.getPublishedRecipe().getId()
        );
    }

    public static RecipeIngredientResponse toIngredientResponse(
            AiRecipeIngredient ingredient
    ) {
        return new RecipeIngredientResponse(
                ingredient.getId(),
                ingredient.getIngredientName(),
                ingredient.getQuantity()
        );
    }

    public static AiRecipeResponse toResponse(
            AiRecipe recipe,
            List<AiRecipeIngredient> ingredients
    ) {
        Integer sourceMessageId = recipe.getSourceMessage() == null
                ? null
                : recipe.getSourceMessage().getId();

        List<RecipeIngredientResponse> ingredientResponses = ingredients
                .stream()
                .map(AiRecipeMapper::toIngredientResponse)
                .toList();

        return new AiRecipeResponse(
                recipe.getId(),
                sourceMessageId,
                recipe.getName(),
                recipe.getDescription(),
                recipe.getPrepTime(),
                recipe.getServings(),
                recipe.getDifficulty(),
                recipe.getInstructions(),
                ingredientResponses,
                recipe.getCreatedAt(),
                recipe.getCategoryId(),
                recipe.getDishType(),
                recipe.getRegion(),
                recipe.getCountry(),
                recipe.getImageUrl(),
                recipe.getVideoUrl(),
                recipe.getPublishedRecipe() == null ? null : recipe.getPublishedRecipe().getId(),
                recipe.getPublishedRecipe() != null && recipe.getPublishedRecipe().isRecommended(),
                recipe.getCaloriesKcal(),
                recipe.getCookingTip(),
                RecipeSteps.fromInstructions(recipe.getInstructions())
        );
    }
}