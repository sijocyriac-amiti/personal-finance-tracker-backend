package com.example.financetracker.repository;

import com.example.financetracker.domain.TransactionType;
import com.example.financetracker.domain.FinanceTransaction;
import com.example.financetracker.domain.User;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TransactionRepository extends JpaRepository<FinanceTransaction, Long> {

    List<FinanceTransaction> findByUserAndTransactionDateBetweenOrderByTransactionDateDesc(
        User user,
        LocalDate start,
        LocalDate end
    );

    java.util.Optional<FinanceTransaction> findByIdAndUser(Long id, User user);

    @Query("""
        select coalesce(sum(t.amount), 0)
        from FinanceTransaction t
        where t.user = :user
          and t.type = :type
          and t.transactionDate between :start and :end
        """)
    BigDecimal sumAmountByTypeAndDateRange(
        @Param("user") User user,
        @Param("type") TransactionType type,
        @Param("start") LocalDate start,
        @Param("end") LocalDate end
    );

    @Query("""
        select coalesce(sum(t.amount), 0)
        from FinanceTransaction t
        where t.user = :user
          and t.type = com.example.financetracker.domain.TransactionType.EXPENSE
          and upper(t.category.name) = upper(:categoryName)
          and t.transactionDate between :start and :end
        """)
    BigDecimal sumExpensesByCategoryAndDateRange(
        @Param("user") User user,
        @Param("categoryName") String categoryName,
        @Param("start") LocalDate start,
        @Param("end") LocalDate end
    );
}
