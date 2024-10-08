package com.achdev.onlinebookstoreapp.service;

import com.achdev.onlinebookstoreapp.model.Book;
import java.util.List;

public interface BookService {
    Book save(Book book);

    List<Book> findAll();
}
