package com.example.financetracker.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "monthly_budgets")
public class MonthlyBudget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private BudgetCategory category;

    @Column(nullable = false)
    private LocalDate monthStart;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amountLimit;

    public static Builder builder() {
        return new Builder();
    }

    public Long getId() {
        return id;
    }

    public BudgetCategory getCategory() {
        return category;
    }

    public LocalDate getMonthStart() {
        return monthStart;
    }

    public BigDecimal getAmountLimit() {
        return amountLimit;
    }

    public static class Builder {
        private final MonthlyBudget budget = new MonthlyBudget();

        public Builder category(BudgetCategory category) {
            budget.category = category;
            return this;
        }

        public Builder monthStart(LocalDate monthStart) {
            budget.monthStart = monthStart;
            return this;
        }

        public Builder amountLimit(BigDecimal amountLimit) {
            budget.amountLimit = amountLimit;
            return this;
        }

        public MonthlyBudget build() {
            return budget;
        }
    }
}
