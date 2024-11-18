package com.achdev.onlinebookstoreapp.dto.errors;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class EntityNotFoundErrorResponseDto {
    private LocalDateTime timestamp;
    private String status;
    private String error;
    private String message;
    private String path;
}
