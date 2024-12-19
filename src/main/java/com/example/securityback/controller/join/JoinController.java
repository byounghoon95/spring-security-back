package com.example.securityback.controller.join;

import com.example.securityback.common.CommonResponse;
import com.example.securityback.dto.join.JoinRequest;
import com.example.securityback.dto.join.JoinResponse;
import com.example.securityback.service.join.JoinService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/join")
public class JoinController {

    private final JoinService joinService;

    @GetMapping()
    public String get() {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        return "Main Controller : " + name;
    }

    @PostMapping()
    public CommonResponse join(@RequestBody JoinRequest dto) {
        return CommonResponse.success(JoinResponse.of(joinService.join(dto)));
    }
}