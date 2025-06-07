package com.example.financelloapi.service.unit;

import com.example.financelloapi.mapper.SavingGoalMapper;
import com.example.financelloapi.model.entity.SavingGoal;
import com.example.financelloapi.repository.GoalContributionRepository;
import com.example.financelloapi.repository.SavingGoalRepository;
import com.example.financelloapi.service.SavingGoalService;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class SavingGoalServiceUnitTest {

    @Mock private SavingGoalRepository savingGoalRepository;
    @Mock private GoalContributionRepository goalContributionRepository;
    @Mock private SavingGoalMapper savingGoalMapper;
    @InjectMocks private SavingGoalService savingGoalService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }


}