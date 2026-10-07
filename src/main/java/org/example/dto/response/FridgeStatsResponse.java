package org.example.dto.response;

import java.util.List;

public record FridgeStatsResponse(
        long totalItems,
        long coolerItems,
        long freezerItems,
        long expiredItems,
        long expiringWithinThreeDays,
        List<IngredientCount> mostStoredIngredients
) {
    public record IngredientCount(String name, long count) {
    }
}
