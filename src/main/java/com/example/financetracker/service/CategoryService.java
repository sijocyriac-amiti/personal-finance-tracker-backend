package com.example.financetracker.service;

import com.example.financetracker.domain.Category;
import com.example.financetracker.dto.CategoryResponse;
import com.example.financetracker.dto.CreateCategoryRequest;
import com.example.financetracker.mapper.FinanceMapper;
import com.example.financetracker.repository.CategoryRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserService userService;

    public CategoryService(CategoryRepository categoryRepository, UserService userService) {
        this.categoryRepository = categoryRepository;
        this.userService = userService;
    }

    public List<CategoryResponse> listCategories() {
        return categoryRepository.findByUser(userService.getCurrentUser()).stream()
            .map(FinanceMapper::toCategoryResponse)
            .toList();
    }

    public CategoryResponse createCategory(CreateCategoryRequest request) {
        Category category = Category.builder()
            .user(userService.getCurrentUser())
            .name(request.name())
            .type(request.type())
            .color(request.color())
            .icon(request.icon())
            .build();

        return FinanceMapper.toCategoryResponse(categoryRepository.save(category));
    }

    public CategoryResponse updateCategory(Long id, CreateCategoryRequest request) {
        Category category = categoryRepository.findById(id)
            .filter(c -> c.getUser().getId().equals(userService.getCurrentUser().getId()))
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));

        category.setName(request.name());
        category.setType(request.type());
        category.setColor(request.color());
        category.setIcon(request.icon());

        return FinanceMapper.toCategoryResponse(categoryRepository.save(category));
    }

    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
            .filter(c -> c.getUser().getId().equals(userService.getCurrentUser().getId()))
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));
        categoryRepository.delete(category);
    }
}
