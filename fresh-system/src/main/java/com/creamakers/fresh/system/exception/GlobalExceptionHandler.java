package com.creamakers.fresh.system.exception;

import cn.hutool.json.JSONUtil;

import com.creamakers.fresh.system.constants.CommonConst;
import com.creamakers.fresh.system.domain.vo.ResultVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.sql.SQLException;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    // 专门处理SQL异常
    @ExceptionHandler({SQLException.class})
    public ResponseEntity<String> handleSQLException(SQLException ex) {
        log.error("数据库异常:", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(JSONUtil.toJsonStr(ResultVo.fail(CommonConst.RESULT_FAILURE_CODE, CommonConst.DATABASE_OPERATION_ERROR)));
    }

    // 专门处理跟数据库交互出现的异常
    @ExceptionHandler({DataAccessException.class})
    public ResponseEntity<String> handleSQLException(DataAccessException ex) {
        log.error("数据库异常:", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(JSONUtil.toJsonStr(ResultVo.fail(CommonConst.RESULT_FAILURE_CODE, CommonConst.DATABASE_OPERATION_ERROR)));
    }
    @ExceptionHandler(UserServiceException.class)
    public ResponseEntity<String> handleRuntimeException(UserServiceException ex) {
        log.info("用户的异常错误是: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(JSONUtil.toJsonStr(ResultVo.fail(CommonConst.RESULT_FAILURE_CODE, ex.getMessage())));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
        log.info("不知道是什么错误是: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(JSONUtil.toJsonStr(ResultVo.fail(CommonConst.RESULT_FAILURE_CODE, ex.getMessage())));
    }
}
