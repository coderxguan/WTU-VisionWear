package com.wtu.controller;


import com.wtu.DTO.ImageToImageDTO;
import com.wtu.DTO.SketchToImageDTO;
import com.wtu.DTO.TextToImageDTO;
import com.wtu.VO.ImageToImageVO;
import com.wtu.VO.SketchToImageVO;
import com.wtu.VO.TextToImageVO;
import com.wtu.result.Result;
import com.wtu.service.ImageService;
import com.wtu.service.ImageStorageService;
import com.wtu.utils.AliOssUtil;
import com.wtu.utils.UserContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/image")
@Tag(name = "图像生成模块")
@Slf4j
@RequiredArgsConstructor
public class ImageController {

    // IOC 注入
    private final ImageService imageService;
    private final ImageStorageService imageStorageService;
    private final AliOssUtil aliOssUtil;

    @PostMapping("/text-to-image")
    @Operation(summary = "文生图功能")
    public Result<List<String>> textToImage(@RequestBody @Validated TextToImageDTO request,
                                            HttpServletRequest httpServletRequest) throws Exception {

        //从token 获取当前用户ID
        Long userId = UserContext.getCurrentUserId(httpServletRequest);
        log.info("收到图像生成请求: {}", request);

        // 调用用户服务的textToImage方法生成图像
        TextToImageVO response = imageService.textToImage(request, userId);
        List<String> ids = new ArrayList<>();

        //对返回结果中存放imageId的集合迭代循环
        for (TextToImageVO.GeneratedImage image : response.getImages()) {
            String imageId = image.getImageId();
            ids.add(imageId);
        }

        return Result.success(ids);
    }

    @GetMapping("/get-image/{imageId}")
    @Operation(summary = "通过imageId获取图片URL")
    public Result<String> getImage(@PathVariable String imageId) {
        log.info("获取图像id: {}", imageId);
        String imageUrl = imageStorageService.getImageUrl(imageId);
        log.info("获取图像URL: {}", imageUrl);

        if (imageUrl == null) {
            return Result.error("找不到图片!");
        }

        return Result.success(imageUrl);
    }

    @PostMapping("/image-to-image")
    @Operation(summary = "图生图功能")
    public Result<ImageToImageVO> imageToImage(@RequestBody ImageToImageDTO request,
                                             HttpServletRequest httpServletRequest) {
        try {
            Long userId = UserContext.getCurrentUserId(httpServletRequest);
            log.info("当前用户 ID: {}", userId);

            // 参数校验
            if (request.getSourceImageUrl() == null || request.getSourceImageUrl().isEmpty()) {
                return Result.error("源图像url不能为空");
            }
            if (request.getPrompt() == null || request.getPrompt().isEmpty()) {
                return Result.error("提示词不能为空");
            }
            if (request.getImageStrength() < 0 || request.getImageStrength() > 1) {
                return Result.error("图像强度必须在0到1之间");
            }

            // 设置默认值
            request.setCfgScale(request.getCfgScale() < 0 ? 7.0f : request.getCfgScale());
            request.setSteps(request.getSteps() < 10 || request.getSteps() > 150 ? 30 : request.getSteps());
            request.setSamples(Math.max(request.getSamples(), 1));

            log.info("开始处理以图生图请求: {}", request);

            // 调用用户服务的imageToImage方法生成图像
            ImageToImageVO response = imageService.imageToImage(request, userId);

            log.info("以图生图处理成功: {}", response);
            return Result.success(response);
        } catch (Exception e) {
            log.error("以图生图失败", e);
            // 错误信息处理保持不变
            String errorMessage = e.getMessage();
            if (errorMessage != null && errorMessage.contains("520")) {
                return Result.error("调用Stable Diffusion API时发生服务器错误(520)，请检查API配置和请求参数");
            }
            return Result.error("以图生图失败: " + e.getMessage());
        }
    }
    @PostMapping("/sketch-to-image")
    @Operation(summary = "线稿生图功能")
    public Result<List<String>> sketchToImage(@RequestBody @Validated SketchToImageDTO request,
                                              HttpServletRequest httpServletRequest) {
        try {
            Long userId = UserContext.getCurrentUserId(httpServletRequest);
            SketchToImageVO response = imageService.sketchToImage(request, userId);
            List<String> ids = response.getImages().stream()
                    .map(SketchToImageVO.GeneratedImage::getImageId)
                    .collect(Collectors.toList());
            return Result.success(ids);
        } catch (Exception e) {
            return Result.error("线稿生图失败: " + e.getMessage());
        }
    }

    @PostMapping("/upload")
    @Operation(description = "文件上传")
    public Result<String> upload(MultipartFile file){
        log.info("文件上传开始: 文件名={}, 大小={}bytes",
                file.getOriginalFilename(), file.getSize());

        try {
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null) {
                log.error("文件名为空");
                return Result.error("文件名为空");
            }

            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            // 构建新的文件名称
            String objectName = UUID.randomUUID().toString() + extension;
            log.info("生成的对象名称: {}", objectName);

            String filePath = aliOssUtil.upload(file.getBytes(), objectName);
            log.info("上传成功，返回的文件路径: {}", filePath);

            return Result.success(filePath);
        } catch (IOException e) {
            log.error("文件上传失败, 错误: {}", e.getMessage(), e);
            return Result.error("文件读取失败: " + e.getMessage());
        } catch (Exception e) {
            log.error("文件上传过程发生未知错误", e);
            return Result.error("上传过程发生错误: " + e.getMessage());
        }
    }

}
