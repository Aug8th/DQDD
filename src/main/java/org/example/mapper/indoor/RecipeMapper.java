package org.example.mapper.indoor;

import lombok.RequiredArgsConstructor;
import org.example.dto.request.indoor.RecipeIngredientDto;
import org.example.dto.response.indoor.RecipeResponseDto;
import org.example.entity.indoor.Recipe;
import org.example.entity.indoor.RecipeIngredient;
import org.example.repository.indoor.RecipeIngredientRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RecipeMapper {

    private final RecipeIngredientRepository recipeIngredientRepository;
    public RecipeResponseDto toResponseDto(Recipe recipe) {
        if (recipe == null) {
            return null;
        }

        RecipeResponseDto dto = new RecipeResponseDto();
        dto.setId(recipe.getId());
        dto.setCategoryId(recipe.getCategoryId());
        dto.setName(recipe.getName());
        dto.setPrepTime(recipe.getPrepTime());
        dto.setServings(recipe.getServings());
        dto.setDifficulty(recipe.getDifficulty());
        dto.setInstructions(recipe.getInstructions());
        dto.setVideoUrl(recipe.getVideoUrl());

        List<RecipeIngredient> ingredients = recipeIngredientRepository.findByRecipeId(recipe.getId());
        List<RecipeIngredientDto> ingredientDtos = ingredients.stream().map(i -> {
            RecipeIngredientDto iDto = new RecipeIngredientDto();
            iDto.setId(i.getId());
            iDto.setIngredientName(i.getIngredientName());
            iDto.setQuantity(i.getQuantity());
            return iDto;
        }).collect(Collectors.toList());

        dto.setIngredients(ingredientDtos);
        return dto;
    }
}