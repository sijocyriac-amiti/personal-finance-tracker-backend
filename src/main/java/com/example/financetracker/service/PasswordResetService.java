package com.example.financetracker.service;

import com.example.financetracker.domain.PasswordResetToken;
import com.example.financetracker.domain.User;
import com.example.financetracker.repository.PasswordResetTokenRepository;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PasswordResetService {

    private final PasswordResetTokenRepository repository;
    private final long resetTokenDurationMillis;

    public PasswordResetService(
        PasswordResetTokenRepository repository,
        @Value("${app.security.password-reset-token-ms:3600000}") long resetTokenDurationMillis
    ) {
        this.repository = repository;
        this.resetTokenDurationMillis = resetTokenDurationMillis;
    }

    @Transactional
    public PasswordResetToken createTokenForUser(User user) {
        repository.deleteByUserId(user.getId());

        PasswordResetToken token = new PasswordResetToken();
        token.setUser(user);
        token.setToken(UUID.randomUUID().toString());
        token.setExpiresAt(Instant.now().plusMillis(resetTokenDurationMillis));
        token.setUsed(false);
        return repository.save(token);
    }

    public Optional<PasswordResetToken> findByToken(String token) {
        return repository.findByToken(token);
    }

    @Transactional
    public void markAsUsed(PasswordResetToken token) {
        token.setUsed(true);
        repository.save(token);
    }

    public boolean isTokenValid(PasswordResetToken token) {
        return token != null
            && !token.isUsed()
            && token.getExpiresAt() != null
            && token.getExpiresAt().isAfter(Instant.now());
    }
}
