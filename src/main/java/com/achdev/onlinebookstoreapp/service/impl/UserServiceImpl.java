package com.achdev.onlinebookstoreapp.service.impl;

import com.achdev.onlinebookstoreapp.dto.user.UserDto;
import com.achdev.onlinebookstoreapp.dto.user.UserRegistrationRequestDto;
import com.achdev.onlinebookstoreapp.exception.RegistrationException;
import com.achdev.onlinebookstoreapp.mapper.UserMapper;
import com.achdev.onlinebookstoreapp.model.User;
import com.achdev.onlinebookstoreapp.repository.user.UserRepository;
import com.achdev.onlinebookstoreapp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto register(UserRegistrationRequestDto requestDto) throws RegistrationException {
        String email = requestDto.getEmail();
        if (userRepository.existsByEmail(email)) {
            throw new RegistrationException("User with email: " + email + " already exists");
        }
        User user = userMapper.toModel(requestDto);
        return userMapper.toDto(userRepository.save(user));
    }
}
