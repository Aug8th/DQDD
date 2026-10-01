package org.example.controller.user.indoor;

import org.example.dto.response.indoor.RecipeRecommendationResponseDto;
import org.example.dto.response.indoor.RecipeResponseDto;
import org.example.entity.indoor.Recipe;
import org.example.service.indoor.RecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recipes")
public class RecipeController {

    @Autowired
    private RecipeService recipeService;

    // 1. API Lấy danh sách công thức
    @GetMapping
    public ResponseEntity<List<RecipeResponseDto>> getRecipes(
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) String difficulty) {
        List<RecipeResponseDto> recipes = recipeService.getRecipes(categoryId, difficulty);
        return ResponseEntity.ok(recipes);
    }

    // 2. API Tìm kiếm & Bộ lọc đa tầng
    // Ví dụ: GET /api/recipes/search?keyword=bò&dishType=MAIN_DISH&region=ASIAN
    @GetMapping("/search")
    public ResponseEntity<List<RecipeResponseDto>> searchRecipes(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Recipe.DishType dishType,
            @RequestParam(required = false) Recipe.Region region,
            @RequestParam(required = false) String country) {
        List<RecipeResponseDto> recipes = recipeService.searchRecipes(keyword, dishType, region, country);
        return ResponseEntity.ok(recipes);
    }

    // 3. API Random Dish - Chọn ngẫu nhiên 1 món ăn
    // Ví dụ: GET /api/recipes/random
    @GetMapping("/random")
    public ResponseEntity<?> getRandomRecipe() {
        try {
            RecipeResponseDto recipe = recipeService.getRandomRecipe();
            return ResponseEntity.ok(recipe);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // 4. API Xem chi tiết một công thức
    @GetMapping("/{id}")
    public ResponseEntity<?> getRecipeDetail(@PathVariable Integer id) {
        try {
            RecipeResponseDto recipe = recipeService.getRecipeDetail(id);
            return ResponseEntity.ok(recipe);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // 5. API Thuật toán gợi ý món ăn
    @GetMapping("/recommendations/{userId}")
    public ResponseEntity<?> recommendRecipes(@PathVariable Integer userId) {
        try {
            RecipeRecommendationResponseDto recommendations = recipeService.recommendRecipes(userId);
            return ResponseEntity.ok(recommendations);
        } catch (RuntimeException e) {
            String message = e.getMessage();
            if (message.contains("Không tìm thấy người dùng")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        }
    }
}