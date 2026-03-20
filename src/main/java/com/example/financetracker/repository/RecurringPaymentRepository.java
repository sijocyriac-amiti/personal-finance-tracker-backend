package com.example.financetracker.repository;

import com.example.financetracker.domain.RecurringPayment;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecurringPaymentRepository extends JpaRepository<RecurringPayment, Long> {

    List<RecurringPayment> findByActiveTrueOrderByNextPaymentDateAsc();

    List<RecurringPayment> findByActiveTrueAndNextPaymentDateLessThanEqualOrderByNextPaymentDateAsc(
        LocalDate date
    );
}
