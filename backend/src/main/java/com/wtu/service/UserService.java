package com.wtu.service;

import com.wtu.DTO.ImageToImageDTO;
import com.wtu.DTO.TextToImageDTO;
import com.wtu.VO.ImageToImageVO;
import com.wtu.VO.TextToImageVO;

// UserService.java
public interface UserService {
     // 原方法签名
     // TextToImageVO textToimage(TextToImageDTO request) throws Exception;

     // 修改后的方法签名（添加 userId）
     TextToImageVO textToImage(TextToImageDTO request, Long userId) throws Exception;

     ImageToImageVO imageToImage(ImageToImageDTO request, Long userId) throws Exception;
}