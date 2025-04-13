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

    @Cacheable(value = "images", key = "#imageId")
    byte[] getImage(String imageId);
}
