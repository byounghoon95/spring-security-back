package com.example.securityback.dto.auth;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class CustomOAuth2User implements OAuth2User {

    private final UserDto dto;

    public CustomOAuth2User(UserDto dto) {
        this.dto = dto;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collection = new ArrayList<>();

        collection.add((GrantedAuthority) () -> dto.getRole());

        return collection;
    }

    @Override
    public String getName() {
        return dto.getName();
    }

    public String getUsername() {
        return dto.getUsername();
    }
}