package com.achdev.onlinebookstoreapp.repository.book;

import com.achdev.onlinebookstoreapp.dto.book.BookSearchParameters;
import com.achdev.onlinebookstoreapp.model.Book;
import com.achdev.onlinebookstoreapp.repository.SpecificationBuilder;
import com.achdev.onlinebookstoreapp.repository.SpecificationProviderManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class BookSpecificationBuilder implements SpecificationBuilder<Book> {
    private static final String TITLE_KEY = "title";
    private static final String AUTHOR_KEY = "author";
    private final SpecificationProviderManager<Book> specificationProviderManager;

    @Override
    public Specification<Book> build(BookSearchParameters searchParameters) {
        Specification<Book> bookSpecification = Specification.where(null);
        if (searchParameters.titles() != null && searchParameters.titles().length > 0) {
            bookSpecification = bookSpecification
                    .and(specificationProviderManager.getSpecificationProvider(TITLE_KEY)
                            .getSpecification(searchParameters.titles()));
        }
        if (searchParameters.authors() != null && searchParameters.authors().length > 0) {
            bookSpecification = bookSpecification
                    .and(specificationProviderManager.getSpecificationProvider(AUTHOR_KEY)
                            .getSpecification(searchParameters.authors()));
        }
        return bookSpecification;
    }
}
