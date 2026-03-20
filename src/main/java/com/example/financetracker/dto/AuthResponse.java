package com.example.financetracker.dto;

public record AuthResponse(
    String accessToken,
    String refreshToken,
    String tokenType,
    long expiresInSeconds,
    String email,
    String displayName
) {
}
