package com.achdev.onlinebookstoreapp.dto.page;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.domain.Page;

@Data
@AllArgsConstructor
public class PageResponse<T> {
    private List<T> content;
    private PageMetadata metadata;

    public static <T> PageResponse<T> of(Page<T> page) {
        return new PageResponse<>(page.getContent(), getMetadata(page));

    }

    private static <T> PageMetadata getMetadata(Page<T> page) {
        return new PageMetadata(
                page.getNumber(),
                page.getSize(),
                page.getTotalPages(),
                page.getTotalElements()
        );
    }
}
