package com.example.financetracker.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransactionResponse(
    Long id,
    String description,
    BigDecimal amount,
    LocalDate transactionDate,
    String type,
    Long accountId,
    Long categoryId,
    String merchant,
    String note,
    String tags
) {
}
