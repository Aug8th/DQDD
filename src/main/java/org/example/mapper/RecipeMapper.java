package org.example.mapper;

import org.example.dto.response.RecipeResponse;
import org.example.entity.indoor.Recipe;

@org.springframework.stereotype.Component
@lombok.RequiredArgsConstructor
public class RecipeMapper {

    public static RecipeResponse toResponse(Recipe r) {
        return new RecipeResponse(
                r.getId(),
                r.getCategoryId(),
                r.getName(),
                r.getPrepTime(),
                r.getServings(),
                r.getDifficulty(),
                r.getInstructions(),
                r.getVideoUrl(),
                r.getImageUrl(),
                r.getDishType(),
                r.getRegion(),
                r.getCountry(),
                r.getSubmittedBy() == null ? null : r.getSubmittedBy().getId(),
                r.getApprovalStatus(),
                r.getCreatedAt(),
                r.getIngredients().stream()
                        .map(i -> new org.example.dto.response.RecipeIngredientResponse(
                                i.getId(), i.getIngredientName(), i.getQuantity()))
                        .toList(),
                r.isRecommended(),
                r.getCaloriesKcal(),
                r.getCookingTip(),
                RecipeSteps.fromInstructions(r.getInstructions()),
                r.getDescription()
        );
    }

    public org.example.dto.response.indoor.RecipeResponseDto toResponseDto(Recipe recipe) {
        if (recipe == null) { return null; }
        var dto = new org.example.dto.response.indoor.RecipeResponseDto();
        dto.setId(recipe.getId());
        dto.setCategoryId(recipe.getCategoryId());
        dto.setName(recipe.getName());
        dto.setPrepTime(recipe.getPrepTime());
        dto.setServings(recipe.getServings());
        dto.setDifficulty(recipe.getDifficulty());
        dto.setInstructions(recipe.getInstructions());
        dto.setVideoUrl(recipe.getVideoUrl());
        dto.setDishType(recipe.getDishType() == null ? null : Recipe.DishType.valueOf(recipe.getDishType()));
        dto.setRegion(recipe.getRegion() == null ? null : Recipe.Region.valueOf(recipe.getRegion()));
        dto.setCountry(recipe.getCountry());
        dto.setImageUrl(recipe.getImageUrl());
        dto.setDescription(recipe.getDescription());
        dto.setCaloriesKcal(recipe.getCaloriesKcal());
        dto.setCookingTip(recipe.getCookingTip());
        dto.setSteps(RecipeSteps.fromInstructions(recipe.getInstructions()));
        dto.setRecommended(recipe.isRecommended());
        dto.setIngredients(recipe.getIngredients().stream().map(i -> {
            var ingredient = new org.example.dto.request.indoor.RecipeIngredientDto();
            ingredient.setId(i.getId());
            ingredient.setIngredientName(i.getIngredientName());
            ingredient.setQuantity(i.getQuantity());
            return ingredient;
        }).toList());
        return dto;
    }
}
