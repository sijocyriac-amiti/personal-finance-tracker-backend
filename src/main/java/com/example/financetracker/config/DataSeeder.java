package com.example.financetracker.config;

import com.example.financetracker.domain.Account;
import com.example.financetracker.domain.AccountType;
import com.example.financetracker.domain.BudgetCategory;
import com.example.financetracker.domain.Category;
import com.example.financetracker.domain.CategoryType;
import com.example.financetracker.domain.FinanceTransaction;
import com.example.financetracker.domain.MonthlyBudget;
import com.example.financetracker.domain.RecurringPayment;
import com.example.financetracker.domain.RecurringPaymentFrequency;
import com.example.financetracker.domain.SavingsGoal;
import com.example.financetracker.domain.SavingsGoalStatus;
import com.example.financetracker.domain.TransactionType;
import com.example.financetracker.domain.User;
import com.example.financetracker.repository.AccountRepository;
import com.example.financetracker.repository.CategoryRepository;
import com.example.financetracker.repository.MonthlyBudgetRepository;
import com.example.financetracker.repository.RecurringPaymentRepository;
import com.example.financetracker.repository.SavingsGoalRepository;
import com.example.financetracker.repository.TransactionRepository;
import com.example.financetracker.repository.UserRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seedDemoData(
        TransactionRepository transactionRepository,
        MonthlyBudgetRepository monthlyBudgetRepository,
        SavingsGoalRepository savingsGoalRepository,
        RecurringPaymentRepository recurringPaymentRepository,
        AccountRepository accountRepository,
        CategoryRepository categoryRepository,
        UserRepository userRepository,
        PasswordEncoder passwordEncoder
    ) {
        return args -> {
            User demoUser = userRepository.findByEmail("demo@example.com").orElseGet(() ->
                userRepository.save(User.builder()
                    .username("demo@example.com")
                    .email("demo@example.com")
                    .displayName("Demo User")
                    .password(passwordEncoder.encode("DemoPass1"))
                    .roles("ROLE_USER")
                    .build()
                )
            );

            Account primaryAccount = accountRepository.findByUser(demoUser).stream()
                .findFirst()
                .orElseGet(() -> accountRepository.save(Account.builder()
                    .user(demoUser)
                    .name("Primary Checking")
                    .type(AccountType.CHECKING.name())
                    .openingBalance(new BigDecimal("4000.00"))
                    .institutionName("Local Bank")
                    .build()
                ));

            Category salaryCategory = findOrCreateCategory(
                categoryRepository,
                demoUser,
                "SALARY",
                CategoryType.INCOME,
                "#2563eb"
            );
            Category foodCategory = findOrCreateCategory(
                categoryRepository,
                demoUser,
                "FOOD",
                CategoryType.EXPENSE,
                "#f59e0b"
            );
            Category rentCategory = findOrCreateCategory(
                categoryRepository,
                demoUser,
                "RENT",
                CategoryType.EXPENSE,
                "#dc2626"
            );

            if (transactionRepository.count() > 0
                || monthlyBudgetRepository.count() > 0
                || savingsGoalRepository.count() > 0
                || recurringPaymentRepository.count() > 0) {
                return;
            }

            transactionRepository.save(FinanceTransaction.builder()
                .user(demoUser)
                .account(primaryAccount)
                .description("Monthly salary")
                .amount(new BigDecimal("6200.00"))
                .transactionDate(LocalDate.now().withDayOfMonth(1))
                .type(TransactionType.INCOME)
                .category(salaryCategory)
                .merchant("Employer Inc.")
                .note("Primary employer payroll")
                .tags("salary,monthly")
                .build());

            transactionRepository.save(FinanceTransaction.builder()
                .user(demoUser)
                .account(primaryAccount)
                .description("Groceries")
                .amount(new BigDecimal("180.50"))
                .transactionDate(LocalDate.now().minusDays(2))
                .type(TransactionType.EXPENSE)
                .category(foodCategory)
                .merchant("Fresh Market")
                .note("Weekly grocery run")
                .tags("grocery,weekly")
                .build());

            transactionRepository.save(FinanceTransaction.builder()
                .user(demoUser)
                .account(primaryAccount)
                .description("Apartment rent")
                .amount(new BigDecimal("1450.00"))
                .transactionDate(LocalDate.now().withDayOfMonth(3))
                .type(TransactionType.EXPENSE)
                .category(rentCategory)
                .merchant("Landlord")
                .note("Recurring rent payment")
                .tags("rent,housing")
                .build());

            monthlyBudgetRepository.save(MonthlyBudget.builder()
                .category(BudgetCategory.FOOD)
                .monthStart(LocalDate.now().withDayOfMonth(1))
                .amountLimit(new BigDecimal("600.00"))
                .build());

            monthlyBudgetRepository.save(MonthlyBudget.builder()
                .category(BudgetCategory.ENTERTAINMENT)
                .monthStart(LocalDate.now().withDayOfMonth(1))
                .amountLimit(new BigDecimal("250.00"))
                .build());

            savingsGoalRepository.save(SavingsGoal.builder()
                .name("Emergency Fund")
                .targetAmount(new BigDecimal("10000.00"))
                .currentAmount(new BigDecimal("3500.00"))
                .targetDate(LocalDate.now().plusMonths(8))
                .status(SavingsGoalStatus.ACTIVE)
                .description("Build six months of living expenses")
                .build());

            recurringPaymentRepository.save(RecurringPayment.builder()
                .title("Netflix")
                .amount(new BigDecimal("15.99"))
                .frequency(RecurringPaymentFrequency.MONTHLY)
                .category(BudgetCategory.ENTERTAINMENT)
                .nextPaymentDate(LocalDate.now().plusDays(5))
                .active(true)
                .build());
        };
    }

    private Category findOrCreateCategory(
        CategoryRepository categoryRepository,
        User user,
        String name,
        CategoryType type,
        String color
    ) {
        return categoryRepository.findByUser(user).stream()
            .filter(category -> category.getName().equalsIgnoreCase(name))
            .findFirst()
            .orElseGet(() -> categoryRepository.save(Category.builder()
                .user(user)
                .name(name)
                .type(type)
                .color(color)
                .icon(name.toLowerCase())
                .build()
            ));
    }
}
