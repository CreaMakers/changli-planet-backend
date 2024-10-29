package com.creamakers.usersystem.exception;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.creamakers.usersystem.consts.ErrorMessage;
import com.creamakers.usersystem.consts.HttpCode;
import com.creamakers.usersystem.dto.response.GeneralResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    /**
     * 漏掉的异常会走这里
     * @param e
     * @return
     */
    @ExceptionHandler(value = Throwable.class)
    public GeneralResponse<?> throwable(Throwable e) {
        log.error("system exception! The reason is: {}",e.getMessage(),e);
        return GeneralResponse.builder()
                .data("全局异常错误")
                .code(HttpCode.INTERNAL_SERVER_ERROR)
                .build();
    }

    @ExceptionHandler(value = JWTVerificationException.class)
    public GeneralResponse<?> jwtVerificationException(JWTVerificationException ex) {
        return GeneralResponse.builder()
                .code(HttpCode.UNAUTHORIZED)
                .msg(ErrorMessage.INVALID_CREDENTIALS)
                .build();
    }

}
