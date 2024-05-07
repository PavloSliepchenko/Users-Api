package com.example.usersapi.util;

import com.example.usersapi.dto.CreateUserRequestDto;
import com.example.usersapi.dto.UserResponseDto;
import com.example.usersapi.model.User;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class TestUtil {
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static User getUserFromCreateUserDto(Long userId, CreateUserRequestDto requestDto) {
        User resulUser = new User();
        resulUser.setId(userId);
        resulUser.setEmail(requestDto.email());
        resulUser.setFirstName(requestDto.firstName());
        resulUser.setLastName(requestDto.lastName());
        resulUser.setBirthDate(LocalDate.parse(requestDto.birthDate(), FORMATTER));
        resulUser.setAddress(requestDto.address());
        resulUser.setPhoneNumber(requestDto.phoneNumber());
        return resulUser;
    }

    public static UserResponseDto getResponseDtoFromUser(User user) {
        UserResponseDto userResponseDto = new UserResponseDto();
        userResponseDto.setId(user.getId());
        userResponseDto.setEmail(user.getEmail());
        userResponseDto.setFirstName(user.getFirstName());
        userResponseDto.setLastName(user.getLastName());
        userResponseDto.setBirthDate(user.getBirthDate().format(FORMATTER));
        userResponseDto.setAddress(user.getAddress());
        userResponseDto.setPhoneNumber(user.getPhoneNumber());
        return userResponseDto;
    }
}
