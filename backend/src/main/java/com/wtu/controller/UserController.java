package com.wtu.controller;

import com.wtu.DTO.ImageToImageDTO;
import com.wtu.DTO.TextToImageDTO;
import com.wtu.VO.ImageToImageVO;
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
    @Operation(summary = "调用API文生图功能")
    public Result<List<String>> textToImage(@RequestBody @Validated TextToImageDTO request,
                                            HttpServletRequest httpServletRequest) throws Exception {

        //从token 获取当前用户ID
        Long userId = UserContext.getCurrentUserId(httpServletRequest);
        log.info("当前用户 ID: {}", userId);

        log.info("收到图像生成请求: {}", request);

        TextToImageVO response = userService.textToImage(request, userId);

        List<String> ids = new ArrayList<>();

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

    @GetMapping(value = "/getAllImage")
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

    @PostMapping("/image-to-image")
    @Operation(summary = "以图生图")
    public Result<List<String>> imageToImage(@RequestBody ImageToImageDTO request,
                                             HttpServletRequest httpServletRequest) {
        try {
            //从token 获取当前用户ID
            Long userId = UserContext.getCurrentUserId(httpServletRequest);
            log.info("当前用户 ID: {}", userId);

            // 参数验证
            if (request.getSourceImageId() == null || request.getSourceImageId().isEmpty()) {
                return Result.error("源图像ID不能为空");
            }
            if (request.getPrompt() == null || request.getPrompt().isEmpty()) {
                return Result.error("提示词不能为空");
            }
            if (request.getImageStrength() < 0 || request.getImageStrength() > 1) {
                return Result.error("图像强度必须在0到1之间");
            }

            // 确保参数在合理范围内
            if (request.getCfgScale() < 0) {
                request.setCfgScale(7.0f); // 使用默认值
            }
            if (request.getSteps() < 10 || request.getSteps() > 150) {
                request.setSteps(30); // 使用默认步数
            }
            if (request.getSamples() < 1) {
                request.setSamples(1); // This 至少生成一张图片
            }

            log.info("开始处理以图生图请求: sourceImageId={}, prompt={}, imageStrength={}",
                    request.getSourceImageId(), request.getPrompt(), request.getImageStrength());

            ImageToImageVO response = userService.imageToImage(request, userId);

            List<String> ids = new ArrayList<>();

            //对返回结果中存放imageId的集合迭代循环
            for (ImageToImageVO.GeneratedImage image : response.getImages()) {
                String imageId = image.getImageId();
                ids.add(imageId);
            }

            log.info("以图生图处理成功: 生成了{}张图片", ids.size());
            return Result.success(ids);
        } catch (Exception e) {
            log.error("以图生图失败", e);
            // 提供更详细的错误信息
            String errorMessage = e.getMessage();
            if (errorMessage != null && errorMessage.contains("520")) {
                return Result.error("调用Stable Diffusion API时发生服务器错误(520)，请检查API配置和请求参数");
            }
            return Result.error("以图生图失败: " + e.getMessage());
        }
    }
}