package com.wtu.controller;

import com.wtu.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@Tag(name = "测试接口")
@Slf4j
public class TestController {



     @GetMapping("/test")
     @Operation(description = "测试接口")
     public Result test() {
            log.info("测试接口正在运行");
            log.error("测试接口正在运行");
            log.warn("测试接口正在运行");
         return Result.success("test");
     }


}
