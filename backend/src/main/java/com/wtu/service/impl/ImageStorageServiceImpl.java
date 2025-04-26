package com.wtu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wtu.entity.Image;
import com.wtu.mapper.ImageMapper;
import com.wtu.service.ImageStorageService;
import com.wtu.utils.AliOssUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageStorageServiceImpl implements ImageStorageService {

    // IOC 注入
    private final AliOssUtil aliOssUtil;
    private final ImageMapper imageMapper;

    @Override
    public String saveBase64Image(String base64Image, Long userId) {
        try {
            String imageId = UUID.randomUUID().toString();
            String objectName = imageId + ".png";

            // 解码并上传到OSS
            byte[] imageBytes = Base64.getDecoder().decode(base64Image);
            String imageUrl = aliOssUtil.upload(imageBytes, objectName);

            // 插入数据库记录
            Image image = Image.builder()
                    .imageId(imageId)
                    .userId(userId)
                    .imageUrl(imageUrl)
                    .createTime(LocalDateTime.now())
                    .status(0)
                    .build();
            imageMapper.insert(image);

            log.info("图片保存成功: imageId={}, userId={}", imageId, userId);
            return imageId;

        } catch (Exception e) {
            log.error("保存图像到OSS失败", e);
            throw new RuntimeException("保存图像到OSS失败", e);
        }
    }

    @Override
    public String getImageUrl(String imageId) {
        // 直接构建并返回URL
        String objectName = imageId + ".png";
        return aliOssUtil.getAccessUrl(objectName);
    }
}