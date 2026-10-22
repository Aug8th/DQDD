package org.example.controller.user;
import org.example.dto.response.RecipeResponse;
import org.example.mapper.RecipeMapper;
import org.example.repository.RecipeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
@RestController
@RequestMapping("/api/member4/recipes")
public class MemberRecipeController {
    private final RecipeRepository recipes;
    public MemberRecipeController(RecipeRepository recipes) { this.recipes=recipes; }
    @GetMapping("/{id}") @Transactional(readOnly=true)
    public RecipeResponse get(@PathVariable Integer id) {
        var recipe=recipes.findById(id).filter(r->"approved".equals(r.getApprovalStatus())).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Recipe not found"));
        return RecipeMapper.toResponse(recipe);
    }
}
