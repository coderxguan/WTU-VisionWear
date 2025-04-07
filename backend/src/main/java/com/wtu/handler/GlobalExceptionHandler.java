package com.wtu.handler;


import com.aliyun.oss.ServiceException;
import com.wtu.result.Result;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Order(1)
public class GlobalExceptionHandler {

    // 捕获自定义的业务异常
    @ExceptionHandler(ServiceException.class)
    public Result<String> handleServiceException(ServiceException e) {
        return Result.error(e.getMessage());
    }

    // 捕获参数校验异常（比如 @Valid 失败）
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<String> handleValid(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldError().getDefaultMessage();
        return Result.error(msg);
    }

    // 捕获所有其他未知异常
    @ExceptionHandler(Exception.class)
    public Result<String> handleException(Exception e) {
        e.printStackTrace(); // 打印日志便于排查
        return Result.error(e.getMessage());
    }
}
