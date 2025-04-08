package com.wtu.service.impl;

import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wtu.DTO.LoginDTO;
import com.wtu.DTO.RegisterDTO;
import com.wtu.VO.LoginVO;
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
    public String register(RegisterDTO dto) {
        // 用户名重复判断
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<User>();
        wrapper.eq(User::getUserName, dto.getUsername());
        if (userMapper.selectOne(wrapper) != null) {
            return "用户名已存在";
        }

        // 邮箱重复判断
        LambdaQueryWrapper<User> emailWrapper = new LambdaQueryWrapper<>();
        emailWrapper.eq(User::getEmail, dto.getEmail());
        if (userMapper.selectOne(emailWrapper) != null) {
            return "邮箱已注册";
        }

        // 保存用户
        User user = new User();
        user.setUserName(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setStatus(0);
        // 密码加密
        user.setPassWord(DigestUtil.md5Hex(dto.getPassword()));

        userMapper.insert(user);
        return "注册成功";
    }


    @Override
    public LoginVO login(LoginDTO dto) {
        // 1. 查用户
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getUserName, dto.getUsername())
        );

        // 2. 判空 + 加密后密码比对
        String password = DigestUtil.md5Hex(dto.getPassword());
        if (user == null || !user.getPassWord().equals(password)) {
            throw new ServiceException("用户名或密码错误!");
        }

        // 3. 判断状态
        if (user.getStatus() != 0) {
            throw new ServiceException("账号状态异常，请联系管理员！");
        }

        // 4. 更新最后登录时间
        user.setLastLogin(LocalDateTime.now());
        userMapper.updateById(user);

        // 5. 生成 token
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getUserId());
        claims.put("userName", user.getUserName());

        String token = JwtUtil.createJwt(jwtProperties.getSecretKey(), jwtProperties.getTtl(), claims);

        // 6. 封装返回对象
        LoginVO loginVO = new LoginVO();
        loginVO.setUserId(user.getUserId());
        loginVO.setUserName(user.getUserName());
        loginVO.setToken(token);

        return loginVO;
    }

}
