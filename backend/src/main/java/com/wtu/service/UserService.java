package com.wtu.service;

import com.wtu.DTO.TextToImageDTO;
import com.wtu.VO.TextToImageVO;

public interface UserService {
     TextToImageVO textToimage(TextToImageDTO request) throws Exception;
}
