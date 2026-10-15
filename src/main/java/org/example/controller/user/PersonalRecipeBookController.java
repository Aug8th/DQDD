package org.example.controller.user;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.dto.response.AiRecipeResponse;
import org.example.dto.response.AiRecipeSummaryResponse;
import org.example.service.CurrentUserService;
import org.example.service.chatbot.AiRecipeStorageService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/recipe-book/mine")
@RequiredArgsConstructor
public class PersonalRecipeBookController {
    private final AiRecipeStorageService recipes;
    private final CurrentUserService currentUser;

    @GetMapping
    public List<AiRecipeSummaryResponse> list(Authentication auth) {
        return recipes.getMyRecipes(currentUser.id(auth));
    }

    @GetMapping("/{id}")
    public AiRecipeResponse detail(Authentication auth, @PathVariable Integer id) {
        return recipes.getMyRecipe(currentUser.id(auth), id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(Authentication auth, @PathVariable Integer id) {
        recipes.deleteMyRecipe(currentUser.id(auth), id);
    }
}
