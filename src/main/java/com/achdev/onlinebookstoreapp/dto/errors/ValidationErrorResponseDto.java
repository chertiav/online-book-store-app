package com.achdev.onlinebookstoreapp.dto.errors;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ValidationErrorResponseDto extends BaseErrorDto {
    private List<ErrorDetailDto> errors;
}
