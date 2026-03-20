package com.example.financetracker.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateSavingsGoalRequest(
    @NotBlank String name,
    @NotNull @DecimalMin(value = "0.01") BigDecimal targetAmount,
    @DecimalMin(value = "0.00") BigDecimal currentAmount,
    LocalDate targetDate,
    String description
) {
}
