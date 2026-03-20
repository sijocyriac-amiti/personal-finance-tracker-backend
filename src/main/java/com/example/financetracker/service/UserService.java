package com.example.financetracker.service;

import com.example.financetracker.domain.User;
import com.example.financetracker.repository.UserRepository;
import com.example.financetracker.security.SecurityUtils;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getCurrentUser() {
        String currentPrincipal = SecurityUtils.getCurrentUsername();
        if (currentPrincipal == null) {
            throw new IllegalStateException("No authenticated user found");
        }
        return userRepository.findByEmail(currentPrincipal.toLowerCase())
            .orElseThrow(() -> new IllegalStateException("Authenticated user not found in database"));
    }
}
