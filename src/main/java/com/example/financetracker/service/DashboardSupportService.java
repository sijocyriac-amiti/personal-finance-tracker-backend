package com.example.financetracker.service;

import com.example.financetracker.domain.BudgetCategory;
import com.example.financetracker.domain.MonthlyBudget;
import com.example.financetracker.repository.TransactionRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.springframework.stereotype.Service;

@Service
public class DashboardSupportService {

    private final TransactionRepository transactionRepository;
    private final UserService userService;

    public DashboardSupportService(TransactionRepository transactionRepository, UserService userService) {
        this.transactionRepository = transactionRepository;
        this.userService = userService;
    }

    public BigDecimal calculateSpentForBudget(MonthlyBudget budget) {
        String mappedCategory = mapBudgetCategory(budget.getCategory());
        LocalDate start = budget.getMonthStart();
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());
        return transactionRepository.sumExpensesByCategoryAndDateRange(userService.getCurrentUser(), mappedCategory, start, end);
    }

    private String mapBudgetCategory(BudgetCategory category) {
        return switch (category) {
            case HOUSING -> "RENT";
            case FOOD -> "FOOD";
            case TRANSPORT -> "TRANSPORT";
            case UTILITIES -> "UTILITIES";
            case ENTERTAINMENT -> "ENTERTAINMENT";
            case HEALTH -> "HEALTH";
            case EDUCATION -> "EDUCATION";
            case SHOPPING -> "SHOPPING";
            case SAVINGS -> "SAVINGS";
            case OTHER -> "OTHER";
        };
    }
}
