package com.achdev.onlinebookstoreapp.service;

import com.achdev.onlinebookstoreapp.dto.BookDto;
import com.achdev.onlinebookstoreapp.dto.CreateBookRequestDto;
import java.util.List;

public interface BookService {
    BookDto save(CreateBookRequestDto requestDto);

    List<BookDto> findAll();

    BookDto findById(Long id);
}
