package com.example.financelloapi.service.unit;

import com.example.financelloapi.mapper.GoalContributionMapper;
import com.example.financelloapi.repository.CategoryRepository;
import com.example.financelloapi.repository.GoalContributionRepository;
import com.example.financelloapi.repository.SavingGoalRepository;
import com.example.financelloapi.repository.SpendingLimitRepository;
import com.example.financelloapi.service.AuthService;
import com.example.financelloapi.service.GoalContributionService;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class GoalContributionServiceUnitTest {

    @Mock private GoalContributionRepository goalContributionRepository;
    @Mock private GoalContributionMapper goalContributionMapper;
    @Mock private SavingGoalRepository savingGoalRepository;
    @InjectMocks private GoalContributionService goalContributionService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }


}
