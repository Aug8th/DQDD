package org.example.controller.admin;

import java.util.List;
import org.example.dto.request.RecipeAdminRequest;
import org.example.dto.response.RecipeResponse;
import org.example.entity.indoor.Recipe;
import org.example.mapper.RecipeMapper;
import org.example.repository.CategoryRepository;
import org.example.repository.RecipeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/admin/recipes")
public class RecipeAdminController {
    private final RecipeRepository recipes;
    private final CategoryRepository categories;

    public RecipeAdminController(RecipeRepository recipes, CategoryRepository categories) {
        this.recipes = recipes;
        this.categories = categories;
    }

    @GetMapping
    @Transactional(readOnly = true)
    public List<RecipeResponse> list(@RequestParam(required = false) Integer categoryId,
                                     @RequestParam(required = false) String dishType,
                                     @RequestParam(required = false) String region,
                                     @RequestParam(required = false) String country) {
        return recipes.findForAdmin(categoryId, dishType, region, country).stream()
                .map(RecipeMapper::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public RecipeResponse get(@PathVariable Integer id) {
        return RecipeMapper.toResponse(find(id));
    }

    @PostMapping
    @Transactional
    @ResponseStatus(HttpStatus.CREATED)
    public RecipeResponse create(@RequestBody RecipeAdminRequest request) {
        validate(request);
        var recipe = new Recipe();
        apply(recipe, request);
        recipe.setApprovalStatus("approved");
        return RecipeMapper.toResponse(recipes.saveAndFlush(recipe));
    }

    @PutMapping("/{id}")
    @Transactional
    public RecipeResponse update(@PathVariable Integer id, @RequestBody RecipeAdminRequest request) {
        validate(request);
        var recipe = find(id);
        apply(recipe, request);
        return RecipeMapper.toResponse(recipe);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) {
        recipes.delete(find(id));
    }

    private Recipe find(Integer id) {
        return recipes.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Recipe not found"));
    }

    private void validate(RecipeAdminRequest request) {
        if (request == null || request.name() == null || request.name().isBlank()
                || request.name().trim().length() > 255
                || request.instructions() == null || request.instructions().isBlank()
                || (request.country() != null && request.country().trim().length() > 100)
                || (request.dishType() != null
                && !List.of("MAIN_DISH", "DRINK", "DESSERT").contains(request.dishType()))
                || (request.region() != null
                && !List.of("ASIAN", "EUROPEAN", "AMERICAN", "AFRICAN", "OCEANIAN", "STREET_FOOD").contains(request.region()))
                || (request.prepTime() != null && request.prepTime() < 0)
                || (request.servings() != null && request.servings() < 1)
                || (request.videoUrl() != null && request.videoUrl().length() > 255)
                || (request.difficulty() != null
                && !List.of("dễ", "trung bình", "khó").contains(request.difficulty()))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid recipe fields");
        }
        if ((request.description() != null && request.description().length() > 5000)
                || (request.cookingTip() != null && request.cookingTip().length() > 5000)
                || (request.caloriesKcal() != null && request.caloriesKcal() <= 0)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid description, calories or tip");
        }
        if (request.instructions().getBytes(java.nio.charset.StandardCharsets.UTF_8).length > 65535) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Instructions exceed TEXT size");
        }
        if (request.ingredients() != null) {
            if (request.ingredients().size() > 50) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Too many ingredients");
            }
            for (var ingredient : request.ingredients()) {
                if (ingredient == null || ingredient.ingredientName() == null
                        || ingredient.ingredientName().isBlank()
                        || ingredient.ingredientName().trim().length() > 150
                        || (ingredient.quantity() != null && ingredient.quantity().length() > 100)) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid ingredient");
                }
            }
        }
        if (request.categoryId() != null && !categories.existsById(request.categoryId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Category does not exist");
        }
    }

    private void apply(Recipe recipe, RecipeAdminRequest request) {
        if (request.ingredients() != null) {
            recipe.getIngredients().clear();
            for (var ingredient : request.ingredients()) {
                recipe.getIngredients().add(new org.example.entity.indoor.RecipeIngredient(
                        recipe, ingredient.ingredientName().trim(), ingredient.quantity()));
            }
        }
        if (request.description() != null) { recipe.setDescription(request.description().trim()); }
        if (request.caloriesKcal() != null) { recipe.setCaloriesKcal(request.caloriesKcal()); }
        if (request.cookingTip() != null) { recipe.setCookingTip(request.cookingTip().trim()); }
        recipe.setName(request.name().trim());
        recipe.setCategoryId(request.categoryId());
        recipe.setDishType(request.dishType() == null ? "MAIN_DISH" : request.dishType());
        recipe.setRegion(request.region());
        recipe.setCountry(request.country() == null || request.country().isBlank()
                ? null : request.country().trim());
        recipe.setPrepTime(request.prepTime());
        recipe.setServings(request.servings());
        recipe.setDifficulty(request.difficulty());
        recipe.setInstructions(request.instructions().trim());
        recipe.setVideoUrl(request.videoUrl());
    }
}
