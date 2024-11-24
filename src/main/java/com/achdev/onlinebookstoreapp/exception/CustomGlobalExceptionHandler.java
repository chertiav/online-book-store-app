package com.achdev.onlinebookstoreapp.exception;

import com.achdev.onlinebookstoreapp.dto.errors.BaseErrorDto;
import com.achdev.onlinebookstoreapp.dto.errors.EntityNotFoundErrorResponseDto;
import com.achdev.onlinebookstoreapp.dto.errors.ErrorDetailDto;
import com.achdev.onlinebookstoreapp.dto.errors.InstanceCreationErrorDto;
import com.achdev.onlinebookstoreapp.dto.errors.RegistrationErrorDto;
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
        HttpStatus errorStatus = HttpStatus.BAD_REQUEST;
        ValidationErrorResponseDto exceptionResponseDto = createErrorResponse(
                ValidationErrorResponseDto.class,
                errorStatus.getReasonPhrase(),
                "Validation failed for object");
        List<ErrorDetailDto> errorMessages = ex.getBindingResult().getAllErrors().stream()
                .map(this::getErrorDetails)
                .toList();
        exceptionResponseDto.setErrors(errorMessages);
        return new ResponseEntity<>(exceptionResponseDto, headers, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    protected ResponseEntity<Object> handleEntityNotFoundException(EntityNotFoundException ex) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        EntityNotFoundErrorResponseDto exceptionResponseDto = createErrorResponse(
                EntityNotFoundErrorResponseDto.class,
                status.getReasonPhrase(),
                getErrorMessage(ex, "Entity was not found"));
        return new ResponseEntity<>(exceptionResponseDto, status);
    }

    @ExceptionHandler(RegistrationException.class)
    protected ResponseEntity<Object> handleRegistrationException(RegistrationException ex) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        RegistrationErrorDto exceptionResponseDto = createErrorResponse(
                RegistrationErrorDto.class,
                status.getReasonPhrase(),
                getErrorMessage(ex, "Bad credentials for registration"));
        return new ResponseEntity<>(exceptionResponseDto, status);
    }

    @ExceptionHandler(InstanceCreationException.class)
    protected ResponseEntity<Object> handleInstanceCreationException(InstanceCreationException ex) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        InstanceCreationErrorDto exceptionResponseDto = createErrorResponse(
                InstanceCreationErrorDto.class,
                status.getReasonPhrase(),
                getErrorMessage(ex, "Error occurred while creating instance"));
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
                : "Something went wrong, please check your input");
        return errorDetail;
    }

    private <T extends BaseErrorDto> T createErrorResponse(
            Class<T> responseClass,
            String errorStatus,
            String errorMessage
    ) {
        T responseDto = createInstance(responseClass);
        responseDto.setTimestamp(LocalDateTime.now());
        responseDto.setStatus(errorStatus);
        responseDto.setMessage(errorMessage);
        return responseDto;
    }

    private <T> T createInstance(Class<T> clazz) {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new InstanceCreationException("Failed to create instance of class: "
                                                + clazz.getName());
        }
    }

    private String getErrorMessage(Exception ex, String message) {
        return ex.getMessage() != null ? ex.getMessage() : message;
    }
}
