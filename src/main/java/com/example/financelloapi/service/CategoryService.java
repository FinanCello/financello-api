package com.example.financelloapi.service;

import com.example.financelloapi.dto.request.CategoryRequest;
import com.example.financelloapi.dto.test.CategoryResponse;
import com.example.financelloapi.exception.CategoryAlreadyExistsException;
import com.example.financelloapi.exception.UserDoesntExistException;
import com.example.financelloapi.mapper.CategoryMapper;
import com.example.financelloapi.model.entity.Category;
import com.example.financelloapi.model.entity.User;
import com.example.financelloapi.repository.CategoryRepository;
import com.example.financelloapi.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final UserRepository userRepository;

    @Transactional
    public CategoryResponse createCategory(Integer userId, CategoryRequest request) {
        if (categoryRepository.existsByNameAndUserId(request.name(), userId)) {
            throw new CategoryAlreadyExistsException("La categoría ya existe");
        }

        User user = userRepository.findByIdCustom(userId)
                .orElseThrow(() -> new UserDoesntExistException("Usuario no encontrado"));

        Category newCategory = categoryMapper.toCategoryEntity(request, user);
        Category savedCategory = categoryRepository.save(newCategory);

        CategoryResponse response = categoryMapper.toCategoryResponse(savedCategory);
        return response;

    }

}
