package org.example.service.chatbot;

import jakarta.persistence.EntityManager;
import org.example.dto.request.AiRecipePublishRequest;
import org.example.dto.response.AiRecipeResponse;
import org.example.dto.response.RecipeResponse;
import org.example.entity.indoor.Recipe;
import org.example.entity.indoor.RecipeIngredient;
import org.example.mapper.AiRecipeMapper;
import org.example.mapper.RecipeMapper;
import org.example.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class AiRecipeAdminService {

    private final AiRecipeRepository aiRecipes;
    private final AiRecipeIngredientRepository aiIngredients;
    private final RecipeRepository recipes;
    private final RecipeIngredientRepository ingredients;
    private final CategoryRepository categories;
    private final EntityManager entityManager;

    public AiRecipeAdminService(
            AiRecipeRepository aiRecipes,
            AiRecipeIngredientRepository aiIngredients,
            RecipeRepository recipes,
            RecipeIngredientRepository ingredients,
            CategoryRepository categories,
            EntityManager entityManager
    ) {
        this.aiRecipes = aiRecipes;
        this.aiIngredients = aiIngredients;
        this.recipes = recipes;
        this.ingredients = ingredients;
        this.categories = categories;
        this.entityManager = entityManager;
    }

    @Transactional(readOnly = true)
    public List<AiRecipeResponse> list() {
        return aiRecipes.findAllByOrderByCreatedAtDescIdDesc().stream()
                .map(recipe -> AiRecipeMapper.toResponse(
                        recipe,
                        aiIngredients.findByRecipeIdOrderByIdAsc(recipe.getId())
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public AiRecipeResponse get(Integer id) {
        var recipe = aiRecipes.findById(id)
                .orElseThrow(() -> notFound());

        return AiRecipeMapper.toResponse(
                recipe,
                aiIngredients.findByRecipeIdOrderByIdAsc(id)
        );
    }

    @Transactional
    public RecipeResponse publish(Integer id, AiRecipePublishRequest request) {
        var source = aiRecipes.findForPublishing(id)
                .orElseThrow(() -> notFound());

        if (source.getPublishedRecipe() != null) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "The AI recipe has already been published to the recipe book"
            );
        }

        Integer categoryId = request == null || request.categoryId() == null
                ? source.getCategoryId()
                : request.categoryId();

        if (categoryId != null && !categories.existsById(categoryId)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Category does not exist"
            );
        }

        var sourceIngredients = aiIngredients.findByRecipeIdOrderByIdAsc(id);

        if (sourceIngredients.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Ingredients are required before publishing to the recipe book"
            );
        }

        if (source.getInstructions()
                .getBytes(java.nio.charset.StandardCharsets.UTF_8).length > 65535) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Cooking instructions exceed the recipe book TEXT column limit"
            );
        }

        var recipe = new Recipe();
        recipe.setName(source.getName());
        recipe.setDescription(source.getDescription());
        recipe.setCaloriesKcal(source.getCaloriesKcal());
        recipe.setCookingTip(source.getCookingTip());
        recipe.setCategoryId(categoryId);
        recipe.setDishType(source.getDishType());
        recipe.setRegion(source.getRegion());
        recipe.setCountry(source.getCountry());
        recipe.setPrepTime(source.getPrepTime());
        recipe.setServings(source.getServings());
        recipe.setDifficulty(source.getDifficulty());
        recipe.setInstructions(source.getInstructions());
        recipe.setImageUrl(source.getImageUrl());
        recipe.setVideoUrl(source.getVideoUrl());
        recipe.setSubmittedBy(source.getUser());
        recipe.setApprovalStatus("approved");
        recipe.setRecommended(
                request != null && Boolean.TRUE.equals(request.recommended())
        );

        recipes.saveAndFlush(recipe);

        var copiedIngredients = sourceIngredients.stream()
                .map(i -> new RecipeIngredient(
                        recipe,
                        i.getIngredientName(),
                        i.getQuantity()
                ))
                .toList();

        ingredients.saveAllAndFlush(copiedIngredients);

        source.setPublishedRecipe(recipe);
        aiRecipes.flush();
        entityManager.refresh(recipe);

        return RecipeMapper.toResponse(recipe);
    }

    @Transactional
    public RecipeResponse recommend(Integer id, Boolean recommended) {
        if (recommended == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "recommended is required"
            );
        }

        var recipe = recipes.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Recipe not found"
                ));

        if (recommended && !"approved".equals(recipe.getApprovalStatus())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Only approved recipes can be recommended"
            );
        }

        recipe.setRecommended(recommended);

        return RecipeMapper.toResponse(recipe);
    }

    private ResponseStatusException notFound() {
        return new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "AI recipe not found"
        );
    }
}