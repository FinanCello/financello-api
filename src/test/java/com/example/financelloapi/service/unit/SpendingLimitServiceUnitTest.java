package com.example.financelloapi.service.unit;

import com.example.financelloapi.mapper.SpendingLimitMapper;
import com.example.financelloapi.repository.CategoryRepository;
import com.example.financelloapi.repository.FinancialMovementRepository;
import com.example.financelloapi.repository.SpendingLimitRepository;
import com.example.financelloapi.repository.UserRepository;
import com.example.financelloapi.service.AuthService;
import com.example.financelloapi.service.SpendingLimitService;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class SpendingLimitServiceUnitTest {

    @Mock private SpendingLimitRepository spendingLimitRepository;
    @Mock private CategoryRepository categoryRepository;
    @Mock private AuthService authService;
    @Mock private SpendingLimitMapper spendingLimitMapper;
    @Mock private UserRepository userRepository;
    @Mock private FinancialMovementRepository financialMovementRepository;
    @InjectMocks private SpendingLimitService spendingLimitService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }


}
