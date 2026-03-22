package com.creamakers.toolsystem.exception;

import com.creamakers.toolsystem.dto.response.GeneralResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 * 统一处理业务异常，返回标准化的错误响应
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    /**
     * 处理BusinessException业务异常
     * 
     * @param e 业务异常
     * @return 统一错误响应
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<GeneralResponse<Void>> handleBusinessException(BusinessException e) {
        log.error("业务异常：code={}, message={}", e.getCode(), e.getMessage(), e);
        return ResponseEntity.ok(GeneralResponse.fail(String.valueOf(e.getCode()), e.getMessage()));
    }
    
    /**
     * 处理其他未知异常
     * 
     * @param e 异常
     * @return 统一错误响应
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<GeneralResponse<Void>> handleException(Exception e) {
        log.error("系统异常", e);
        return ResponseEntity.ok(GeneralResponse.fail("系统异常，请联系管理员"));
    }
}