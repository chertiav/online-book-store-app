package com.achdev.onlinebookstoreapp.service.impl;

import com.achdev.onlinebookstoreapp.dto.book.BookDto;
import com.achdev.onlinebookstoreapp.dto.book.BookDtoWithoutCategoryIds;
import com.achdev.onlinebookstoreapp.dto.book.BookSearchParameters;
import com.achdev.onlinebookstoreapp.dto.book.CreateBookRequestDto;
import com.achdev.onlinebookstoreapp.exception.EntityNotFoundException;
import com.achdev.onlinebookstoreapp.mapper.BookMapper;
import com.achdev.onlinebookstoreapp.model.Book;
import com.achdev.onlinebookstoreapp.repository.book.BookRepository;
import com.achdev.onlinebookstoreapp.repository.book.BookSpecificationBuilder;
import com.achdev.onlinebookstoreapp.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final BookSpecificationBuilder bookSpecificationBuilder;

    @Override
    public Page<BookDto> findAll(Pageable pageable) {
        return bookRepository.findAll(pageable)
                .map(bookMapper::toDto);
    }

    @Override
    public Page<BookDtoWithoutCategoryIds> findAllByCategoryId(Long categoryId, Pageable pageable) {
        return bookRepository.findAllByCategories(categoryId, pageable)
                .map(bookMapper::toDtoWithoutCategories);
    }

    @Override
    public BookDto findById(Long id) {
        return bookRepository.findById(id)
                .map(bookMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Can't find book by id: " + id));
    }

    @Override
    public Page<BookDto> search(BookSearchParameters searchParameters, Pageable pageable) {
        Specification<Book> bookSpecification = bookSpecificationBuilder.build(searchParameters);
        return bookRepository.findAll(bookSpecification, pageable)
                .map(bookMapper::toDto);
    }

    @Transactional
    @Override
    public BookDto save(CreateBookRequestDto requestDto) {
        Book book = bookMapper.toModel(requestDto);
        return bookMapper.toDto(bookRepository.save(book));
    }

    @Transactional
    @Override
    public BookDto updateById(Long id, CreateBookRequestDto requestDto) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Can't update book by id: " + id));
        bookMapper.updateBookFromDto(requestDto, book);
        return bookMapper.toDto(bookRepository.save(book));
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        bookRepository.deleteById(id);
    }
}
