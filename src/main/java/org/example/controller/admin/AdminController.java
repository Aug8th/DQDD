package org.example.controller.admin;

import java.util.List;
import org.example.dto.request.RecipeReviewRequest;
import org.example.dto.response.RecipeResponse;
import org.example.dto.response.UserResponse;
import org.example.repository.RecipeRepository;
import org.example.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final UserRepository users;
    private final RecipeRepository recipes;

    public AdminController(UserRepository users, RecipeRepository recipes) {
        this.users = users;
        this.recipes = recipes;
    }

    @GetMapping("/users")
    public List<UserResponse> users() {
        return users.findAll().stream().map(UserResponse::from).toList();
    }

    @GetMapping("/recipes/submissions")
    public List<RecipeResponse> submissions(@RequestParam(required = false) String status) {
        if (status != null && !List.of("pending", "approved", "rejected").contains(status)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid status");
        }

        var items = status == null
                ? recipes.findBySubmittedByIsNotNullOrderByCreatedAtDesc()
                : recipes.findBySubmittedByIsNotNullAndApprovalStatusOrderByCreatedAtDesc(status);
        return items.stream().map(RecipeResponse::from).toList();
    }

    @Transactional
    @PatchMapping("/recipes/{id}/approval")
    public RecipeResponse review(@PathVariable Integer id, @RequestBody RecipeReviewRequest body) {
        if (body == null || body.status() == null
                || !List.of("approved", "rejected").contains(body.status())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Use approved or rejected");
        }

        var recipe = recipes.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Recipe not found"));
        if (recipe.getSubmittedBy() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not a user submission");
        }
        if (!"pending".equals(recipe.getApprovalStatus())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Already reviewed");
        }

        recipe.setApprovalStatus(body.status());
        return RecipeResponse.from(recipe);
    }
}
