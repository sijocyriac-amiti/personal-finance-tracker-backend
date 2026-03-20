package com.example.financetracker.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record MonthlyBudgetResponse(
    Long id,
    String category,
    LocalDate monthStart,
    BigDecimal amountLimit,
    BigDecimal spentAmount,
    BigDecimal remainingAmount
) {
}
