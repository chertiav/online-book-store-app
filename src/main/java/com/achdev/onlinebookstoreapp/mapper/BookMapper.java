package com.achdev.onlinebookstoreapp.mapper;

import com.achdev.onlinebookstoreapp.config.MapperConfig;
import com.achdev.onlinebookstoreapp.dto.BookDto;
import com.achdev.onlinebookstoreapp.dto.CreateBookRequestDto;
import com.achdev.onlinebookstoreapp.model.Book;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface BookMapper {
    BookDto toDto(Book book);

    Book toModel(CreateBookRequestDto requestDto);
}
