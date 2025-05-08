package com.wtu.service;


import com.wtu.DTO.ImageToImageDTO;
import com.wtu.DTO.SketchToImageDTO;
import com.wtu.DTO.TextToImageDTO;
import com.wtu.VO.ImageToImageVO;
import com.wtu.VO.SketchToImageVO;
import com.wtu.VO.TextToImageVO;

import java.util.List;

public interface ImageService {
    /**
     * 文本生成图像 (Text-to-Image)
     * @param request 图像生成请求
     * @param userId 用户ID
     * @return 图像生成响应
     */
    TextToImageVO textToImage(TextToImageDTO request, Long userId) throws Exception;

    /**
     * 图像生成图像 (Image-to-Image)
     * @param request 以图生图请求
     * @param userId 用户ID
     * @return 图像生成响应
     */
    ImageToImageVO imageToImage(ImageToImageDTO request, Long userId) throws Exception;

    /**
     * 线稿生成图像 (Sketch-to-Image)
     * @param request 图像生成请求
     * @param userId 用户ID
     * @return 图像生成响应
     */
    SketchToImageVO sketchToImage(SketchToImageDTO request, Long userId) throws Exception;

    /**
     * 获取所有图像URL
     *
     * @param userId 用户ID
     * @return 图像URL列表
     */
    List<String> getAllImageUrls(Long userId);
}
