package org.example.mapper;

import org.example.dto.response.CategoryResponse;
import org.example.entity.indoor.Category;

@lombok.experimental.UtilityClass
public class CategoryMapper {


    public static CategoryResponse toResponse(Category c) {
        return new CategoryResponse(
                c.getId(),
                c.getName()
        );
    }
}
