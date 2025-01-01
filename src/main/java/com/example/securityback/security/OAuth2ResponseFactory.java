package com.example.securityback.security;

import com.example.securityback.dto.auth.GoogleResponse;
import com.example.securityback.dto.auth.NaverResponse;
import com.example.securityback.dto.auth.OAuth2Response;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class OAuth2ResponseFactory {
    public OAuth2Response createResponse(String provider, Map<String, Object> attributes) {
        switch (provider) {
            case "naver":
                return new NaverResponse(attributes);
            case "google":
                return new GoogleResponse(attributes);
            default:
                throw new IllegalArgumentException("Unsupported provider: " + provider);
        }
    }
}
