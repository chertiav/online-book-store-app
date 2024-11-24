package com.achdev.onlinebookstoreapp.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Objects;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

public class FieldMatchValidator implements ConstraintValidator<FieldMatch, Object> {
    private String firstFieldName;
    private String secondFieldName;

    @Override
    public void initialize(FieldMatch constraintAnnotation) {
        this.firstFieldName = constraintAnnotation.firstField();
        this.secondFieldName = constraintAnnotation.secondField();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        BeanWrapper beanWrapper = new BeanWrapperImpl(value);
        Object firstFieldValue = beanWrapper.getPropertyValue(firstFieldName);
        Object secondFieldValue = beanWrapper.getPropertyValue(secondFieldName);
        return Objects.equals(firstFieldValue, secondFieldValue);
    }
}
