package org.example.mapper;

import org.example.dto.response.CategoryResponse;
import org.example.entity.indoor.Category;

public final class CategoryMapper {
    private CategoryMapper() {
    }

    public static CategoryResponse toResponse(Category c) {
        return new CategoryResponse(
                c.getId(),
                c.getName()
        );
    }
}
