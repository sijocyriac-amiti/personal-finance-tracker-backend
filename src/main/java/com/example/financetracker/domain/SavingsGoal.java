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
@Table(name = "savings_goals")
public class SavingsGoal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal targetAmount;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal currentAmount;

    @Column
    private LocalDate targetDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SavingsGoalStatus status;

    @Column(length = 255)
    private String description;

    public static Builder builder() {
        return new Builder();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getTargetAmount() {
        return targetAmount;
    }

    public BigDecimal getCurrentAmount() {
        return currentAmount;
    }

    public LocalDate getTargetDate() {
        return targetDate;
    }

    public SavingsGoalStatus getStatus() {
        return status;
    }

    public String getDescription() {
        return description;
    }

    public void setCurrentAmount(BigDecimal currentAmount) {
        this.currentAmount = currentAmount;
    }

    public void setStatus(SavingsGoalStatus status) {
        this.status = status;
    }

    public static class Builder {
        private final SavingsGoal goal = new SavingsGoal();

        public Builder name(String name) {
            goal.name = name;
            return this;
        }

        public Builder targetAmount(BigDecimal targetAmount) {
            goal.targetAmount = targetAmount;
            return this;
        }

        public Builder currentAmount(BigDecimal currentAmount) {
            goal.currentAmount = currentAmount;
            return this;
        }

        public Builder targetDate(LocalDate targetDate) {
            goal.targetDate = targetDate;
            return this;
        }

        public Builder status(SavingsGoalStatus status) {
            goal.status = status;
            return this;
        }

        public Builder description(String description) {
            goal.description = description;
            return this;
        }

        public SavingsGoal build() {
            return goal;
        }
    }
}
