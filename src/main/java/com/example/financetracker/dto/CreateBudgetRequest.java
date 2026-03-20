package com.example.financetracker.dto;

import com.example.financetracker.domain.BudgetCategory;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateBudgetRequest(
    @NotNull BudgetCategory category,
    @NotNull LocalDate monthStart,
    @NotNull @DecimalMin(value = "0.01") BigDecimal amountLimit
) {
}
