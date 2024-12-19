package com.example.securityback.controller.auth;

import com.example.securityback.common.CommonResponse;
import com.example.securityback.dto.auth.TokenDto;
import com.example.securityback.dto.login.LoginResponse;
import com.example.securityback.service.auth.AuthService;
import com.example.securityback.util.JWTUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/auth")
@RestController
public class AuthController {

    private final JWTUtil jwtUtil;
    private final AuthService authService;

    @PostMapping("/reissue")
    public CommonResponse reissue(HttpServletRequest request, HttpServletResponse response) {
        String refresh = extractRefreshToken(request);

        TokenDto token = authService.reissue(refresh);

        response.addCookie(jwtUtil.createCookie("refresh", token.getRefreshToken()));

        return CommonResponse.success(LoginResponse.of(token.getAccessToken()));
    }

    private String extractRefreshToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("refresh")) {
                return cookie.getValue();
            }
        }

        return null;
    }
}