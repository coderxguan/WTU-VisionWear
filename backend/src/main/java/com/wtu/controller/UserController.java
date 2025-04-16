package com.wtu.controller;

import com.wtu.DTO.TextToImageDTO;
import com.wtu.VO.TextToImageVO;
import com.wtu.result.Result;
import com.wtu.service.ImageStorageService;
import com.wtu.service.UserService;
import com.wtu.utils.UserContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/user")
@Tag(name = "用户模块")
@Slf4j

public class UserController {
    @Resource
    private UserService userService;
    @Resource
    private ImageStorageService imageStorageService;
    @PostMapping("/image/generate")
    @Operation(summary = "调用API文生图功能")//返回结果为base64编码集
    public Result<List<String>> textToImage(@RequestBody @Validated TextToImageDTO request,
                                            HttpServletRequest httpServletRequest) throws Exception {

        //从token 获取当前用户ID
        Long userId = UserContext.getCurrentUserId(httpServletRequest);
        log.info("当前用户 ID: {}", userId);

        log.info("收到图像生成请求: {}", request);

        TextToImageVO response = userService.textToImage(request, userId);

        List<String > ids=new ArrayList<>();

        //对返回结果中存放imageId的集合迭代循环
        for (TextToImageVO.GeneratedImage image : response.getImages()) {
            String imageId = image.getImageId();
            ids.add(imageId);
        }

        return Result.success(ids);
    }

    @GetMapping(value = "/images/{imageId}")
    @Operation(summary = "通过imageId获取图片URL")
    public Result<String> getImage(@PathVariable String imageId) {
        log.info("获取图像URL: {}", imageId);

        String imageUrl = imageStorageService.getImageUrl(imageId);

        if (imageUrl == null) {
            return Result.error("找不到图片!");
        }

        return Result.success(imageUrl);
    }

    @GetMapping(value="/getAllImage")
    @Operation(summary = "获取用户生成的所有图片URL")
    public Result<List<String>> getAllImage(HttpServletRequest httpServletRequest) {
        //从token 获取当前用户ID
        Long userId = UserContext.getCurrentUserId(httpServletRequest);

        List<String> imageUrls = imageStorageService.getAllImageUrls(userId);

        if (imageUrls == null || imageUrls.isEmpty()) {
            return Result.error("没有找到图片!");
        }

        return Result.success(imageUrls);
    }

}
