package com.achdev.onlinebookstoreapp.exception;

import com.achdev.onlinebookstoreapp.dto.errors.EntityNotFoundErrorResponseDto;
import com.achdev.onlinebookstoreapp.dto.errors.ErrorDetailDto;
import com.achdev.onlinebookstoreapp.dto.errors.ValidationErrorResponseDto;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class CustomGlobalExceptionHandler extends ResponseEntityExceptionHandler {
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status,
            @NonNull WebRequest request) {
        ValidationErrorResponseDto errorResponse = new ValidationErrorResponseDto();
        errorResponse.setTimestamp(LocalDateTime.now());
        errorResponse.setStatus(HttpStatus.BAD_REQUEST.getReasonPhrase());
        List<ErrorDetailDto> errorMessages = ex.getBindingResult().getAllErrors().stream()
                .map(this::getErrorDetails)
                .toList();
        errorResponse.setErrors(errorMessages);
        return new ResponseEntity<>(errorResponse, headers, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    protected ResponseEntity<Object> handleEntityNotFoundException(
            EntityNotFoundException ex,
            @NonNull WebRequest request) {
        EntityNotFoundErrorResponseDto exceptionResponseDto =
                new EntityNotFoundErrorResponseDto();
        exceptionResponseDto.setTimestamp(LocalDateTime.now());
        exceptionResponseDto.setStatus(HttpStatus.NOT_FOUND.getReasonPhrase());
        exceptionResponseDto.setError("Entity not found");
        exceptionResponseDto.setMessage(ex.getMessage() != null
                ? ex.getMessage()
                : "Object was not found");
        exceptionResponseDto.setPath(request.getDescription(false));
        return new ResponseEntity<>(exceptionResponseDto, HttpStatus.NOT_FOUND);
    }

    private ErrorDetailDto getErrorDetails(ObjectError e) {
        ErrorDetailDto errorDetail = new ErrorDetailDto();
        if (e instanceof FieldError) {
            errorDetail.setField(((FieldError) e).getField());
        } else {
            errorDetail.setField(e.getObjectName());
        }
        errorDetail.setMessage(e.getDefaultMessage() != null
                ? e.getDefaultMessage()
                : "Something went wrong, please check your input");
        return errorDetail;
    }
}
