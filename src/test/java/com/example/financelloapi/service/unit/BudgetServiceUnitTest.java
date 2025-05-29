package com.example.financelloapi.service.unit;

import com.example.financelloapi.repository.BudgetRepository;
import com.example.financelloapi.repository.UserRepository;
import com.example.financelloapi.service.BudgetService;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class BudgetServiceUnitTest {

    @Mock private BudgetRepository budgetRepository;
    @Mock private UserRepository userRepository;
    @InjectMocks private BudgetService budgetService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }


}
