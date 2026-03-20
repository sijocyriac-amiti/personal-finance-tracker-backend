package com.example.financetracker.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record RecurringPaymentResponse(
    Long id,
    String title,
    BigDecimal amount,
    String frequency,
    String category,
    LocalDate nextPaymentDate,
    boolean active
) {
}
