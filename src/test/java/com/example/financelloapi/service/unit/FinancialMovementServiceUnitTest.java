package com.example.financelloapi.service.unit;

import com.example.financelloapi.mapper.FinancialMovementMapper;
import com.example.financelloapi.repository.CategoryRepository;
import com.example.financelloapi.repository.FinancialMovementRepository;
import com.example.financelloapi.repository.UserRepository;
import com.example.financelloapi.service.FinancialMovementService;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class FinancialMovementServiceUnitTest {

    @Mock private CategoryRepository categoryRepository;
    @Mock private FinancialMovementRepository financialMovementRepository;
    @Mock private FinancialMovementMapper financialMovementMapper;
    @Mock private UserRepository userRepository;
    @InjectMocks private FinancialMovementService financialMovementService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }


}





