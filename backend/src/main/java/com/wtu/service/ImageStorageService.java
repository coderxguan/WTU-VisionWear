package com.wtu.service;

import org.springframework.cache.annotation.Cacheable;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: gaochen
 * @Date: 2025/04/13/21:19
 * @Description: 图像缓存接口
 */
public interface ImageStorageService {


    String saveBase64Image(String base64Image);

    /**
     * 获取图像URL
     *
     * @param imageId 图像ID
     * @return 图像URL
     */
    public String getImageUrl(String imageId);
    String saveBase64Image(String base64Image, Long userId); // 添加 userId 参数
}
