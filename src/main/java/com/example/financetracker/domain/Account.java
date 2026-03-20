package com.example.financetracker.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 30)
    private String type;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal openingBalance = BigDecimal.ZERO;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal currentBalance = BigDecimal.ZERO;

    @Column(length = 120)
    private String institutionName;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public static Builder builder() {
        return new Builder();
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public BigDecimal getOpeningBalance() {
        return openingBalance;
    }

    public BigDecimal getCurrentBalance() {
        return currentBalance;
    }

    public String getInstitutionName() {
        return institutionName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setInstitutionName(String institutionName) {
        this.institutionName = institutionName;
    }

    public void setCurrentBalance(BigDecimal currentBalance) {
        this.currentBalance = currentBalance;
    }

    public static class Builder {
        private final Account account = new Account();

        public Builder user(User user) {
            account.user = user;
            return this;
        }

        public Builder name(String name) {
            account.name = name;
            return this;
        }

        public Builder type(String type) {
            account.type = type;
            return this;
        }

        public Builder openingBalance(BigDecimal openingBalance) {
            account.openingBalance = openingBalance;
            account.currentBalance = openingBalance;
            return this;
        }

        public Builder institutionName(String institutionName) {
            account.institutionName = institutionName;
            return this;
        }

        public Account build() {
            return account;
        }
    }
}
