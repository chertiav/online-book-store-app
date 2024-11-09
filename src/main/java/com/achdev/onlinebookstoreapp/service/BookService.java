package com.achdev.onlinebookstoreapp.service;

import com.achdev.onlinebookstoreapp.dto.BookDto;
import com.achdev.onlinebookstoreapp.dto.BookSearchParameters;
import com.achdev.onlinebookstoreapp.dto.CreateBookRequestDto;
import java.util.List;

public interface BookService {
    List<BookDto> findAll();

    BookDto findById(Long id);

    List<BookDto> findAllByParameters(BookSearchParameters parameters);

    BookDto save(CreateBookRequestDto requestDto);

    BookDto updateById(Long id, CreateBookRequestDto requestDto);

    void deleteById(Long id);
}
