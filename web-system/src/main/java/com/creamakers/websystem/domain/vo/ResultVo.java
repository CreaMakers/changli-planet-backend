package com.creamakers.websystem.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.creamakers.websystem.constants.CommonConst.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResultVo<T> {
    String code;
    String msg;
    T data;

    public static <Void> ResultVo<Void> success() {
        return new ResultVo<Void>(RESULT_SUCCESS_CODE, RESULT_SUCCESS_MSG, null);
    }
    public static <T> ResultVo<T> success(T data) {
        return new ResultVo<T>(RESULT_SUCCESS_CODE, RESULT_SUCCESS_MSG, data);
    }

    public static <Void> ResultVo<Void> fail() {
        return new ResultVo<Void>(RESULT_FAILURE_CODE, RESULT_FAILURE_MSG, null);
    }

    public static <Void> ResultVo<Void> fail(String msg) {
        return new ResultVo<Void>(RESULT_FAILURE_CODE, msg, null);
    }

    public static <T> ResultVo<T> fail(T data) {
        return new ResultVo<T>(RESULT_FAILURE_CODE, RESULT_FAILURE_MSG, data);
    }

}
