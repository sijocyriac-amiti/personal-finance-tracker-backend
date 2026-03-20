package com.example.financetracker.service;

import com.example.financetracker.domain.Account;
import com.example.financetracker.domain.Category;
import com.example.financetracker.domain.FinanceTransaction;
import com.example.financetracker.dto.CreateTransactionRequest;
import com.example.financetracker.dto.TransactionResponse;
import com.example.financetracker.mapper.FinanceMapper;
import com.example.financetracker.repository.AccountRepository;
import com.example.financetracker.repository.CategoryRepository;
import com.example.financetracker.repository.TransactionRepository;
import java.time.YearMonth;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final CategoryRepository categoryRepository;
    private final UserService userService;

    public TransactionService(
        TransactionRepository transactionRepository,
        AccountRepository accountRepository,
        CategoryRepository categoryRepository,
        UserService userService
    ) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.categoryRepository = categoryRepository;
        this.userService = userService;
    }

    public List<TransactionResponse> listTransactions(String month) {
        YearMonth selectedMonth = month == null || month.isBlank() ? YearMonth.now() : YearMonth.parse(month);
        return transactionRepository.findByUserAndTransactionDateBetweenOrderByTransactionDateDesc(
                userService.getCurrentUser(),
                selectedMonth.atDay(1),
                selectedMonth.atEndOfMonth()
            ).stream()
            .map(FinanceMapper::toTransactionResponse)
            .toList();
    }

    public TransactionResponse getTransaction(Long id) {
        FinanceTransaction transaction = transactionRepository.findByIdAndUser(id, userService.getCurrentUser())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction not found"));
        return FinanceMapper.toTransactionResponse(transaction);
    }

    public TransactionResponse createTransaction(CreateTransactionRequest request) {
        Account account = accountRepository.findById(request.accountId())
            .filter(a -> a.getUser().getId().equals(userService.getCurrentUser().getId()))
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid account"));

        Category category = categoryRepository.findById(request.categoryId())
            .filter(c -> c.getUser().getId().equals(userService.getCurrentUser().getId()))
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid category"));

        FinanceTransaction transaction = FinanceTransaction.builder()
            .user(userService.getCurrentUser())
            .account(account)
            .category(category)
            .description(request.description())
            .amount(request.amount())
            .transactionDate(request.transactionDate())
            .type(request.type())
            .merchant(request.merchant())
            .note(request.note())
            .tags(request.tags())
            .build();

        return FinanceMapper.toTransactionResponse(transactionRepository.save(transaction));
    }

    public TransactionResponse updateTransaction(Long id, CreateTransactionRequest request) {
        FinanceTransaction transaction = transactionRepository.findByIdAndUser(id, userService.getCurrentUser())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction not found"));

        Account account = accountRepository.findById(request.accountId())
            .filter(a -> a.getUser().getId().equals(userService.getCurrentUser().getId()))
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid account"));

        Category category = categoryRepository.findById(request.categoryId())
            .filter(c -> c.getUser().getId().equals(userService.getCurrentUser().getId()))
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid category"));

        transaction.setAccount(account);
        transaction.setCategory(category);
        transaction.setDescription(request.description());
        transaction.setAmount(request.amount());
        transaction.setTransactionDate(request.transactionDate());
        transaction.setType(request.type());
        transaction.setMerchant(request.merchant());
        transaction.setNote(request.note());
        transaction.setTags(request.tags());

        return FinanceMapper.toTransactionResponse(transactionRepository.save(transaction));
    }

    public void deleteTransaction(Long id) {
        transactionRepository.findByIdAndUser(id, userService.getCurrentUser())
            .ifPresentOrElse(
                transactionRepository::delete,
                () -> { throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction not found"); }
            );
    }
}
