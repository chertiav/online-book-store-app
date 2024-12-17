package com.achdev.onlinebookstoreapp.controller;

import com.achdev.onlinebookstoreapp.dto.errors.CommonApiErrorResponse;
import com.achdev.onlinebookstoreapp.dto.user.UserDto;
import com.achdev.onlinebookstoreapp.dto.user.UserLoginRequestDto;
import com.achdev.onlinebookstoreapp.dto.user.UserLoginResponseDto;
import com.achdev.onlinebookstoreapp.dto.user.UserRegistrationRequestDto;
import com.achdev.onlinebookstoreapp.exception.RegistrationException;
import com.achdev.onlinebookstoreapp.security.AuthenticationService;
import com.achdev.onlinebookstoreapp.service.UserService;
import com.achdev.onlinebookstoreapp.util.ApiResponseConstants;
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
    private final UserService userService;
    private final AuthenticationService authenticationService;

    @Operation(
            summary = ApiResponseConstants.USER_REGISTRATION_DESCRIPTION,
            description = ApiResponseConstants.USER_REGISTRATION_DESCRIPTION,
            responses = {
                    @ApiResponse(
                            responseCode = ApiResponseConstants.RESPONSE_CODE_CREATED,
                            description = "Successfully registration a new user"
                    ),
                    @ApiResponse(responseCode = ApiResponseConstants.RESPONSE_CODE_BAD_REQUEST,
                            description = ApiResponseConstants.INVALID_REQUEST_DESCRIPTION,
                            content = @Content(schema = @Schema(
                                    implementation = CommonApiErrorResponse.class))
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
            summary = ApiResponseConstants.USER_LOGIN_DESCRIPTION,
            description = ApiResponseConstants.USER_LOGIN_DESCRIPTION,
            responses = {
                    @ApiResponse(
                            responseCode = ApiResponseConstants.RESPONSE_CODE_OK,
                            description = "Successful user login"
                    ),
                    @ApiResponse(responseCode = ApiResponseConstants.RESPONSE_CODE_BAD_REQUEST,
                            description = ApiResponseConstants.INVALID_REQUEST_DESCRIPTION,
                            content = @Content(schema = @Schema(
                                    implementation = CommonApiErrorResponse.class))
                    ),
                    @ApiResponse(responseCode = ApiResponseConstants.RESPONSE_CODE_NOT_FOUND,
                            description = ApiResponseConstants.NOT_FOUND_DESCRIPTION,
                            content = @Content(schema = @Schema(
                                    implementation = CommonApiErrorResponse.class))
                    ),
                    @ApiResponse(responseCode = ApiResponseConstants.RESPONSE_CODE_UNAUTHORIZED,
                            description = ApiResponseConstants.UNAUTHORIZED_DESCRIPTION
                    )
            }
    )
    @PostMapping("/login")
    public UserLoginResponseDto register(@RequestBody @Valid UserLoginRequestDto requestDto) {
        return authenticationService.authenticate(requestDto);
    }
}
