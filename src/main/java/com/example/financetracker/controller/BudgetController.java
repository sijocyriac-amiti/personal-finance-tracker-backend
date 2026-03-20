package com.example.financetracker.controller;

import com.example.financetracker.dto.CreateBudgetRequest;
import com.example.financetracker.dto.MonthlyBudgetResponse;
import com.example.financetracker.service.BudgetService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/budgets")
public class BudgetController {

    private final BudgetService budgetService;

    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    @GetMapping
    public List<MonthlyBudgetResponse> listBudgets(@RequestParam(required = false) String month) {
        return budgetService.listBudgets(month);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MonthlyBudgetResponse createBudget(@Valid @RequestBody CreateBudgetRequest request) {
        return budgetService.createBudget(request);
    }
}
