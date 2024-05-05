package com.example.usersapi.service;

import com.example.usersapi.dto.CreateUserRequestDto;
import com.example.usersapi.dto.UserPatchRequestDto;
import com.example.usersapi.dto.UserResponseDto;
import com.example.usersapi.dto.UserResponseDtoWrapper;
import java.util.List;

public interface UserService {
    UserResponseDtoWrapper<UserResponseDto> createUser(CreateUserRequestDto requestDto);

    UserResponseDtoWrapper<UserResponseDto> patchUser(Long userId, UserPatchRequestDto requestDto);

    UserResponseDtoWrapper<UserResponseDto> updateUser(Long userId,CreateUserRequestDto requestDto);

    void deleteUser(Long userId);

    UserResponseDtoWrapper<List<UserResponseDto>> searchByBirthDates(
            String fromDate, String toDate);
}
