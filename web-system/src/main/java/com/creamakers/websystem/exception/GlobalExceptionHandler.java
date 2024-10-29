package com.creamakers.websystem.exception;

import cn.hutool.json.JSONUtil;
import com.creamakers.websystem.constants.CommonConst;
import com.creamakers.websystem.domain.vo.ResultVo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(JSONUtil.toJsonStr(ResultVo.fail(CommonConst.BAD_REQUEST_CODE, ex.getMessage())));
    }

}
