package org.example.mapper;

import org.example.dto.response.RecipeResponse;
import org.example.entity.indoor.Recipe;

public final class RecipeMapper {
    private RecipeMapper() {
    }

    public static RecipeResponse toResponse(Recipe r) {
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
                r.getDishType(),
                r.getRegion(),
                r.getCountry(),
                r.getSubmittedBy() == null ? null : r.getSubmittedBy().getId(),
                r.getApprovalStatus(),
                r.getCreatedAt()
        );
    }
}
