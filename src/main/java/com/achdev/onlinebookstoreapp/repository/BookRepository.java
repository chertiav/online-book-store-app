package com.achdev.onlinebookstoreapp.repository;

import com.achdev.onlinebookstoreapp.model.Book;
import java.util.List;

public interface BookRepository {
    Book save(Book book);

    List<Book> findAll();
}
