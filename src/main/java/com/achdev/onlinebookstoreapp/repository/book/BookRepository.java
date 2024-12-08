package com.achdev.onlinebookstoreapp.repository.book;

import com.achdev.onlinebookstoreapp.model.Book;
import com.achdev.onlinebookstoreapp.model.Category;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface BookRepository extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {
    Page<Book> findAllByCategories(Set<Category> categories, Pageable pageable);
}
