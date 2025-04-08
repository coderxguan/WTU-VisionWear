package com.wtu.controller;

import com.wtu.DTO.MidjourneyTextDTO;
import com.wtu.result.Result;
import com.wtu.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/user")
@Tag(name = "用户模块")
public class UserController {
    @Resource
    private UserService userService;
    @PostMapping("/Image")
    @Operation(summary = "文生图功能")
    public Result<String> textToImage(@RequestHeader HttpHeaders midjourneyHeader, @RequestBody MidjourneyTextDTO midjourneyTextDTO) throws IOException {

        String string = userService.textToimage(midjourneyHeader, midjourneyTextDTO);

        return Result.success(string);
    }
}
