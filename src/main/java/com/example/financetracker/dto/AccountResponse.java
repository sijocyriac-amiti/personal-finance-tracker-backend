package com.example.financetracker.dto;

import com.example.financetracker.domain.AccountType;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AccountResponse(
    Long id,
    String name,
    AccountType type,
    BigDecimal openingBalance,
    BigDecimal currentBalance,
    String institutionName,
    LocalDateTime createdAt
) {
}
