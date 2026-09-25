package org.example.controller.user;

import org.example.repository.RecipeRepository;
import org.example.repository.UserRepository;
import org.example.service.upload.FileUpLoadService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/uploads")
public class FileUploadController {
    private final FileUpLoadService files;
    private final UserRepository users;
    private final RecipeRepository recipes;

    public FileUploadController(FileUpLoadService files, UserRepository users, RecipeRepository recipes) {
        this.files = files;
        this.users = users;
        this.recipes = recipes;
    }

    @Transactional
    @PostMapping("/avatar")
    public UploadResponse avatar(Authentication auth, @RequestParam("file") MultipartFile file) {
        var user = users.findByEmail(auth.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        String url = files.saveImage(file, "avatar");
        user.setAvatarUrl(url);
        return new UploadResponse(url);
    }

    @Transactional
    @PostMapping("/recipes/{id}/image")
    public UploadResponse recipe(Authentication auth, @PathVariable Integer id,
                                 @RequestParam("file") MultipartFile file) {
        var recipe = recipes.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Recipe not found"));
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
        boolean isOwner = recipe.getSubmittedBy() != null
                && recipe.getSubmittedBy().getEmail().equals(auth.getName());
        if (!isOwner && !isAdmin) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not allowed to edit this recipe");
        }
        if ("rejected".equals(recipe.getApprovalStatus())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Rejected recipe cannot be edited");
        }

        String url = files.saveImage(file, "recipe");
        recipe.setImageUrl(url);
        if ("approved".equals(recipe.getApprovalStatus())) {
            recipe.setApprovalStatus("pending");
        }
        return new UploadResponse(url);
    }

    public record UploadResponse(String url) {}
}
