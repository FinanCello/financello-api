package com.example.financelloapi.mapper;

import com.example.financelloapi.dto.test.SpendingLimitResponse;
import com.example.financelloapi.model.entity.SpendingLimit;
import org.springframework.stereotype.Component;
import java.time.YearMonth;

@Component
public class SpendingLimitMapper {
    public SpendingLimitResponse toResponse(SpendingLimit limit, YearMonth period) {
        return new SpendingLimitResponse(
                limit.getCategory().getName(),
                limit.getMonthlyLimit(),
                period
        );
    }
}
