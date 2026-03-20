package com.example.financetracker.dto;

import com.example.financetracker.domain.BudgetCategory;
import com.example.financetracker.domain.RecurringPaymentFrequency;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateRecurringPaymentRequest(
    @NotBlank String title,
    @NotNull @DecimalMin(value = "0.01") BigDecimal amount,
    @NotNull RecurringPaymentFrequency frequency,
    @NotNull BudgetCategory category,
    @NotNull LocalDate nextPaymentDate,
    boolean active
) {
}
