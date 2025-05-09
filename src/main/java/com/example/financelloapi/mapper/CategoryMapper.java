package com.example.financelloapi.mapper;

import com.example.financelloapi.dto.request.CategoryRequest;
import com.example.financelloapi.dto.test.CategoryResponse;
import com.example.financelloapi.model.entity.Category;
import com.example.financelloapi.model.entity.User;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

    public CategoryResponse toCategoryResponse(Category category) {
        return new CategoryResponse(
                category.getName(),
                category.getDescription()
        );
    }

    public Category toCategoryEntity(CategoryRequest request, User user) {
        Category category = new Category();
        category.setName(request.name());
        category.setDescription(request.description());
        category.setUser(user);
        return category;
    }
}
