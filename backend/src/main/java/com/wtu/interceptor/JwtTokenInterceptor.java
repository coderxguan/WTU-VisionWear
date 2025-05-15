package com.wtu.interceptor;

import com.wtu.properties.JwtProperties;
import com.wtu.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtTokenInterceptor implements HandlerInterceptor {

    private final JwtProperties jwtProperties;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {    // 获取请求路径
        //检测是否为动态方法，如果不是，直接放行
        if(!(handler instanceof HandlerMethod)){
            return true;
        }

        try {
            //检查token
            log.info("检验开始");
            String token = request.getHeader(jwtProperties.getTokenName());
            Claims claims = JwtUtil.parseJwt(jwtProperties.getSecretKey(), token);
            log.info("检验token: {}",token);
            log.info("检验封装数据:{}",claims.toString());

            Object userId = claims.get("userId");
            request.setAttribute("userId", userId); // 存入作用域
        } catch (Exception e) {
            //token不存在相应claims，则报错,响应码变成401
            response.setStatus(401);
            return false;
        }

        return true;
    }
}
