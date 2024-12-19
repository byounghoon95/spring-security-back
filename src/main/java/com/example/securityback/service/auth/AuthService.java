package com.example.securityback.service.auth;

import com.example.securityback.dto.auth.TokenDto;
import com.example.securityback.entity.RefreshEntity;
import com.example.securityback.enums.ErrorEnum;
import com.example.securityback.enums.TokenTime;
import com.example.securityback.exception.CustomException;
import com.example.securityback.repository.auth.AuthRepository;
import com.example.securityback.util.JWTUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class AuthService {

    private final AuthRepository authRepository;
    private final JWTUtil jwtUtil;

    @Transactional
    public TokenDto reissue(String refresh) {
        if (refresh == null) {
            throw new CustomException(ErrorEnum.REFRESH_TOKEN_ISNULL.getCode(), ErrorEnum.REFRESH_TOKEN_ISNULL.getMessage());
        }

        boolean isExpired = jwtUtil.isExpired(refresh);
        if (isExpired) {
            throw new CustomException(ErrorEnum.REFRESH_TOKEN_EXPIRED.getCode(), ErrorEnum.REFRESH_TOKEN_EXPIRED.getMessage());
        }

        // 리프레시 토큰인지 확인
        String type = jwtUtil.getType(refresh);
        if (!type.equals("refresh")) {
            throw new CustomException(ErrorEnum.REFRESH_TOKEN_INVALID.getCode(), ErrorEnum.REFRESH_TOKEN_INVALID.getMessage());
        }

        String username = jwtUtil.getUsername(refresh);
        String role = jwtUtil.getRole(refresh);

        String newAccess = jwtUtil.createJwt("access", username, role, TokenTime.ACCESS.getExpiredAt());
        String newRefresh = jwtUtil.createJwt("refresh", username, role, TokenTime.REFRESH.getExpiredAt());

        // Refresh 토큰 저장 DB에 기존의 Refresh 토큰 삭제 후 새 Refresh 토큰 저장
        authRepository.deleteByToken(refresh);

        RefreshEntity refreshToken = jwtUtil.createRefreshToken(username, newRefresh, TokenTime.REFRESH.getExpiredAt());
        authRepository.save(refreshToken);

        return new TokenDto(newAccess, newRefresh);
    }

    @Transactional
    public void logout(String refresh) {
        authRepository.deleteByToken(refresh);
    }
}