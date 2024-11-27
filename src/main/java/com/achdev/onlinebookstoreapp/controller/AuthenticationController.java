package com.achdev.onlinebookstoreapp.controller;

import com.achdev.onlinebookstoreapp.dto.errors.BookApiErrorResponse;
import com.achdev.onlinebookstoreapp.dto.user.UserDto;
import com.achdev.onlinebookstoreapp.dto.user.UserRegistrationRequestDto;
import com.achdev.onlinebookstoreapp.exception.RegistrationException;
import com.achdev.onlinebookstoreapp.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private static final String RESPONSE_CODE_CREATED = "201";
    private static final String RESPONSE_CODE_BAD_REQUEST = "400";
    private final UserService userService;

    @Operation(
            summary = "Registration a new user",
            description = "Registration a new user",
            responses = {
                    @ApiResponse(
                            responseCode = RESPONSE_CODE_CREATED,
                            description = "Successfully registration a new user"
                    ),
                    @ApiResponse(responseCode = RESPONSE_CODE_BAD_REQUEST,
                            description = "Invalid request",
                            content = @Content(schema = @Schema(
                                    implementation = BookApiErrorResponse.class))
                    ),
            }
    )
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/registration")
    public UserDto register(@RequestBody @Valid UserRegistrationRequestDto requestDto)
            throws RegistrationException {
        return userService.register(requestDto);
    }
}
