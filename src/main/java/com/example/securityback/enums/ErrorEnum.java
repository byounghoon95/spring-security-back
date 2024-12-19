package com.example.securityback.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/*
 * 1xxx : 유저 관련
 * 2xxx : 토큰 관련
 * */
@Getter
@RequiredArgsConstructor
public enum ErrorEnum {

    SUCCESS("0000", "SUCCESS"),
    USER_EXIST("1000", "User Already Exist"),
    NO_USER("1001", "NOT FOUND USER"),
    LOGIN_FAILED("1002", "ID Or Password Is Incorrect"),
    GET_NOT_ALLOWED("1003", "GET Method Is Not Allowed"),
    NO_AUTHORIZATION("1004", "No Authorization"),
    ACCESS_TOKEN_EXPIRED("2000", "Access Token Is Expired"),
    ACCESS_TOKEN_INVALID("2001", "Access Token Invalid"),
    REFRESH_TOKEN_ISNULL("2002", "Refresh Token Is Null"),
    REFRESH_TOKEN_EXPIRED("2003", "Refresh Token Expired"),
    REFRESH_TOKEN_INVALID("2004", "Refresh Token Invalid"),
    REFRESH_TOKEN_NOEXIST("2005", "Refresh Token No Exist"),
    UNKNOWN_ERROR("9999", "UNKNOWN_ERROR");

    private final String code;
    private final String message;
}
