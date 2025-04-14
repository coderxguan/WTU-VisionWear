package com.wtu.controller;

import com.wtu.DTO.TextToImageDTO;
import com.wtu.VO.TextToImageVO;
import com.wtu.result.Result;
import com.wtu.service.ImageStorageService;
import com.wtu.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
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
    public Result<List<String>> textToImage(@RequestBody @Validated TextToImageDTO request) throws Exception {

        log.info("收到图像生成请求: {}", request);

        TextToImageVO response = userService.textToimage(request);

        List<String > ids=new ArrayList<>();

        //对返回结果中存放imageid的集合迭代循环
        for (TextToImageVO.GeneratedImage image : response.getImages()) {
            String imageId = image.getImageId();
            ids.add(imageId);
        }

        return Result.success(ids);
    }

    @GetMapping(value = "/images/{imageId}")
    @Operation(summary = "通过imageid获取图片URL")
    public Result<String> getImage(@PathVariable String imageId) {
        log.info("获取图像URL: {}", imageId);

        String imageUrl = imageStorageService.getImageUrl(imageId);

        if (imageUrl == null) {
            return Result.error("找不到图片!");
        }

        return Result.success(imageUrl);
    }


}
