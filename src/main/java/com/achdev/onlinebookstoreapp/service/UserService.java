package com.achdev.onlinebookstoreapp.service;

import com.achdev.onlinebookstoreapp.dto.user.UserDto;
import com.achdev.onlinebookstoreapp.dto.user.UserRegistrationRequestDto;
import com.achdev.onlinebookstoreapp.exception.RegistrationException;

public interface UserService {
    UserDto register(UserRegistrationRequestDto requestDto) throws RegistrationException;
}
