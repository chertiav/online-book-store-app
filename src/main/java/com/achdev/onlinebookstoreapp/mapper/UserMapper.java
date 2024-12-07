package com.achdev.onlinebookstoreapp.mapper;

import com.achdev.onlinebookstoreapp.config.MapperConfig;
import com.achdev.onlinebookstoreapp.dto.user.UserDto;
import com.achdev.onlinebookstoreapp.dto.user.UserRegistrationRequestDto;
import com.achdev.onlinebookstoreapp.mapper.password.EncodedMapping;
import com.achdev.onlinebookstoreapp.mapper.password.PasswordEncoderMapper;
import com.achdev.onlinebookstoreapp.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class, uses = {PasswordEncoderMapper.class})
public interface UserMapper {
    UserDto toDto(User user);

    @Mapping(source = "password", target = "password", qualifiedBy = EncodedMapping.class)
    User toModel(UserRegistrationRequestDto requestDto);
}
