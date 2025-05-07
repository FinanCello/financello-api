package com.example.financelloapi.mapper;

import com.example.financelloapi.dto.request.CategoryRequest;
import com.example.financelloapi.dto.response.CategoryResponse;
import com.example.financelloapi.model.entity.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

    public CategoryResponse toCategoryResponse(Category category) {
        return new CategoryResponse(
                category.getName(),
                category.getDescription()
        );
    }

    public Category toCategoryEntity(CategoryRequest request) {
        Category category = new Category();
        category.setName(request.name());
        category.setDescription(request.description());
        return category;
    }
}
