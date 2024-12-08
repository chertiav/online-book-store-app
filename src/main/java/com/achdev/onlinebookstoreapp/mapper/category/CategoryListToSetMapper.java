package com.achdev.onlinebookstoreapp.mapper.category;

import com.achdev.onlinebookstoreapp.model.Category;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryListToSetMapper {

    @CategoryMapping
    public Set<Category> map(List<Long> categories) {
        return categories.stream()
                .map(Category::new)
                .collect(Collectors.toSet());
    }
}
