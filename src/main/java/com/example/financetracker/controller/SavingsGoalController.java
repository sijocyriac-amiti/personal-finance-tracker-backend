package com.example.financetracker.controller;

import com.example.financetracker.dto.ContributeToSavingsGoalRequest;
import com.example.financetracker.dto.CreateSavingsGoalRequest;
import com.example.financetracker.dto.SavingsGoalResponse;
import com.example.financetracker.service.SavingsGoalService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/savings-goals")
public class SavingsGoalController {

    private final SavingsGoalService savingsGoalService;

    public SavingsGoalController(SavingsGoalService savingsGoalService) {
        this.savingsGoalService = savingsGoalService;
    }

    @GetMapping
    public List<SavingsGoalResponse> listGoals() {
        return savingsGoalService.listGoals();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SavingsGoalResponse createGoal(@Valid @RequestBody CreateSavingsGoalRequest request) {
        return savingsGoalService.createGoal(request);
    }

    @PatchMapping("/{goalId}/contributions")
    public SavingsGoalResponse contribute(
        @PathVariable Long goalId,
        @Valid @RequestBody ContributeToSavingsGoalRequest request
    ) {
        return savingsGoalService.contribute(goalId, request);
    }
}
