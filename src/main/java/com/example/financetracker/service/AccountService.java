package com.example.financetracker.service;

import com.example.financetracker.domain.Account;
import com.example.financetracker.dto.AccountResponse;
import com.example.financetracker.dto.AccountTransferRequest;
import com.example.financetracker.dto.CreateAccountRequest;
import com.example.financetracker.mapper.FinanceMapper;
import com.example.financetracker.repository.AccountRepository;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserService userService;

    public AccountService(AccountRepository accountRepository, UserService userService) {
        this.accountRepository = accountRepository;
        this.userService = userService;
    }

    public List<AccountResponse> listAccounts() {
        return accountRepository.findByUser(userService.getCurrentUser()).stream()
            .map(FinanceMapper::toAccountResponse)
            .toList();
    }

    public AccountResponse createAccount(CreateAccountRequest request) {
        Account account = Account.builder()
            .user(userService.getCurrentUser())
            .name(request.name())
            .type(request.type().name())
            .openingBalance(request.openingBalance())
            .institutionName(request.institutionName())
            .build();

        return FinanceMapper.toAccountResponse(accountRepository.save(account));
    }

    public AccountResponse updateAccount(Long id, CreateAccountRequest request) {
        Account account = accountRepository.findById(id)
            .filter(a -> a.getUser().getId().equals(userService.getCurrentUser().getId()))
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));

        account.setName(request.name());
        account.setType(request.type().name());
        account.setInstitutionName(request.institutionName());

        return FinanceMapper.toAccountResponse(accountRepository.save(account));
    }

    @Transactional
    public void transfer(AccountTransferRequest request) {
        if (request.fromAccountId().equals(request.toAccountId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Source and destination must differ");
        }

        Account from = accountRepository.findById(request.fromAccountId())
            .filter(a -> a.getUser().getId().equals(userService.getCurrentUser().getId()))
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid source account"));

        Account to = accountRepository.findById(request.toAccountId())
            .filter(a -> a.getUser().getId().equals(userService.getCurrentUser().getId()))
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid destination account"));

        BigDecimal amount = request.amount();
        if (from.getCurrentBalance().compareTo(amount) < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Insufficient balance");
        }

        from.setCurrentBalance(from.getCurrentBalance().subtract(amount));
        to.setCurrentBalance(to.getCurrentBalance().add(amount));

        accountRepository.save(from);
        accountRepository.save(to);
    }

    public void deleteAccount(Long id) {
        Account account = accountRepository.findById(id)
            .filter(a -> a.getUser().getId().equals(userService.getCurrentUser().getId()))
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));
        accountRepository.delete(account);
    }
}
