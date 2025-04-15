package com.wtu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wtu.entity.Image;
import com.wtu.mapper.ImageMapper;
import com.wtu.service.ImageStorageService;
import com.wtu.utils.AliOssUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class ImageStorageServiceImpl implements ImageStorageService {


    @Autowired
    private AliOssUtil aliOssUtil;

    @Autowired
    private ImageMapper imageMapper;  // 确保已注入

    // 缓存imageId与OSS URL的映射关系
    private final Map<String, String> imageUrlCache = new ConcurrentHashMap<>();

    /**
     * 保存Base64编码的图像到阿里云OSS
     * @param base64Image Base64编码的图像数据
     * @return 生成的图像ID
     */
    @Override
    public String saveBase64Image(String base64Image, Long userId) {
        try {
            // 生成唯一文件名
            String imageId = UUID.randomUUID().toString();
            String objectName = imageId + ".png";

            // 解码Base64图像
            byte[] imageBytes = Base64.getDecoder().decode(base64Image);

            // 上传到OSS并获取访问URL
            String imageUrl = aliOssUtil.upload(imageBytes, objectName);

            // 缓存图像URL
            imageUrlCache.put(imageId, imageUrl);

            // 插入数据库记录（包含 userId）
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
    public List<String> getAllImageUrls(Long userId) {
        List<String> imageUrls = new ArrayList<>();
        // 从数据库中查询用户的所有图像记录
        LambdaQueryWrapper<Image> wrapper = new LambdaQueryWrapper<>();
        // 添加查询条件 最新的图像在前
        wrapper.eq(Image::getUserId, userId)
                .orderByDesc(Image::getCreateTime);
        List<Image> images = imageMapper.selectList(wrapper);
        log.info("查询到的图像记录: {}", images);
        // 遍历图像记录，获取图像URL
        for (Image image : images) {
            String imageUrl = image.getImageUrl();
            if (imageUrl != null) {
                imageUrls.add(imageUrl);
            }
        }
        log.info("用户 {} 的所有图像URL: {}", userId, imageUrls);

        return imageUrls;
    }

    /**
     * 获取图像URL
     * @param imageId 图像ID
     * @return 图像的OSS访问URL
     */
    public String getImageUrl(String imageId) {
        // 先从缓存获取
        String cachedUrl = imageUrlCache.get(imageId);
        if (cachedUrl != null) {
            return cachedUrl;
        }

        // 如果缓存中没有，则构建URL
        // 文件访问路径规则 https://BucketName.Endpoint/ObjectName
        String objectName = imageId + ".png";
        String imageUrl = aliOssUtil.getAccessUrl(objectName);

        // 加入缓存
        imageUrlCache.put(imageId, imageUrl);

        return imageUrl;
    }
}