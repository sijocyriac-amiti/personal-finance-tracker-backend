package com.example.financetracker.dto;

import java.math.BigDecimal;
import java.util.List;

public record DashboardSummaryResponse(
    String month,
    BigDecimal totalIncome,
    BigDecimal totalExpenses,
    BigDecimal netBalance,
    long transactionCount,
    long activeSavingsGoals,
    long activeRecurringPayments,
    List<BudgetOverviewResponse> budgets,
    List<SavingsGoalResponse> savingsGoals,
    List<RecurringPaymentResponse> upcomingRecurringPayments
) {
}
