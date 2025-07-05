package com.example.financelloapi.mapper;

import com.example.financelloapi.dto.request.CategoryRequest;
import com.example.financelloapi.dto.test.CategoryResponse;
import com.example.financelloapi.dto.test.CategorySimpleResponse;
import com.example.financelloapi.model.entity.Category;
import com.example.financelloapi.model.entity.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

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

    // devuelve nombre de categoria
    public static CategorySimpleResponse toCategorySimpleResponse(Category category) {
        return new CategorySimpleResponse(category.getId(), category.getName());
    }

    // devuelve lista con nombres de categorias
    public static List<CategorySimpleResponse> toCategorySimpleResponseList(List<Category> categories) {
        return categories.stream()
                .map(CategoryMapper::toCategorySimpleResponse)
                .collect(Collectors.toList());
    }

}

