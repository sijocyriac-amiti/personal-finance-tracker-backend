package com.example.financetracker.repository;

import com.example.financetracker.domain.SavingsGoal;
import com.example.financetracker.domain.SavingsGoalStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SavingsGoalRepository extends JpaRepository<SavingsGoal, Long> {

    long countByStatus(SavingsGoalStatus status);
}
