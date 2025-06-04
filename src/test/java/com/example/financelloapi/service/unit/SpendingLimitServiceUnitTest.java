package com.example.financelloapi.service.unit;

import com.example.financelloapi.dto.request.SpendingLimitRequest;
import com.example.financelloapi.dto.test.SpendingLimitResponse;
import com.example.financelloapi.mapper.SpendingLimitMapper;
import com.example.financelloapi.model.entity.SpendingLimit;
import com.example.financelloapi.repository.CategoryRepository;
import com.example.financelloapi.repository.FinancialMovementRepository;
import com.example.financelloapi.repository.SpendingLimitRepository;
import com.example.financelloapi.repository.UserRepository;
import com.example.financelloapi.service.AuthService;
import com.example.financelloapi.service.SpendingLimitService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

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
