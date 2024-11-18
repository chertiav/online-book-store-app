package com.achdev.onlinebookstoreapp.dto.errors;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
public class ValidationErrorResponseDto {
    private LocalDateTime timestamp;
    private String status;
    private List<ErrorDetailDto> errors;
}
