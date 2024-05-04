package com.example.usersapi.mapper;

import com.example.usersapi.config.MapperConfig;
import com.example.usersapi.dto.CreateUserRequestDto;
import com.example.usersapi.dto.UserResponseDto;
import com.example.usersapi.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface UserMapper {
    @Mapping(target = "birthDate", source = "birthDate", dateFormat = "dd/MM/yyyy")
    User toModel(CreateUserRequestDto requestDto);

    @Mapping(target = "birthDate", source = "birthDate", dateFormat = "dd/MM/yyyy")
    UserResponseDto toDto(User user);
}
