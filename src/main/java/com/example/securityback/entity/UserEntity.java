package com.example.securityback.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(unique = true)
    private String username;
    private String name;
    private String password;
    private String email;

    private String role;

    public UserEntity(String username,String role) {
        this.username = username;
        this.role = role;
    }

    public UserEntity(String username, String password, String role, String email, String name) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.email = email;
        this.name = name;
    }

    public void setOAuthData(String email) {
        this.email = email;
    }
}