package com.achdev.onlinebookstoreapp.mapper;

import com.achdev.onlinebookstoreapp.config.MapperConfig;
import com.achdev.onlinebookstoreapp.dto.user.UserDto;
import com.achdev.onlinebookstoreapp.dto.user.UserRegistrationRequestDto;
import com.achdev.onlinebookstoreapp.exception.EntityNotFoundException;
import com.achdev.onlinebookstoreapp.mapper.password.EncodedMapping;
import com.achdev.onlinebookstoreapp.mapper.password.PasswordEncoderMapper;
import com.achdev.onlinebookstoreapp.model.Role;
import com.achdev.onlinebookstoreapp.model.User;
import com.achdev.onlinebookstoreapp.repository.role.RoleRepository;
import java.util.Set;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(config = MapperConfig.class, uses = {PasswordEncoderMapper.class})
public abstract class UserMapper {
    @Autowired
    protected RoleRepository roleRepository;

    public abstract UserDto toDto(User user);

    @Mapping(source = "password", target = "password", qualifiedBy = EncodedMapping.class)
    public abstract User toModel(UserRegistrationRequestDto requestDto);

    @AfterMapping
    public void setDefaultRole(@MappingTarget User user) {
        Set<Role> roles = Set.of(roleRepository.findByName(Role.RoleName.USER)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Role not found role: " + Role.RoleName.USER.name())));
        user.setRoles(roles);
    }
}
