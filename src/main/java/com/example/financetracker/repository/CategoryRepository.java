package com.example.financetracker.repository;

import com.example.financetracker.domain.Category;
import com.example.financetracker.domain.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findByUser(User user);

}
