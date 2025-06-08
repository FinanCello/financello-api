package com.example.financelloapi.service.unit;

import com.example.financelloapi.exception.CustomException;
import com.example.financelloapi.repository.BudgetRepository;
import com.example.financelloapi.repository.FinancialMovementRepository;
import com.example.financelloapi.repository.UserRepository;
import com.example.financelloapi.service.BudgetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

public class BudgetServiceUnitTest {

    @Mock private BudgetRepository budgetRepository;
    @Mock private UserRepository userRepository;
    @InjectMocks private BudgetService budgetService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }





}
