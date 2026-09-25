package org.example.dto.response.indoor;
import lombok.Data;

import java.util.List;

@Data
public class RecipeRecommendationResponseDto {
    // 1. Những món nấu được ngay (đủ 100% nguyên liệu)
    private List<RecipeResponseDto> readyToCook;

    // 2. Những món thiếu nguyên liệu kèm danh sách chi tiết thiếu
    private List<MissingRecipeDto> missingIngredientsRecipes;

    @Data
    public static class MissingRecipeDto {
        private RecipeResponseDto recipe;
        // Các nguyên liệu còn thiếu cần đi chợ
        private List<String> missingIngredients;
    }
}