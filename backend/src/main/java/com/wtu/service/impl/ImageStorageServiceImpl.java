package com.wtu.service.impl;

import com.wtu.entity.Image;
import com.wtu.mapper.ImageMapper;
import com.wtu.service.ImageStorageService;
import com.wtu.utils.AliOssUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.net.URL;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.*;

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
    public String saveImageFromUrl(String imageUrl, Long userId) {
        try {
            String imageId = UUID.randomUUID().toString();
            String objectName = imageId + ".png";

            // 1. 从URL下载图片数据
            byte[] imageBytes;
            try (InputStream in = new URL(imageUrl).openStream()) {
                imageBytes = in.readAllBytes();
            }

            // 2. 上传到OSS
            String ossImageUrl = aliOssUtil.upload(imageBytes, objectName);

            // 3. 插入数据库记录
            Image image = Image.builder()
                    .imageId(imageId)
                    .userId(userId)
                    .imageUrl(ossImageUrl)
                    .createTime(LocalDateTime.now())
                    .status(0)
                    .build();
            imageMapper.insert(image);

            log.info("URL图片保存成功: imageId={}, userId={}, originalUrl={}", imageId, userId, imageUrl);
            return imageId;
        } catch (IOException e) {
            log.error("从URL获取图片失败: {}", imageUrl, e);
            throw new RuntimeException("从URL获取图片失败", e);
        } catch (Exception e) {
            log.error("保存URL图片到OSS失败", e);
            throw new RuntimeException("保存URL图片到OSS失败", e);
        }
    }
/*
    @Override
    public String saveImageFromUrl(String imageUrl, Long userId) {
        // 1. 下载图片
        // 2. 存储图片（本地、OSS、COS等）
        // 3. 生成图片ID并返回

        // 示例：下载图片到本地（仅供参考，实际可对接云存储）
        try (InputStream in = new URL(imageUrl).openStream()) {
            String fileName = UUID.randomUUID() + ".jpg";
            Files.copy(in, Paths.get("E:\\" + fileName));
            // 存数据库、返回图片ID
            // 这里只返回文件名作为ID示例
            return fileName;
        } catch (IOException e) {
            throw new RuntimeException("图片下载失败: " + e.getMessage(), e);
        }
    }*/

    @Override
    public String getImageUrl(String imageId) {
        // 直接构建并返回URL
        String objectName = imageId + ".png";
        return aliOssUtil.getAccessUrl(objectName);
    }
}