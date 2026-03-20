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
@Table(name = "recurring_payments")
public class RecurringPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String title;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RecurringPaymentFrequency frequency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private BudgetCategory category;

    @Column(nullable = false)
    private LocalDate nextPaymentDate;

    @Column(nullable = false)
    private boolean active;

    public static Builder builder() {
        return new Builder();
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public RecurringPaymentFrequency getFrequency() {
        return frequency;
    }

    public BudgetCategory getCategory() {
        return category;
    }

    public LocalDate getNextPaymentDate() {
        return nextPaymentDate;
    }

    public boolean isActive() {
        return active;
    }

    public static class Builder {
        private final RecurringPayment payment = new RecurringPayment();

        public Builder title(String title) {
            payment.title = title;
            return this;
        }

        public Builder amount(BigDecimal amount) {
            payment.amount = amount;
            return this;
        }

        public Builder frequency(RecurringPaymentFrequency frequency) {
            payment.frequency = frequency;
            return this;
        }

        public Builder category(BudgetCategory category) {
            payment.category = category;
            return this;
        }

        public Builder nextPaymentDate(LocalDate nextPaymentDate) {
            payment.nextPaymentDate = nextPaymentDate;
            return this;
        }

        public Builder active(boolean active) {
            payment.active = active;
            return this;
        }

        public RecurringPayment build() {
            return payment;
        }
    }
}
