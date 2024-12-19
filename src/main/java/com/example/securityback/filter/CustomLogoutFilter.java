package com.example.securityback.filter;

import com.example.securityback.enums.ErrorEnum;
import com.example.securityback.repository.auth.AuthRepository;
import com.example.securityback.service.auth.AuthService;
import com.example.securityback.util.JWTUtil;
import com.example.securityback.util.ResponseUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

@RequiredArgsConstructor
public class CustomLogoutFilter extends GenericFilterBean {

    private final JWTUtil jwtUtil;
    private final AuthRepository authRepository;
    private final AuthService authService;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
    }
    private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        String requestUri = request.getRequestURI();
        if (!requestUri.matches("^\\/api/logout$")) {
            filterChain.doFilter(request, response);
            return;
        }

        String requestMethod = request.getMethod();
        if (!requestMethod.equals("POST")) {
            filterChain.doFilter(request, response);
            return;
        }

        String refresh = null;
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("refresh")) {
                refresh = cookie.getValue();
            }
        }

        if (refresh == null) {
            ResponseUtils.sendErrorResponse(response, ErrorEnum.REFRESH_TOKEN_ISNULL);
            return;
        }

        boolean isExpired = jwtUtil.isExpired(refresh);
        if (isExpired) {
            ResponseUtils.sendErrorResponse(response, ErrorEnum.REFRESH_TOKEN_EXPIRED);
        }

        // 토큰이 refresh인지 확인 (발급시 페이로드에 명시)
        String category = jwtUtil.getType(refresh);
        if (!category.equals("refresh")) {
            ResponseUtils.sendErrorResponse(response, ErrorEnum.REFRESH_TOKEN_INVALID);
            return;
        }

        boolean isExist = authRepository.existsByToken(refresh);
        if (!isExist) {
            ResponseUtils.sendErrorResponse(response, ErrorEnum.REFRESH_TOKEN_NOEXIST);
            return;
        }

        authService.logout(refresh);

        // Refresh 토큰 Cookie 값 0
        Cookie cookie = new Cookie("refresh", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");

        response.addCookie(cookie);
        ResponseUtils.sendResponse(response, "Logout Success");
    }
}