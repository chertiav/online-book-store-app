package com.achdev.onlinebookstoreapp.repository;

import com.achdev.onlinebookstoreapp.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {
}
