package com.example.usersapi.dto;

import jakarta.validation.constraints.Email;

public record UserPatchRequestDto(
        @Email
        String email,
        String firstName,
        String lastName,
        String birthDate,
        String address,
        String phoneNumber
) {
}
