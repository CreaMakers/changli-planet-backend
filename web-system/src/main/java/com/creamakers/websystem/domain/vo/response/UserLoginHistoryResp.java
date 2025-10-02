package com.creamakers.websystem.domain.vo.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

/**
* 用户登录历史记录响应类
* @author zhaoge-code
* @since 2025-10-1
* */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginHistoryResp {
    /**
     * 用户ID
     */
    private Integer userId;
    /**
     * 学号
     */
    private String studentNumber;
    /**
     * 最后登录时间
     */
    private LocalDateTime lastLoginTime;
    /**
     * 是否删除: 0-未删除，1-已删除
     */
    private Integer isDeleted;
    /**
     * 用户动态数据描述
     */
    private String description;
}
