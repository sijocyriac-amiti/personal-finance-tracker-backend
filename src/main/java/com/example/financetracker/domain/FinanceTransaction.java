package com.example.financetracker.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "transactions")
public class FinanceTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private com.example.financetracker.domain.User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(nullable = false, length = 120)
    private String description;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false)
    private LocalDate transactionDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TransactionType type;

    @Column(length = 255)
    private String merchant;

    @Column(length = 255)
    private String note;

    @Column(length = 255)
    private String tags;

    public static Builder builder() {
        return new Builder();
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public Account getAccount() {
        return account;
    }

    public Category getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public LocalDate getTransactionDate() {
        return transactionDate;
    }

    public TransactionType getType() {
        return type;
    }

    public String getMerchant() {
        return merchant;
    }

    public String getNote() {
        return note;
    }

    public String getTags() {
        return tags;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setTransactionDate(LocalDate transactionDate) {
        this.transactionDate = transactionDate;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public void setMerchant(String merchant) {
        this.merchant = merchant;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public static class Builder {
        private final FinanceTransaction transaction = new FinanceTransaction();

        public Builder user(com.example.financetracker.domain.User user) {
            transaction.user = user;
            return this;
        }

        public Builder account(Account account) {
            transaction.account = account;
            return this;
        }

        public Builder category(Category category) {
            transaction.category = category;
            return this;
        }

        public Builder description(String description) {
            transaction.description = description;
            return this;
        }

        public Builder amount(BigDecimal amount) {
            transaction.amount = amount;
            return this;
        }

        public Builder transactionDate(LocalDate transactionDate) {
            transaction.transactionDate = transactionDate;
            return this;
        }

        public Builder type(TransactionType type) {
            transaction.type = type;
            return this;
        }

        public Builder merchant(String merchant) {
            transaction.merchant = merchant;
            return this;
        }

        public Builder note(String note) {
            transaction.note = note;
            return this;
        }

        public Builder tags(String tags) {
            transaction.tags = tags;
            return this;
        }

        public FinanceTransaction build() {
            return transaction;
        }
    }
}
