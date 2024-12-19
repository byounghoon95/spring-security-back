package com.example.securityback.filter;

import com.example.securityback.common.CommonResponse;
import com.example.securityback.dto.login.LoginResponse;
import com.example.securityback.entity.RefreshEntity;
import com.example.securityback.enums.ErrorEnum;
import com.example.securityback.enums.TokenTime;
import com.example.securityback.repository.auth.AuthRepository;
import com.example.securityback.util.JWTUtil;
import com.example.securityback.util.ResponseUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

@Slf4j
public class LoginFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;
    private final AuthRepository authRepository;

    public LoginFilter(AuthenticationManager authenticationManager, JWTUtil jwtUtil, AuthRepository authRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.authRepository = authRepository;
        setFilterProcessesUrl("/api/login");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        if (!request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported");
        }

        // 클라이언트 요청에서 username, password 추출
        String username = obtainUsername(request);
        String password = obtainPassword(request);

        // username과 password를 검증하기 위해 token에 저장
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password, null);

        // ProviderManager
        // token에 담은 검증을 위한 AuthenticationManager로 전달
        return authenticationManager.authenticate(authToken);
    }

    // 로그인 성공시 실행(JWT 발급)
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException {
        String username = authentication.getName();

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

        // 응답 객체 생성
        CommonResponse<LoginResponse> commonResponse = CommonResponse.success(LoginResponse.of(access));

        // JSON 직렬화 및 응답 작성
        response.addCookie(jwtUtil.createCookie("refresh", refresh));
        ResponseUtils.sendResponse(response, commonResponse);
    }

    // 로그인 실패시 실행
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
        log.info("Authentication failed for request [{}]: {}", request.getRequestURI(), exception.getMessage());
        if (exception instanceof BadCredentialsException) {
            ResponseUtils.sendErrorResponse(response, ErrorEnum.LOGIN_FAILED);
        } else if (exception instanceof AuthenticationServiceException) {
            ResponseUtils.sendErrorResponse(response, ErrorEnum.GET_NOT_ALLOWED);
        }
    }
}