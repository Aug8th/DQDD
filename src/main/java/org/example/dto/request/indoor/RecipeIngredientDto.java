package org.example.dto.request.indoor;

import lombok.Data;

@Data
public class RecipeIngredientDto {
    private Integer id;
    private String ingredientName;
    private String quantity;
}