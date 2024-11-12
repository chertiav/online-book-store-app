package com.achdev.onlinebookstoreapp.repository.book.spec;

import com.achdev.onlinebookstoreapp.model.Book;
import com.achdev.onlinebookstoreapp.repository.SpecificationProvider;
import java.util.Arrays;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class TitleSpecificationProvider implements SpecificationProvider<Book> {
    private static final String TITLE_KEY = "title";

    @Override
    public Specification<Book> getSpecification(String[] params) {
        return (root, query, criteriaBuilder) ->
                root.get(TITLE_KEY).in(Arrays.stream(params).toArray());
    }

    @Override
    public String getKey() {
        return TITLE_KEY;
    }
}
