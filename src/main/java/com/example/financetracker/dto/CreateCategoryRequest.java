package com.example.financetracker.dto;

import com.example.financetracker.domain.CategoryType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateCategoryRequest(
    @NotBlank String name,
    @NotNull CategoryType type,
    String color,
    String icon
) {
}
