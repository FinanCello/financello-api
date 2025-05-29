package com.example.financelloapi.service.unit;

import com.example.financelloapi.mapper.CategoryMapper;
import com.example.financelloapi.repository.CategoryRepository;
import com.example.financelloapi.repository.FinancialMovementRepository;
import com.example.financelloapi.repository.UserRepository;
import com.example.financelloapi.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class CategoryServiceUnitTest {

    @Mock private CategoryRepository categoryRepository;
    @Mock private CategoryMapper categoryMapper;
    @Mock private UserRepository userRepository;
    @Mock private FinancialMovementRepository financialMovementRepository;
    @InjectMocks private CategoryService categoryService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }


}
