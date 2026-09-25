package org.example.controller.user;

import java.util.List;
import org.example.dto.request.RecipeRequest;
import org.example.dto.response.RecipeResponse;
import org.example.entity.indoor.Recipe;
import org.example.repository.RecipeRepository;
import org.example.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/recipes/submissions")
public class RecipeSubmissionController {
    private final RecipeRepository recipes;
    private final UserRepository users;

    public RecipeSubmissionController(RecipeRepository recipes, UserRepository users) {
        this.recipes = recipes;
        this.users = users;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RecipeResponse submit(Authentication auth, @RequestBody RecipeRequest request) {
        if (request == null || request.name() == null || request.name().isBlank()
                || request.name().length() > 255
                || request.instructions() == null || request.instructions().isBlank()
                || (request.prepTime() != null && request.prepTime() < 0)
                || (request.servings() != null && request.servings() < 1)
                || (request.difficulty() != null
                && !List.of("dễ", "trung bình", "khó").contains(request.difficulty()))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid recipe fields");
        }
        if (request.imageUrl() != null && !request.imageUrl().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Upload the image after creating the recipe using /api/uploads/recipes/{id}/image");
        }

        var user = users.findByEmail(auth.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        var recipe = new Recipe();
        recipe.setSubmittedBy(user);
        recipe.setApprovalStatus("pending");
        recipe.setName(request.name().trim());
        recipe.setInstructions(request.instructions().trim());
        recipe.setCategoryId(request.categoryId());
        recipe.setPrepTime(request.prepTime());
        recipe.setServings(request.servings());
        recipe.setDifficulty(request.difficulty());
        recipe.setVideoUrl(request.videoUrl());

        return RecipeResponse.from(recipes.saveAndFlush(recipe));
    }
}
