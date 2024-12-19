package com.example.securityback.filter;

import com.example.securityback.entity.UserEntity;
import com.example.securityback.enums.ErrorEnum;
import com.example.securityback.security.CustomUserDetails;
import com.example.securityback.util.JWTUtil;
import com.example.securityback.util.ResponseUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 헤더에서 access키에 담긴 토큰을 꺼냄
        String accessToken = request.getHeader("Authorization");
        accessToken = accessToken != null ? accessToken.split(" ")[1] : null;

        // 토큰이 없다면 다음 필터로 넘김
        if (accessToken == null) {
            filterChain.doFilter(request, response);
            return;
        }

        // 토큰 만료 여부 확인, 만료시 다음 필터로 넘기지 않음
        boolean isExpired = jwtUtil.isExpired(accessToken);
        if (isExpired) {
            ResponseUtils.sendErrorResponse(response, ErrorEnum.ACCESS_TOKEN_EXPIRED);
            return;
        }

        // Access Token 인지 확인
        String type = jwtUtil.getType(accessToken);
        if (!type.equals("access")) {
            ResponseUtils.sendErrorResponse(response, ErrorEnum.ACCESS_TOKEN_INVALID);
            return;
        }

        // username, role 값을 획득
        String username = jwtUtil.getUsername(accessToken);
        String role = jwtUtil.getRole(accessToken);

        UserEntity userEntity = new UserEntity(username, role);
        CustomUserDetails customUserDetails = new CustomUserDetails(userEntity);

        Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }
}