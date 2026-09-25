package org.example.dto.response;

import org.example.entity.indoor.Recipe;
import java.time.LocalDateTime;

public record RecipeResponse(
        Integer id,
        Integer categoryId,
        String name,
        Integer prepTime,
        Integer servings,
        String difficulty,
        String instructions,
        String videoUrl,
        String imageUrl,
        Integer submittedByUserId,
        String approvalStatus,
        LocalDateTime createdAt)
{
    public static RecipeResponse from(Recipe r)
    {
        return new RecipeResponse(
                r.getId(),
                r.getCategoryId(),
                r.getName(),
                r.getPrepTime(),
                r.getServings(),
                r.getDifficulty(),
                r.getInstructions(),
                r.getVideoUrl(),
                r.getImageUrl(),
                r.getSubmittedBy() == null ? null: r.getSubmittedBy().getId(),
                r.getApprovalStatus(),
                r.getCreatedAt());}
}