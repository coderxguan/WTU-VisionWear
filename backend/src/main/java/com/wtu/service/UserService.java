package com.wtu.service;

import com.wtu.DTO.ImageGenerationRequest;
import com.wtu.DTO.ImageGenerationResponse;
import com.wtu.DTO.MidjourneyTextDTO;
import org.springframework.http.HttpHeaders;

import java.io.IOException;

public interface UserService {
     ImageGenerationResponse textToimage(ImageGenerationRequest request) throws Exception;
}
