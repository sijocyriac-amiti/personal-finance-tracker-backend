package com.example.financetracker.controller;

import com.example.financetracker.dto.CreateRecurringPaymentRequest;
import com.example.financetracker.dto.RecurringPaymentResponse;
import com.example.financetracker.service.RecurringPaymentService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/recurring-payments")
public class RecurringPaymentController {

    private final RecurringPaymentService recurringPaymentService;

    public RecurringPaymentController(RecurringPaymentService recurringPaymentService) {
        this.recurringPaymentService = recurringPaymentService;
    }

    @GetMapping
    public List<RecurringPaymentResponse> listRecurringPayments() {
        return recurringPaymentService.listRecurringPayments();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RecurringPaymentResponse createRecurringPayment(
        @Valid @RequestBody CreateRecurringPaymentRequest request
    ) {
        return recurringPaymentService.createRecurringPayment(request);
    }
}
