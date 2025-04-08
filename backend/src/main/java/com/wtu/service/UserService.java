package com.wtu.service;

import com.wtu.DTO.MidjourneyTextDTO;
import org.springframework.http.HttpHeaders;

import java.io.IOException;

public interface UserService {

     String textToimage(HttpHeaders header, MidjourneyTextDTO midjourneyTextDTO) throws IOException;
}
