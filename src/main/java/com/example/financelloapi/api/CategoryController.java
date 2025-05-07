package com.example.financelloapi.api;

import com.example.financelloapi.dto.request.CategoryRequest;
import com.example.financelloapi.model.entity.Category;
import com.example.financelloapi.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/categories")
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<Category> addCategory(@RequestBody CategoryRequest dto) {
        return new ResponseEntity<>(categoryService.createCategory(dto), HttpStatus.CREATED);

    }
}
