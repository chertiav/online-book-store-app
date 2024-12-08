package com.achdev.onlinebookstoreapp.repository.category;

import com.achdev.onlinebookstoreapp.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
