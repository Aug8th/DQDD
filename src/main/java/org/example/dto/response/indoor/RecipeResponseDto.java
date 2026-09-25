package org.example.dto.response.indoor;

import lombok.Data;
import org.example.dto.request.indoor.RecipeIngredientDto;

import java.util.List;

@Data
public class RecipeResponseDto {
    private Integer id;
    private Integer categoryId;
    private String name;
    private Integer prepTime;
    private Integer servings;
    private String difficulty;
    private String instructions;
    private String videoUrl;
    // Danh sách nguyên liệu cần thiết
    private List<RecipeIngredientDto> ingredients;
}