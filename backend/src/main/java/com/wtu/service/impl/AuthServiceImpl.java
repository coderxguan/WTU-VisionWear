package com.wtu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wtu.DTO.LoginDTO;
import com.wtu.DTO.RegisterDTO;
import com.wtu.entity.User;
import com.wtu.exception.ServiceException;
import com.wtu.mapper.UserMapper;
import com.wtu.properties.JwtProperties;
import com.wtu.service.AuthService;
import com.wtu.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtProperties jwtProperties;

    @Override
    public void register(RegisterDTO dto) {

    }


    @Override
    public String login(LoginDTO dto) {
        // 1. 查用户
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getUserName, dto.getUsername())
        );

        // 2. 判空 + 密码比对
        if (user == null || !user.getPassWord().equals(dto.getPassword())) {
            throw new ServiceException("用户名或密码错误!");
        }

        // 3. 判断状态
        if (user.getStatus() != 0) {
            throw new ServiceException("账号状态异常，请联系管理员！");
        }

//        // 4. 更新最后登录时间
//        user.setLastLogin(LocalDateTime.now());
//        userMapper.updateById(user);

        // 5. 生成 token
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getUserId());
        claims.put("userName", user.getUserName());

        return JwtUtil.createJwt(jwtProperties.getSecretKey(), jwtProperties.getTtl(), claims);
    }

}
