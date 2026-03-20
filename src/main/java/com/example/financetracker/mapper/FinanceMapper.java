package com.example.financetracker.mapper;

import com.example.financetracker.domain.FinanceTransaction;
import com.example.financetracker.domain.MonthlyBudget;
import com.example.financetracker.domain.RecurringPayment;
import com.example.financetracker.domain.SavingsGoal;
import com.example.financetracker.dto.AccountResponse;
import com.example.financetracker.dto.BudgetOverviewResponse;
import com.example.financetracker.dto.CategoryResponse;
import com.example.financetracker.dto.MonthlyBudgetResponse;
import com.example.financetracker.dto.RecurringPaymentResponse;
import com.example.financetracker.dto.SavingsGoalResponse;
import com.example.financetracker.dto.TransactionResponse;
import java.math.BigDecimal;
import java.math.RoundingMode;

public final class FinanceMapper {

    private FinanceMapper() {
    }

    public static TransactionResponse toTransactionResponse(FinanceTransaction transaction) {
        return new TransactionResponse(
            transaction.getId(),
            transaction.getDescription(),
            transaction.getAmount(),
            transaction.getTransactionDate(),
            transaction.getType().name(),
            transaction.getAccount() != null ? transaction.getAccount().getId() : null,
            transaction.getCategory() != null ? transaction.getCategory().getId() : null,
            transaction.getMerchant(),
            transaction.getNote(),
            transaction.getTags()
        );
    }

    public static MonthlyBudgetResponse toBudgetResponse(MonthlyBudget budget, BigDecimal spentAmount) {
        return new MonthlyBudgetResponse(
            budget.getId(),
            budget.getCategory().name(),
            budget.getMonthStart(),
            budget.getAmountLimit(),
            spentAmount,
            budget.getAmountLimit().subtract(spentAmount)
        );
    }

    public static BudgetOverviewResponse toBudgetOverview(MonthlyBudget budget, BigDecimal spentAmount) {
        return new BudgetOverviewResponse(
            budget.getCategory().name(),
            budget.getAmountLimit(),
            spentAmount,
            budget.getAmountLimit().subtract(spentAmount)
        );
    }

    public static SavingsGoalResponse toSavingsGoalResponse(SavingsGoal goal) {
        BigDecimal completionPercentage = BigDecimal.ZERO;
        if (goal.getTargetAmount().compareTo(BigDecimal.ZERO) > 0) {
            completionPercentage = goal.getCurrentAmount()
                .multiply(BigDecimal.valueOf(100))
                .divide(goal.getTargetAmount(), 2, RoundingMode.HALF_UP);
        }

        return new SavingsGoalResponse(
            goal.getId(),
            goal.getName(),
            goal.getTargetAmount(),
            goal.getCurrentAmount(),
            completionPercentage,
            goal.getTargetDate(),
            goal.getStatus().name(),
            goal.getDescription()
        );
    }

    public static RecurringPaymentResponse toRecurringPaymentResponse(RecurringPayment payment) {
        return new RecurringPaymentResponse(
            payment.getId(),
            payment.getTitle(),
            payment.getAmount(),
            payment.getFrequency().name(),
            payment.getCategory().name(),
            payment.getNextPaymentDate(),
            payment.isActive()
        );
    }

    public static AccountResponse toAccountResponse(com.example.financetracker.domain.Account account) {
        return new AccountResponse(
            account.getId(),
            account.getName(),
            com.example.financetracker.domain.AccountType.valueOf(account.getType()),
            account.getOpeningBalance(),
            account.getCurrentBalance(),
            account.getInstitutionName(),
            account.getCreatedAt()
        );
    }

    public static CategoryResponse toCategoryResponse(com.example.financetracker.domain.Category category) {
        return new CategoryResponse(
            category.getId(),
            category.getName(),
            category.getType(),
            category.getColor(),
            category.getIcon(),
            category.isArchived()
        );
    }
}
