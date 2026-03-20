package com.example.financetracker.service;

import com.example.financetracker.domain.MonthlyBudget;
import com.example.financetracker.dto.CreateBudgetRequest;
import com.example.financetracker.dto.MonthlyBudgetResponse;
import com.example.financetracker.mapper.FinanceMapper;
import com.example.financetracker.repository.MonthlyBudgetRepository;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class BudgetService {

    private final MonthlyBudgetRepository monthlyBudgetRepository;
    private final DashboardSupportService dashboardSupportService;

    public BudgetService(
        MonthlyBudgetRepository monthlyBudgetRepository,
        DashboardSupportService dashboardSupportService
    ) {
        this.monthlyBudgetRepository = monthlyBudgetRepository;
        this.dashboardSupportService = dashboardSupportService;
    }

    public List<MonthlyBudgetResponse> listBudgets(String month) {
        LocalDate monthStart = parseMonth(month);
        return monthlyBudgetRepository.findByMonthStart(monthStart).stream()
            .map(budget -> FinanceMapper.toBudgetResponse(
                budget,
                dashboardSupportService.calculateSpentForBudget(budget)
            ))
            .toList();
    }

    public MonthlyBudgetResponse createBudget(CreateBudgetRequest request) {
        MonthlyBudget budget = MonthlyBudget.builder()
            .category(request.category())
            .monthStart(request.monthStart().withDayOfMonth(1))
            .amountLimit(request.amountLimit())
            .build();

        MonthlyBudget saved = monthlyBudgetRepository.save(budget);
        return FinanceMapper.toBudgetResponse(saved, dashboardSupportService.calculateSpentForBudget(saved));
    }

    private LocalDate parseMonth(String month) {
        return month == null || month.isBlank()
            ? LocalDate.now().withDayOfMonth(1)
            : YearMonth.parse(month).atDay(1);
    }
}
