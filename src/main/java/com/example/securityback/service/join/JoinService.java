package com.example.securityback.service.join;

import com.example.securityback.dto.join.JoinRequest;
import com.example.securityback.entity.UserEntity;
import com.example.securityback.enums.ErrorEnum;
import com.example.securityback.enums.Role;
import com.example.securityback.exception.CustomException;
import com.example.securityback.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class JoinService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserEntity join(JoinRequest dto) {
        String username = dto.getUsername();
        String password = dto.getPassword();

        // 유저 중복되면 에러
        userRepository.findByUsername(username)
                .ifPresent(entity -> {
                    throw new CustomException(ErrorEnum.USER_EXIST);
                });

        UserEntity data = new UserEntity(username, bCryptPasswordEncoder.encode(password), Role.ROLE_USER.name());

        return userRepository.save(data);
    }
}