package com.achdev.onlinebookstoreapp.dto.errors;

import lombok.Data;

@Data
public class ErrorDetailDto {
    private String field;
    private String message;
}
