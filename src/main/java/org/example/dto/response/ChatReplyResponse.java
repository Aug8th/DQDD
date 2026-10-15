package org.example.dto.response;

import java.util.List;

public record ChatReplyResponse(
        Integer sessionId,
        ChatMessageResponse userMessage,
        ChatMessageResponse assistantMessage,
        List<RecipeResponse> suggestedRecipes,
        List<AiRecipeResponse> generatedRecipes
) {
}