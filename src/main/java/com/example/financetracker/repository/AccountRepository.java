package com.example.financetracker.repository;

import com.example.financetracker.domain.Account;
import com.example.financetracker.domain.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {

    List<Account> findByUser(User user);

}
