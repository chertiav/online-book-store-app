package com.achdev.onlinebookstoreapp.repository;

import com.achdev.onlinebookstoreapp.dto.BookSearchParameters;
import org.springframework.data.jpa.domain.Specification;

public interface SpecificationBuilder<T> {
    Specification<T> build(BookSearchParameters searchParameters);
}
