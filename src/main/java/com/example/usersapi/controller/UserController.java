package com.example.usersapi.controller;

import com.example.usersapi.dto.CreateUserRequestDto;
import com.example.usersapi.dto.UserPatchRequestDto;
import com.example.usersapi.dto.UserResponseDto;
import com.example.usersapi.dto.UserResponseDtoWrapper;
import com.example.usersapi.service.UserService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/users")
public class UserController {
    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDtoWrapper<UserResponseDto> addUser(
            @RequestBody @Valid CreateUserRequestDto requestDto) {
        return userService.createUser(requestDto);
    }

    @PatchMapping(value = "/{userId}")
    public UserResponseDtoWrapper<UserResponseDto> patchUser(
            @PathVariable Long userId, @RequestBody @Valid UserPatchRequestDto requestDto) {
        return userService.patchUser(userId, requestDto);
    }

    @PutMapping(value = "/{userId}")
    public UserResponseDtoWrapper<UserResponseDto> updateUser(
            @PathVariable Long userId, @RequestBody @Valid CreateUserRequestDto requestDto) {
        return userService.updateUser(userId, requestDto);
    }

    @DeleteMapping(value = "/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUserById(@PathVariable Long userId) {
        userService.deleteUser(userId);
    }

    @GetMapping(value = "/search")
    public UserResponseDtoWrapper<List<UserResponseDto>> searchByBirthDates(
            @RequestParam String fromDate, @RequestParam String toDate
    ) {
        return userService.searchByBirthDates(fromDate, toDate);
    }
}
