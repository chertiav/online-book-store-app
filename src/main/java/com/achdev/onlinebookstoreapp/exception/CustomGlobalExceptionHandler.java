package com.achdev.onlinebookstoreapp.exception;

import com.achdev.onlinebookstoreapp.dto.errors.BookApiErrorResponse;
import com.achdev.onlinebookstoreapp.dto.errors.ErrorDetailDto;
import java.time.LocalDateTime;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.authorization.AuthorizationDeniedException;
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
        return buildResponseEntity(
                HttpStatus.BAD_REQUEST,
                LocalDateTime.now(),
                ex.getBindingResult().getAllErrors().stream()
                        .map(this::getErrorDetails)
                        .toList()
        );
    }

    @ExceptionHandler(EntityNotFoundException.class)
    protected ResponseEntity<Object> handleEntityNotFoundException(EntityNotFoundException ex) {
        return buildResponseEntity(
                HttpStatus.NOT_FOUND,
                LocalDateTime.now(),
                getErrorMessage(ex, "Entity was not found")
        );
    }

    @ExceptionHandler(RegistrationException.class)
    protected ResponseEntity<Object> handleRegistrationException(RegistrationException ex) {
        return buildResponseEntity(
                HttpStatus.BAD_REQUEST,
                LocalDateTime.now(),
                getErrorMessage(ex, "Bad credentials for registration")
        );
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    protected ResponseEntity<Object> handleRegistrationException(AuthorizationDeniedException ex) {
        return buildResponseEntity(
                HttpStatus.FORBIDDEN,
                LocalDateTime.now(),
                getErrorMessage(ex, "Access denied")
        );
    }

    private ResponseEntity<Object> buildResponseEntity(
            HttpStatus status,
            LocalDateTime timestamp,
            Object errorMessages) {
        BookApiErrorResponse exceptionResponseDto =
                new BookApiErrorResponse(status, timestamp, errorMessages);
        return new ResponseEntity<>(exceptionResponseDto, status);
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
                : "Validation failed for object, please check your input");
        return errorDetail;
    }

    private String getErrorMessage(Exception ex, String defaultMessage) {
        return ex.getMessage() != null ? ex.getMessage() : defaultMessage;
    }
}
