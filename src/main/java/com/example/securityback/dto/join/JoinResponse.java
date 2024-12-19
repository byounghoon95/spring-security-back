package com.example.securityback.dto.join;

import com.example.securityback.entity.UserEntity;

public record JoinResponse(String username, String role) {
    public static JoinResponse of(UserEntity entity) {
        return new JoinResponse(entity.getUsername(), entity.getRole());
    }
}
