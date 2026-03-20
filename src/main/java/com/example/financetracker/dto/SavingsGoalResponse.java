package com.example.financetracker.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record SavingsGoalResponse(
    Long id,
    String name,
    BigDecimal targetAmount,
    BigDecimal currentAmount,
    BigDecimal completionPercentage,
    LocalDate targetDate,
    String status,
    String description
) {
}
