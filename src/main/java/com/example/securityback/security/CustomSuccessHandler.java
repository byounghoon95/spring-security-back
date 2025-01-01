package com.example.securityback.security;

import com.example.securityback.common.CommonResponse;
import com.example.securityback.dto.auth.CustomOAuth2User;
import com.example.securityback.dto.login.LoginResponse;
import com.example.securityback.entity.RefreshEntity;
import com.example.securityback.enums.TokenTime;
import com.example.securityback.repository.auth.AuthRepository;
import com.example.securityback.util.JWTUtil;
import com.example.securityback.util.ResponseUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

@Component
@RequiredArgsConstructor
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JWTUtil jwtUtil;
    private final AuthRepository authRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        //OAuth2User
        CustomOAuth2User customUserDetails = (CustomOAuth2User) authentication.getPrincipal();

        String username = customUserDetails.getUsername();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();

        //토큰 생성
        String access = jwtUtil.createJwt("access", username, role, TokenTime.ACCESS.getExpiredAt());
        String refresh = jwtUtil.createJwt("refresh", username, role, TokenTime.REFRESH.getExpiredAt());

        //Refresh 토큰 저장
        RefreshEntity refreshToken = jwtUtil.createRefreshToken(username, refresh, TokenTime.REFRESH.getExpiredAt());
        authRepository.save(refreshToken);

        response.addCookie(jwtUtil.createCookie("access", access));
        response.addCookie(jwtUtil.createCookie("refresh", refresh));
        response.sendRedirect("http://localhost:3000/main");
    }
}