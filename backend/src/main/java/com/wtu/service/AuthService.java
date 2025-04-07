package com.wtu.service;

import com.wtu.DTO.LoginDTO;
import com.wtu.DTO.RegisterDTO;
import com.wtu.VO.LoginVO;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

public interface AuthService {

    void register(@Valid RegisterDTO dto);

    LoginVO login(@Valid LoginDTO dto);
}
