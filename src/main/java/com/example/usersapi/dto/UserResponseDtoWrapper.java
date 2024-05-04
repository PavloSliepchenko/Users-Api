package com.example.usersapi.dto;

import lombok.Data;

@Data
public class UserResponseDtoWrapper<T> {
    private T data;

    public UserResponseDtoWrapper(T data) {
        this.data = data;
    }
}
