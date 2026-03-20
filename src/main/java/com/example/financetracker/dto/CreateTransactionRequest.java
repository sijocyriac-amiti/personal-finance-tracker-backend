package com.example.financetracker.dto;

import com.example.financetracker.domain.TransactionType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateTransactionRequest(
    @NotBlank String description,
    @NotNull @DecimalMin(value = "0.01") BigDecimal amount,
    @NotNull LocalDate transactionDate,
    @NotNull TransactionType type,
    @NotNull Long accountId,
    @NotNull Long categoryId,
    String merchant,
    String note,
    String tags
) {
}
