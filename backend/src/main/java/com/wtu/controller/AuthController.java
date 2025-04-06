package com.wtu.controller;


import com.wtu.DTO.LoginDTO;
import com.wtu.DTO.RegisterDTO;
import com.wtu.result.Result;
import com.wtu.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "认证模块")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody RegisterDTO dto) {
        authService.register(dto);
        return Result.success(null);
    }

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<String> login(@Valid @RequestBody LoginDTO dto) {
        String token = authService.login(dto);
        return Result.success(token);
    }
}
