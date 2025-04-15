package com.wtu.service.impl;

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
import java.util.Base64;
import java.util.Map;
import java.util.UUID;
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
    public String saveBase64Image(String base64Image) {
        return "";
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