package com.example.securityback.util;

import com.example.securityback.common.CommonResponse;
import com.example.securityback.enums.ErrorEnum;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class ResponseUtils {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static <T> void sendResponse(HttpServletResponse response, T data) throws IOException {
        CommonResponse<T> commonResponse = CommonResponse.success(data);

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setStatus(HttpStatus.OK.value());

        objectMapper.writeValue(response.getWriter(), commonResponse);
    }

    public static void sendErrorResponse(HttpServletResponse response,
                                         ErrorEnum errorEnum) throws IOException {
        sendErrorResponse(response, errorEnum, HttpServletResponse.SC_UNAUTHORIZED);
    }

    public static void sendErrorResponse(HttpServletResponse response,
                                         ErrorEnum errorEnum,
                                         int httpStatus) throws IOException {
        CommonResponse commonResponse = CommonResponse.error(
                errorEnum.getCode(),
                errorEnum.getMessage()
        );

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setStatus(httpStatus);

        objectMapper.writeValue(response.getWriter(), commonResponse);
    }
}