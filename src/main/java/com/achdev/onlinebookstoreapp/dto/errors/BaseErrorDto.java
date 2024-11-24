package com.achdev.onlinebookstoreapp.dto.errors;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public abstract class BaseErrorDto {
    private LocalDateTime timestamp;
    private String status;
    private String message;
}
