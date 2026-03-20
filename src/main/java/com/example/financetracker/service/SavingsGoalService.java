package com.example.financetracker.service;

import com.example.financetracker.domain.SavingsGoal;
import com.example.financetracker.domain.SavingsGoalStatus;
import com.example.financetracker.dto.ContributeToSavingsGoalRequest;
import com.example.financetracker.dto.CreateSavingsGoalRequest;
import com.example.financetracker.dto.SavingsGoalResponse;
import com.example.financetracker.mapper.FinanceMapper;
import com.example.financetracker.repository.SavingsGoalRepository;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class SavingsGoalService {

    private final SavingsGoalRepository savingsGoalRepository;

    public SavingsGoalService(SavingsGoalRepository savingsGoalRepository) {
        this.savingsGoalRepository = savingsGoalRepository;
    }

    public List<SavingsGoalResponse> listGoals() {
        return savingsGoalRepository.findAll().stream()
            .map(FinanceMapper::toSavingsGoalResponse)
            .toList();
    }

    public SavingsGoalResponse createGoal(CreateSavingsGoalRequest request) {
        BigDecimal currentAmount = request.currentAmount() == null ? BigDecimal.ZERO : request.currentAmount();
        SavingsGoalStatus status = currentAmount.compareTo(request.targetAmount()) >= 0
            ? SavingsGoalStatus.ACHIEVED
            : SavingsGoalStatus.ACTIVE;

        SavingsGoal goal = SavingsGoal.builder()
            .name(request.name())
            .targetAmount(request.targetAmount())
            .currentAmount(currentAmount)
            .targetDate(request.targetDate())
            .description(request.description())
            .status(status)
            .build();

        return FinanceMapper.toSavingsGoalResponse(savingsGoalRepository.save(goal));
    }

    public SavingsGoalResponse contribute(Long goalId, ContributeToSavingsGoalRequest request) {
        SavingsGoal goal = savingsGoalRepository.findById(goalId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Savings goal not found"));

        BigDecimal updatedAmount = goal.getCurrentAmount().add(request.amount());
        goal.setCurrentAmount(updatedAmount);
        if (updatedAmount.compareTo(goal.getTargetAmount()) >= 0) {
            goal.setStatus(SavingsGoalStatus.ACHIEVED);
        }

        return FinanceMapper.toSavingsGoalResponse(savingsGoalRepository.save(goal));
    }
}
