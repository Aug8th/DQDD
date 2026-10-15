package org.example.controller.admin;

import org.example.dto.request.AiRecipePublishRequest;
import org.example.dto.request.RecipeRecommendationRequest;
import org.example.dto.response.AiRecipeResponse;
import org.example.dto.response.RecipeResponse;
import org.example.service.chatbot.AiRecipeAdminService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AiRecipeAdminController {
    private final AiRecipeAdminService service;

    public AiRecipeAdminController(AiRecipeAdminService service) {
        this.service = service;
    }

    @GetMapping("/ai-recipes")
    public List<AiRecipeResponse> list() { return service.list(); }

    @GetMapping("/ai-recipes/{id}")
    public AiRecipeResponse get(@PathVariable Integer id) { return service.get(id); }

    @PostMapping("/ai-recipes/{id}/publish")
    @ResponseStatus(HttpStatus.CREATED)
    public RecipeResponse publish(@PathVariable Integer id,
                                  @RequestBody(required = false) AiRecipePublishRequest request) {
        return service.publish(id, request);
    }

    @PatchMapping("/recipes/{id}/recommendation")
    public RecipeResponse recommend(@PathVariable Integer id,
                                    @RequestBody RecipeRecommendationRequest request) {
        return service.recommend(id, request.recommended());
    }
}
