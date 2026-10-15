package org.example.dto.request;

public record AiRecipePublishRequest(
        Integer categoryId,
        Boolean recommended
) {
}
