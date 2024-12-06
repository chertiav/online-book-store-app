package com.achdev.onlinebookstoreapp.controller;

import com.achdev.onlinebookstoreapp.dto.errors.BookApiErrorResponse;
import com.achdev.onlinebookstoreapp.dto.user.UserDto;
import com.achdev.onlinebookstoreapp.dto.user.UserLoginRequestDto;
import com.achdev.onlinebookstoreapp.dto.user.UserLoginResponseDto;
import com.achdev.onlinebookstoreapp.dto.user.UserRegistrationRequestDto;
import com.achdev.onlinebookstoreapp.exception.RegistrationException;
import com.achdev.onlinebookstoreapp.security.AuthenticationService;
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
public class AuthController {
    private static final String RESPONSE_CODE_OK = "200";
    private static final String RESPONSE_CODE_CREATED = "201";
    private static final String RESPONSE_CODE_BAD_REQUEST = "400";
    private static final String RESPONSE_CODE_UNAUTHORIZED = "401";
    private static final String RESPONSE_CODE_NOT_FOUND = "404";
    private final UserService userService;
    private final AuthenticationService authenticationService;

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
                    )
            }
    )
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/registration")
    public UserDto register(@RequestBody @Valid UserRegistrationRequestDto requestDto)
            throws RegistrationException {
        return userService.register(requestDto);
    }

    @Operation(
            summary = "User login",
            description = "User login",
            responses = {
                    @ApiResponse(
                            responseCode = RESPONSE_CODE_OK,
                            description = "Successful user login"
                    ),
                    @ApiResponse(responseCode = RESPONSE_CODE_BAD_REQUEST,
                            description = "Invalid request",
                            content = @Content(schema = @Schema(
                                    implementation = BookApiErrorResponse.class))
                    ),
                    @ApiResponse(responseCode = RESPONSE_CODE_NOT_FOUND,
                            description = "Not found",
                            content = @Content(schema = @Schema(
                                    implementation = BookApiErrorResponse.class))
                    ),
                    @ApiResponse(responseCode = RESPONSE_CODE_UNAUTHORIZED,
                            description = "Unauthorized"
                    )
            }
    )
    @PostMapping("/login")
    public UserLoginResponseDto register(@RequestBody @Valid UserLoginRequestDto requestDto) {
        return authenticationService.authenticate(requestDto);
    }
}
