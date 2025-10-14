package com.hayaizo.chatsystem.dto.ws;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * Description: 群成员列表的成员信息
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMemberResp {
    @ApiModelProperty("uid")
    private Long uid;


    @ApiModelProperty("在线状态 1在线 2离线")
    private Integer activeStatus;

    /**
     * 角色ID
     */
    private Integer roleId;

    @ApiModelProperty("最后一次上下线时间")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "GMT+8")
    private Date lastOptTime;
}
