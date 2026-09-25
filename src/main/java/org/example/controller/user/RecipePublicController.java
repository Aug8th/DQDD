package org.example.controller.user;

import java.util.List;
import org.example.dto.response.RecipeResponse;
import org.example.repository.RecipeRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/recipes")
public class RecipePublicController {
    private final RecipeRepository recipes;

    public RecipePublicController(RecipeRepository recipes) {
        this.recipes = recipes;
    }

    @GetMapping("/public")
    public List<RecipeResponse> list() {
        return recipes.findByApprovalStatusOrderByCreatedAtDesc("approved")
                .stream()
                .map(RecipeResponse::from)
                .toList();
    }
}
