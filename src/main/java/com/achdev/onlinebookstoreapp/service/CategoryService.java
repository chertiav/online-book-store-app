package com.achdev.onlinebookstoreapp.service;

import com.achdev.onlinebookstoreapp.dto.book.BookDtoWithoutCategoryIds;
import com.achdev.onlinebookstoreapp.dto.category.CategoryDto;
import com.achdev.onlinebookstoreapp.dto.category.CreateCategoryRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CategoryService {
    Page<CategoryDto> findAll(Pageable pageable);

    Page<BookDtoWithoutCategoryIds> findAllBooksByCategoryId(Long id, Pageable pageable);

    CategoryDto findById(Long id);

    CategoryDto save(CreateCategoryRequestDto requestDto);

    CategoryDto updateById(Long id, CreateCategoryRequestDto requestDto);

    void deleteById(Long id);
}
