package com.example.financetracker.service;

import com.example.financetracker.domain.RecurringPayment;
import com.example.financetracker.dto.CreateRecurringPaymentRequest;
import com.example.financetracker.dto.RecurringPaymentResponse;
import com.example.financetracker.mapper.FinanceMapper;
import com.example.financetracker.repository.RecurringPaymentRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class RecurringPaymentService {

    private final RecurringPaymentRepository recurringPaymentRepository;

    public RecurringPaymentService(RecurringPaymentRepository recurringPaymentRepository) {
        this.recurringPaymentRepository = recurringPaymentRepository;
    }

    public List<RecurringPaymentResponse> listRecurringPayments() {
        return recurringPaymentRepository.findAll().stream()
            .map(FinanceMapper::toRecurringPaymentResponse)
            .toList();
    }

    public RecurringPaymentResponse createRecurringPayment(CreateRecurringPaymentRequest request) {
        RecurringPayment recurringPayment = RecurringPayment.builder()
            .title(request.title())
            .amount(request.amount())
            .frequency(request.frequency())
            .category(request.category())
            .nextPaymentDate(request.nextPaymentDate())
            .active(request.active())
            .build();

        return FinanceMapper.toRecurringPaymentResponse(recurringPaymentRepository.save(recurringPayment));
    }
}
