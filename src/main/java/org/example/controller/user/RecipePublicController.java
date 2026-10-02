package org.example.controller.user;

import java.util.List;
import org.example.dto.response.RecipeResponse;
import org.example.repository.RecipeRepository;
import org.example.mapper.RecipeMapper;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/recipes")
public class RecipePublicController {
    private final RecipeRepository recipes;

    public RecipePublicController(RecipeRepository recipes) {
        this.recipes = recipes;
    }

    @GetMapping("/public")
    @Transactional(readOnly = true)
    public List<RecipeResponse> list(@RequestParam(required = false) String dishType,
                                     @RequestParam(required = false) String region,
                                     @RequestParam(required = false) String country) {
        return recipes.findPublic(dishType, region, country)
                .stream()
                .map(RecipeMapper::toResponse)
                .toList();
    }
}
