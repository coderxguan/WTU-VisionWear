package com.wtu.controller;


import com.wtu.DTO.LoginDTO;
import com.wtu.DTO.RegisterDTO;
import com.wtu.VO.LoginVO;
import com.wtu.result.Result;
import com.wtu.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "认证模块")
public class AuthController {
    // IOC 注入
    private final AuthService authService;

    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public Result<Void> register(@RequestBody RegisterDTO dto) {
        String msg = authService.register(dto);
        if ("注册成功".equals(msg)) {
            return Result.success(null, msg);
        } else {
            return Result.error(msg);
        }
    }

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<LoginVO> login(@RequestBody LoginDTO dto) {
        LoginVO loginVO = authService.login(dto);
        return Result.success(loginVO);
    }
}
