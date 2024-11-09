package com.achdev.onlinebookstoreapp.service.impl;

import static com.achdev.onlinebookstoreapp.model.QBook.book;

import com.achdev.onlinebookstoreapp.dto.BookDto;
import com.achdev.onlinebookstoreapp.dto.BookSearchParameters;
import com.achdev.onlinebookstoreapp.dto.CreateBookRequestDto;
import com.achdev.onlinebookstoreapp.exception.EntityNotFoundException;
import com.achdev.onlinebookstoreapp.mapper.BookMapper;
import com.achdev.onlinebookstoreapp.model.Book;
import com.achdev.onlinebookstoreapp.repository.BookRepository;
import com.achdev.onlinebookstoreapp.repository.querydsl.QPredicates;
import com.achdev.onlinebookstoreapp.service.BookService;
import com.querydsl.core.types.dsl.BooleanExpression;
import java.util.List;
import java.util.stream.StreamSupport;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    @Override
    public List<BookDto> findAll() {
        return bookRepository.findAll().stream()
                .map(bookMapper::toDto)
                .toList();
    }

    @Override
    public BookDto findById(Long id) {
        return bookRepository.findById(id)
                .map(bookMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Can't find book by id: " + id));
    }

    @Override
    public List<BookDto> findAllByParameters(BookSearchParameters parameters) {
        BooleanExpression predicates = QPredicates.builder()
                .add(parameters.titles(), book.title::containsIgnoreCase)
                .add(parameters.authors(), book.author::containsIgnoreCase)
                .add(parameters.isbns(), book.isbn::containsIgnoreCase)
                .add(parameters.descriptions(), book.description::containsIgnoreCase)
                .build();

        return StreamSupport.stream(bookRepository.findAll(predicates).spliterator(), false)
                .map(bookMapper::toDto)
                .toList();
    }

    @Override
    public BookDto save(CreateBookRequestDto requestDto) {
        Book book = bookMapper.toModel(requestDto);
        return bookMapper.toDto(bookRepository.save(book));
    }

    @Override
    public BookDto updateById(Long id, CreateBookRequestDto requestDto) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Can't update book by id: " + id));
        bookMapper.updateBookFromDto(requestDto, book);
        return bookMapper.toDto(bookRepository.save(book));
    }

    @Override
    public void deleteById(Long id) {
        bookRepository.deleteById(id);
    }
}
