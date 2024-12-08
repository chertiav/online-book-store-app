package com.achdev.onlinebookstoreapp.service;

import com.achdev.onlinebookstoreapp.dto.book.BookDto;
import com.achdev.onlinebookstoreapp.dto.book.BookDtoWithoutCategoryIds;
import com.achdev.onlinebookstoreapp.dto.book.BookSearchParameters;
import com.achdev.onlinebookstoreapp.dto.book.CreateBookRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookService {
    Page<BookDto> findAll(Pageable pageable);

    Page<BookDtoWithoutCategoryIds> findAllByCategoryId(Long categoryId, Pageable pageable);

    BookDto findById(Long id);

    Page<BookDto> search(BookSearchParameters searchParameters, Pageable pageable);

    BookDto save(CreateBookRequestDto requestDto);

    BookDto updateById(Long id, CreateBookRequestDto requestDto);

    void deleteById(Long id);
}
