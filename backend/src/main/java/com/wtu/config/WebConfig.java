package com.wtu.config;

import com.wtu.interceptor.JwtTokenInterceptor;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 功能:
 * 作者:
 * 日期:2024/09/24 16:44
 */
@Configuration
@Slf4j
public class WebConfig implements WebMvcConfigurer {
    @Resource
    private JwtTokenInterceptor jwtTokenInterceptor;
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:9090") // 允许的源
                .allowedMethods("GET", "POST", "PUT", "DELETE") // 允许的方法
                .allowedHeaders("*") // 允许的请求头
                .allowCredentials(true); // 允许携带凭证
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        log.info("开始注册拦截器...");
        registry.addInterceptor(jwtTokenInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/api/auth/login")
                .excludePathPatterns("/api/auth/register")   //作用于所有请求,但免除login请求
                .excludePathPatterns(   // 排除 Swagger 和相关 API 文档路径
                        "/doc.html",             // Swagger UI
                        "/swagger-ui.html",      // Swagger UI
                        "/swagger-ui/**",        // Swagger UI 相关路径
                        "/v3/api-docs/**",       // OpenAPI 文档路径
                        "/swagger-resources/**", // Swagger 资源路径
                        "/webjars/**",           // Swagger 静态资源路径
                        "/swagger/**"            // Swagger 其他路径
                );

    }
}
