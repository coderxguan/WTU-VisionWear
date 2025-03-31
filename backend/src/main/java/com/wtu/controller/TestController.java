package com.wtu.controller;

import com.wtu.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@Tag(name = "测试接口")
public class TestController {



     @GetMapping("/test")
     @Operation(description = "测试接口")
     public Result test() {
         return Result.success("test");
     }

}
