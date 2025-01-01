package com.example.securityback.dto.auth;

import lombok.Getter;

@Getter
public class UserDto {
    private String username;
    private String name;
    private String role;

    public UserDto(String username, String name, String role) {
        this.username = username;
        this.name = name;
        this.role = role;
    }
}
