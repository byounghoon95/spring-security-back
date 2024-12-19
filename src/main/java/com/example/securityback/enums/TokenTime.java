package com.example.securityback.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TokenTime {
    ACCESS("access", 10000L), // 10초
    REFRESH("refresh", 60000L); // 1분
//    ACCESS("access", 600000L),
//    REFRESH("refresh", 86400000L);

    private final String token;
    private final Long expiredAt;
}
