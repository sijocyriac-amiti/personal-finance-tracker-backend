package com.example.financetracker.dto;

import com.example.financetracker.domain.CategoryType;

public record CategoryResponse(
    Long id,
    String name,
    CategoryType type,
    String color,
    String icon,
    boolean archived
) {
}
