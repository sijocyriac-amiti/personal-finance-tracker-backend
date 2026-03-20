package com.example.financetracker.service;

import com.example.financetracker.domain.RefreshToken;
import com.example.financetracker.domain.User;
import com.example.financetracker.repository.RefreshTokenRepository;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final long refreshTokenDurationMillis;

    public RefreshTokenService(
        RefreshTokenRepository refreshTokenRepository,
        @Value("${app.security.jwt.refresh-token-ms:604800000}") long refreshTokenDurationMillis
    ) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.refreshTokenDurationMillis = refreshTokenDurationMillis;
    }

    @Transactional
    public RefreshToken createRefreshToken(User user) {
        // Remove old tokens for the user.
        refreshTokenRepository.deleteByUser(user);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiresAt(Instant.now().plusMillis(refreshTokenDurationMillis));
        return refreshTokenRepository.save(refreshToken);
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    @Transactional
    public void revokeToken(RefreshToken token) {
        token.setRevoked(true);
        refreshTokenRepository.save(token);
    }

    @Transactional
    public void revokeAllTokensForUser(User user) {
        List<RefreshToken> tokens = refreshTokenRepository.findAllByUser(user);
        tokens.forEach(token -> token.setRevoked(true));
        refreshTokenRepository.saveAll(tokens);
    }

    public boolean isTokenValid(RefreshToken token) {
        return token != null
            && !token.isRevoked()
            && token.getExpiresAt() != null
            && token.getExpiresAt().isAfter(Instant.now());
    }
}
