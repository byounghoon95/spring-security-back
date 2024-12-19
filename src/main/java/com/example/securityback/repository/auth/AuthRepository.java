package com.example.securityback.repository.auth;

import com.example.securityback.entity.RefreshEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthRepository extends JpaRepository<RefreshEntity, Long> {

    boolean existsByToken(String token);

    void deleteByToken(String token);
}