package com.wtu.service.impl;

import com.wtu.service.ImageStorageService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: gaochen
 * @Date: 2025/04/13/21:19
 * @Description: 图像缓存接口
 */
@Service
@Slf4j
public class ImageStorageServiceImpl implements ImageStorageService {

    @Value("${vision.api.storagePath}")
    private String storagePath;

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(Paths.get(storagePath));
            log.info("图像存储目录已创建: {}", storagePath);
        } catch (IOException e) {
            log.error("无法创建图像存储目录: {}", storagePath, e);
            throw new RuntimeException("无法创建图像存储目录", e);
        }
    }
    @Override
    public String saveBase64Image(String base64Image) {
        try {
            // 生成唯一文件名
            String imageId = UUID.randomUUID().toString();
            String fileName = imageId + ".png";
            Path filePath = Paths.get(storagePath, fileName);

            // 解码并保存图像
            byte[] imageBytes = Base64.getDecoder().decode(base64Image);
            FileUtils.writeByteArrayToFile(filePath.toFile(), imageBytes);

            log.debug("图像已保存: {}", filePath);
            return imageId;
        } catch (IOException e) {
            log.error("保存图像失败", e);
            throw new RuntimeException("保存图像失败", e);
        }
    }

    @Cacheable(value = "images", key = "#imageId")
    @Override
    public byte[] getImage(String imageId) {
        try {
            Path filePath = Paths.get(storagePath, imageId + ".png");
            File file = filePath.toFile();

            log.info("尝试读取图像文件: {}, 文件存在: {}", filePath, file.exists());

            if (!file.exists()) {
                log.warn("找不到图像: {}", imageId);
                return null;
            }

            return FileUtils.readFileToByteArray(file);
        } catch (IOException e) {
            log.error("读取图像失败: {}", imageId, e);
            throw new RuntimeException("读取图像失败", e);
        }
    }
}
