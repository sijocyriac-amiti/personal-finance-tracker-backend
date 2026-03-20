package com.example.financetracker.service;

import com.example.financetracker.domain.SavingsGoalStatus;
import com.example.financetracker.domain.TransactionType;
import com.example.financetracker.dto.BudgetOverviewResponse;
import com.example.financetracker.dto.DashboardSummaryResponse;
import com.example.financetracker.dto.RecurringPaymentResponse;
import com.example.financetracker.dto.SavingsGoalResponse;
import com.example.financetracker.mapper.FinanceMapper;
import com.example.financetracker.repository.MonthlyBudgetRepository;
import com.example.financetracker.repository.RecurringPaymentRepository;
import com.example.financetracker.repository.SavingsGoalRepository;
import com.example.financetracker.repository.TransactionRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {

    private final TransactionRepository transactionRepository;
    private final MonthlyBudgetRepository monthlyBudgetRepository;
    private final SavingsGoalRepository savingsGoalRepository;
    private final RecurringPaymentRepository recurringPaymentRepository;
    private final DashboardSupportService dashboardSupportService;
    private final UserService userService;

    public DashboardService(
        TransactionRepository transactionRepository,
        MonthlyBudgetRepository monthlyBudgetRepository,
        SavingsGoalRepository savingsGoalRepository,
        RecurringPaymentRepository recurringPaymentRepository,
        DashboardSupportService dashboardSupportService,
        UserService userService
    ) {
        this.transactionRepository = transactionRepository;
        this.monthlyBudgetRepository = monthlyBudgetRepository;
        this.savingsGoalRepository = savingsGoalRepository;
        this.recurringPaymentRepository = recurringPaymentRepository;
        this.dashboardSupportService = dashboardSupportService;
        this.userService = userService;
    }

    public DashboardSummaryResponse getSummary(String month) {
        YearMonth selectedMonth = month == null || month.isBlank() ? YearMonth.now() : YearMonth.parse(month);
        LocalDate start = selectedMonth.atDay(1);
        LocalDate end = selectedMonth.atEndOfMonth();

        BigDecimal income = transactionRepository.sumAmountByTypeAndDateRange(userService.getCurrentUser(), TransactionType.INCOME, start, end);
        BigDecimal expenses = transactionRepository.sumAmountByTypeAndDateRange(userService.getCurrentUser(), TransactionType.EXPENSE, start, end);

        List<BudgetOverviewResponse> budgets = monthlyBudgetRepository.findByMonthStart(start).stream()
            .map(budget -> FinanceMapper.toBudgetOverview(
                budget,
                dashboardSupportService.calculateSpentForBudget(budget)
            ))
            .toList();

        List<SavingsGoalResponse> goals = savingsGoalRepository.findAll().stream()
            .map(FinanceMapper::toSavingsGoalResponse)
            .toList();

        List<RecurringPaymentResponse> upcomingPayments =
            recurringPaymentRepository.findByActiveTrueAndNextPaymentDateLessThanEqualOrderByNextPaymentDateAsc(
                end
            ).stream()
                .map(FinanceMapper::toRecurringPaymentResponse)
                .toList();

        return new DashboardSummaryResponse(
            selectedMonth.toString(),
            income,
            expenses,
            income.subtract(expenses),
            transactionRepository.findByUserAndTransactionDateBetweenOrderByTransactionDateDesc(userService.getCurrentUser(), start, end).size(),
            savingsGoalRepository.countByStatus(SavingsGoalStatus.ACTIVE),
            recurringPaymentRepository.findByActiveTrueOrderByNextPaymentDateAsc().size(),
            budgets,
            goals,
            upcomingPayments
        );
    }
}
