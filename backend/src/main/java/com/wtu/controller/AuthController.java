package com.wtu.controller;


import com.wtu.DTO.LoginDTO;
import com.wtu.DTO.RegisterDTO;
import com.wtu.VO.LoginVO;
import com.wtu.entity.User;
import com.wtu.result.Result;
import com.wtu.service.AuthService;
import com.wtu.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@Tag(name = "认证模块")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserService userService;

    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public Result<Void> register(@RequestBody RegisterDTO dto) {
        authService.register(dto);
        return Result.success(null);
    }

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<LoginVO> login(@RequestBody LoginDTO dto) {
        LoginVO loginVO = authService.login(dto);
        return Result.success(loginVO);
    }
}
