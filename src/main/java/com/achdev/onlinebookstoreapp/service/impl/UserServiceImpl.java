package com.achdev.onlinebookstoreapp.service.impl;

import com.achdev.onlinebookstoreapp.dto.user.UserDto;
import com.achdev.onlinebookstoreapp.dto.user.UserRegistrationRequestDto;
import com.achdev.onlinebookstoreapp.exception.EntityNotFoundException;
import com.achdev.onlinebookstoreapp.exception.RegistrationException;
import com.achdev.onlinebookstoreapp.mapper.UserMapper;
import com.achdev.onlinebookstoreapp.model.Role;
import com.achdev.onlinebookstoreapp.model.Role.RoleName;
import com.achdev.onlinebookstoreapp.model.User;
import com.achdev.onlinebookstoreapp.repository.role.RoleRepository;
import com.achdev.onlinebookstoreapp.repository.user.UserRepository;
import com.achdev.onlinebookstoreapp.service.UserService;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto register(UserRegistrationRequestDto requestDto) throws RegistrationException {
        String email = requestDto.getEmail();
        if (userRepository.existsByEmail(email)) {
            throw new RegistrationException("User with email: " + email + " already exists");
        }
        User user = userMapper.toModel(requestDto);
        user.setRoles(getSetOfUserRole());
        return userMapper.toDto(userRepository.save(user));
    }

    private Set<Role> getSetOfUserRole() {
        return Set.of(roleRepository.findByName(RoleName.USER)
                .orElseThrow(() -> new EntityNotFoundException("Role not found role: "
                                                               + RoleName.USER.name())));
    }
}
