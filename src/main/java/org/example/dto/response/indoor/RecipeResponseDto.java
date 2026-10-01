package org.example.dto.response.indoor;

import lombok.Data;
import org.example.dto.request.indoor.RecipeIngredientDto;
import org.example.entity.indoor.Recipe;

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
    private Recipe.DishType dishType;
    private Recipe.Region region;
    private String country;
    // Danh sách nguyên liệu cần thiết
    private List<RecipeIngredientDto> ingredients;
}