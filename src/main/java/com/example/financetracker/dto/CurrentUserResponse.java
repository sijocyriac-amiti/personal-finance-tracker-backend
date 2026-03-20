package com.example.financetracker.dto;

public record CurrentUserResponse(
    String email,
    String displayName
) {
}
