package com.wtu.controller;

import com.wtu.DTO.ImageGenerationRequest;
import com.wtu.DTO.ImageGenerationResponse;
import com.wtu.result.Result;
import com.wtu.service.ImageStorageService;
import com.wtu.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/user")
@Tag(name = "用户模块")
@Slf4j

public class UserController {
    @Resource
    private UserService userService;
    @Resource
    private ImageStorageService imageStorageService;
    @PostMapping("/Image/generate")
    @Operation(summary = "调用API文生图功能")//返回结果为base64编码集
    public Result<List<String>> textToImage(@RequestBody @Validated ImageGenerationRequest request) throws Exception {

        log.info("收到图像生成请求: {}", request);

        ImageGenerationResponse response = userService.textToimage(request);

        List<String > ids=new ArrayList<>();

        //对返回结果中存放imageid的集合迭代循环
        for (ImageGenerationResponse.GeneratedImage image : response.getImages()) {
            String imageId = image.getImageId();
            ids.add(imageId);
        }


        return Result.success(ids);
    }
    @GetMapping(value = "/images/{imageId}")
    @Operation(summary = "通过imageid拿到png图片")
    public Result<byte[]> getImage(@PathVariable String imageId) {
        log.info("获取图像: {}", imageId);
        byte[] imageData = imageStorageService.getImage(imageId);

        if (imageData == null) {
            return Result.error("找不到文件！");
        }

        return Result.success(imageData);
    }



}
