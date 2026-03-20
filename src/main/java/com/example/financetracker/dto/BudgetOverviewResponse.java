package com.example.financetracker.dto;

import java.math.BigDecimal;

public record BudgetOverviewResponse(
    String category,
    BigDecimal limitAmount,
    BigDecimal spentAmount,
    BigDecimal remainingAmount
) {
}
