package com.example.financetracker.repository;

import com.example.financetracker.domain.MonthlyBudget;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MonthlyBudgetRepository extends JpaRepository<MonthlyBudget, Long> {

    List<MonthlyBudget> findByMonthStart(LocalDate monthStart);
}
