package com.creamakers.websystem.domain.vo.response;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ViolationRecordRespCount {

    private Integer count;

    private Object data;
}
