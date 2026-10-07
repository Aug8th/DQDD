package org.example.service.indoor;

import lombok.RequiredArgsConstructor;
import org.example.dto.response.indoor.RecipeRecommendationResponseDto;
import org.example.dto.response.indoor.RecipeResponseDto;
import org.example.entity.indoor.Recipe;
import org.example.entity.indoor.RecipeIngredient;
import org.example.entity.indoor.UserFridge;
import org.example.mapper.RecipeMapper;
import org.example.repository.RecipeIngredientRepository;
import org.example.repository.RecipeRepository;
import org.example.repository.UserFridgeRepository;
import org.example.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@org.springframework.transaction.annotation.Transactional(readOnly = true)
@RequiredArgsConstructor
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final RecipeIngredientRepository recipeIngredientRepository;
    private final UserFridgeRepository userFridgeRepository;
    private final UserRepository userRepository;
    private final RecipeMapper recipeMapper;

    // 1. Lấy danh sách công thức (Có hỗ trợ lọc theo categoryId hoặc difficulty)
    public List<RecipeResponseDto> getRecipes(Integer categoryId, String difficulty) {
        List<Recipe> recipes;
        if (categoryId != null && difficulty != null) {
            recipes = recipeRepository.findByCategoryIdAndDifficulty(categoryId, difficulty);
        } else if (categoryId != null) {
            recipes = recipeRepository.findByCategoryId(categoryId);
        } else if (difficulty != null) {
            recipes = recipeRepository.findByDifficulty(difficulty);
        } else {
            recipes = recipeRepository.findAll();
        }

        return recipes.stream()
                .filter(r -> "approved".equals(r.getApprovalStatus()))
                .map(recipeMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    // 2. Xem chi tiết một công thức kèm nguyên liệu
    public RecipeResponseDto getRecipeDetail(Integer id) {
        Recipe recipe = recipeRepository.findById(id).filter(r -> "approved".equals(r.getApprovalStatus()))
                .orElseThrow(() -> new RuntimeException("Không tìm thấy công thức nhé!"));
        return recipeMapper.toResponseDto(recipe);
    }

    // 3. Tìm kiếm và lọc đa tầng (Task 2.1)
    public List<RecipeResponseDto> searchRecipes(
            String keyword,
            Recipe.DishType dishType,
            Recipe.Region region,
            String country) {
        List<Recipe> recipes = recipeRepository.searchRecipes(keyword, dishType, region, country);
        return recipes.stream()
                .filter(r -> "approved".equals(r.getApprovalStatus()))
                .map(recipeMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    // 4. API Random Dish - Chọn ngẫu nhiên 1 món ăn từ database (Task 2.4)
    public RecipeResponseDto getRandomRecipe() {
        Recipe randomRecipe = recipeRepository.findRandomRecipe()
                .orElseThrow(() -> new RuntimeException("Không có công thức nào trong cơ sở dữ liệu!"));
        return recipeMapper.toResponseDto(randomRecipe);
    }

    // 5. THUẬT TOÁN GỢI Ý MÓN ĂN TỪ TỦ LẠNH (Đã kiểm tra User tồn tại & Tủ lạnh rỗng)
    public RecipeRecommendationResponseDto recommendRecipes(Integer userId) {
        // Kiểm tra xem User có thực sự tồn tại trong hệ thống hay không
        userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng nhé!"));

        List<UserFridge> userFridges = userFridgeRepository.findByUserId(userId);

        // Kiểm tra nếu tủ lạnh không có nguyên liệu nào thì báo lỗi trực tiếp
        if (userFridges.isEmpty()) {
            throw new RuntimeException("Tủ lạnh của bạn đang trống nhé!");
        }

        Set<String> fridgeIngredientNames = userFridges.stream()
                .filter(f -> f.getExpiryDate() == null || !f.getExpiryDate().isBefore(
                        java.time.LocalDate.now(java.time.ZoneId.of("Asia/Ho_Chi_Minh"))))
                .map(f -> f.getIngredientName().trim().toLowerCase(java.util.Locale.ROOT))
                .collect(Collectors.toSet());

        List<Recipe> allRecipes = recipeRepository.findByApprovalStatusOrderByCreatedAtDesc("approved");

        List<RecipeResponseDto> readyToCook = new ArrayList<>();
        List<RecipeRecommendationResponseDto.MissingRecipeDto> missingList = new ArrayList<>();

        for (Recipe recipe : allRecipes) {
            List<RecipeIngredient> requiredIngredients = recipeIngredientRepository.findByRecipeId(recipe.getId());

            if (requiredIngredients.isEmpty()) { continue; }
            List<String> missingItems = new ArrayList<>();
            for (RecipeIngredient ri : requiredIngredients) {
                String reqName = ri.getIngredientName().trim().toLowerCase();
                if (!fridgeIngredientNames.contains(reqName)) {
                    missingItems.add(ri.getIngredientName());
                }
            }

            RecipeResponseDto recipeDto = recipeMapper.toResponseDto(recipe);

            if (missingItems.isEmpty()) {
                readyToCook.add(recipeDto);
            } else {
                RecipeRecommendationResponseDto.MissingRecipeDto missingDto =
                        new RecipeRecommendationResponseDto.MissingRecipeDto();
                missingDto.setRecipe(recipeDto);
                missingDto.setMissingIngredients(missingItems);
                missingList.add(missingDto);
            }
        }

        RecipeRecommendationResponseDto response = new RecipeRecommendationResponseDto();
        response.setReadyToCook(readyToCook);
        response.setMissingIngredientsRecipes(missingList);
        return response;
    }
}