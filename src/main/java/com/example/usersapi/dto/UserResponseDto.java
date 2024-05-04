package com.example.usersapi.dto;

import lombok.Data;

@Data
public class UserResponseDto {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String birthDate;
    private String address;
    private String phoneNumber;
}
