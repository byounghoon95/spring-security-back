package com.example.securityback.dto.login;



public record LoginResponse(String accessToken) {
    public static LoginResponse of(String accessToken) {
        return new LoginResponse(accessToken);
    }
}
