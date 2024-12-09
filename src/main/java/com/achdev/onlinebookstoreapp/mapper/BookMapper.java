package com.achdev.onlinebookstoreapp.mapper;

import com.achdev.onlinebookstoreapp.config.MapperConfig;
import com.achdev.onlinebookstoreapp.dto.book.BookDto;
import com.achdev.onlinebookstoreapp.dto.book.BookDtoWithoutCategoryIds;
import com.achdev.onlinebookstoreapp.dto.book.CreateBookRequestDto;
import com.achdev.onlinebookstoreapp.mapper.category.CategoryListToSetMapper;
import com.achdev.onlinebookstoreapp.mapper.category.CategoryMapping;
import com.achdev.onlinebookstoreapp.model.Book;
import com.achdev.onlinebookstoreapp.model.Category;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class, uses = CategoryListToSetMapper.class)
public interface BookMapper {
    @Mapping(target = "categoryIds", ignore = true)
    BookDto toDto(Book book);

    @AfterMapping
    default void setCategoryIds(@MappingTarget BookDto bookDto, Book book) {
        Set<Long> categoryIds = book.getCategories().stream()
                .map(Category::getId)
                .collect(Collectors.toSet());
        bookDto.setCategoryIds(categoryIds);
    }

    BookDtoWithoutCategoryIds toDtoWithoutCategories(Book book);

    @Mapping(source = "categories", target = "categories", qualifiedBy = CategoryMapping.class)
    Book toModel(CreateBookRequestDto requestDto);

    @Mapping(source = "categories", target = "categories", qualifiedBy = CategoryMapping.class)
    void updateBookFromDto(CreateBookRequestDto requestDto, @MappingTarget Book book);
}
