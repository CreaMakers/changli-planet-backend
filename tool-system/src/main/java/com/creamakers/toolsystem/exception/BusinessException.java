package com.creamakers.toolsystem.exception;

import com.creamakers.toolsystem.consts.HttpCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 业务异常类
 * 用于业务逻辑中抛出的可预期异常
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BusinessException extends RuntimeException {
    
    /**
     * 错误码
     */
    private String code;
    
    /**
     * 错误消息
     */
    private String message;
    
    /**
     * 构造方法 - 仅消息
     * 
     * @param message 错误消息
     */
    public BusinessException(String message) {
        super(message);
        this.message = message;
        this.code = HttpCode.BAD_REQUEST; // 默认错误码
    }

}