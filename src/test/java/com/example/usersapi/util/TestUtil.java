package com.example.usersapi.util;

import com.example.usersapi.dto.CreateUserRequestDto;
import com.example.usersapi.dto.UserResponseDto;
import com.example.usersapi.model.User;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class TestUtil {
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    public static final CreateUserRequestDto REQUEST_DTO;
    public static final User USER1;
    public static final User USER2;
    public static final User USER3;
    public static final User USER4;
    public static final User USER5;
    public static final UserResponseDto RESPONSE_DTO1;
    public static final UserResponseDto RESPONSE_DTO2;
    public static final UserResponseDto RESPONSE_DTO3;
    public static final UserResponseDto RESPONSE_DTO4;
    public static final UserResponseDto RESPONSE_DTO5;

    static {
        REQUEST_DTO = new CreateUserRequestDto(
                "user@gmail.com",
                "Bob",
                "Jackson",
                "01/02/1990",
                "414 Union Ave, Brooklyn, NY 11211",
                "(111) 111-1111"
        );
        USER1 = getUserFromCreateUserDto(1L, REQUEST_DTO);

        USER2 = new User();
        USER2.setId(2L);
        USER2.setFirstName("Jane");
        USER2.setLastName("Smith");
        USER2.setEmail("user2@example.com");
        USER2.setBirthDate(LocalDate.of(1985, 5, 15));

        USER3 = new User();
        USER3.setId(3L);
        USER3.setFirstName("Alice");
        USER3.setLastName("Johnson");
        USER3.setEmail("user3@example.com");
        USER3.setBirthDate(LocalDate.of(1988, 9, 20));

        USER4 = new User();
        USER4.setId(4L);
        USER4.setFirstName("Bob");
        USER4.setLastName("Brown");
        USER4.setEmail("user4@example.com");
        USER4.setBirthDate(LocalDate.of(1995, 3, 10));

        USER5 = new User();
        USER5.setId(5L);
        USER5.setFirstName("Emily");
        USER5.setLastName("Davis");
        USER5.setEmail("user5@example.com");
        USER5.setBirthDate(LocalDate.of(1992, 11, 28));

        RESPONSE_DTO1 = getResponseDtoFromUser(USER1);
        RESPONSE_DTO2 = getResponseDtoFromUser(USER2);
        RESPONSE_DTO3 = getResponseDtoFromUser(USER3);
        RESPONSE_DTO4 = getResponseDtoFromUser(USER4);
        RESPONSE_DTO5 = getResponseDtoFromUser(USER5);
    }

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
