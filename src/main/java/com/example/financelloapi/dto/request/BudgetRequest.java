package com.example.financelloapi.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BudgetRequest {
        private Integer userId;
        private String period;
        private Float totalIncomePlanned;
        private Float totalOutcomePlanned;
}
