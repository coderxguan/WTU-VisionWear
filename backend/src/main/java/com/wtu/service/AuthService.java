package com.wtu.service;

import com.wtu.DTO.LoginDTO;
import com.wtu.DTO.RegisterDTO;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

public interface AuthService {

    void register(@Valid RegisterDTO dto);

    String login(@Valid LoginDTO dto);
}
