package com.example.securityback.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TokenDto {
    String accessToken;
    String refreshToken;
}
