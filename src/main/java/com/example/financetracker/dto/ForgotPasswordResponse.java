package com.example.financetracker.dto;

public record ForgotPasswordResponse(
    String message,
    String resetToken
) {
}
