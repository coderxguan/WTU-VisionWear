package com.wtu.service;

import com.wtu.DTO.ImageToImageDTO;
import com.wtu.DTO.TextToImageDTO;
import com.wtu.VO.ImageToImageVO;
import com.wtu.VO.TextToImageVO;

public interface UserService {
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
}