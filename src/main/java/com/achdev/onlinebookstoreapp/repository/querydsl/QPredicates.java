package com.achdev.onlinebookstoreapp.repository.querydsl;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class QPredicates {
    private final List<BooleanExpression> predicates = new ArrayList<>();

    public static QPredicates builder() {
        return new QPredicates();
    }

    public <T> QPredicates add(T[] objects, Function<T, BooleanExpression> function) {
        if (objects != null && objects.length > 0) {
            BooleanExpression expression = null;
            for (T object : objects) {
                BooleanExpression newPredicate = function.apply(object);
                expression = (expression == null) ? newPredicate : expression.or(newPredicate);
            }
            if (expression != null) {
                predicates.add(expression);
            }
        }
        return this;
    }

    public BooleanExpression build() {
        return predicates.stream()
                .reduce(BooleanExpression::and)
                .orElseGet(() -> Expressions.asBoolean(true).isTrue());
    }
}
