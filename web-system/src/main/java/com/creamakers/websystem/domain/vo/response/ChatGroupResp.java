package com.creamakers.websystem.domain.vo.response;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatGroupResp {
    private Long groupId;

    // 群聊名称
    private String groupName;

    // 当前群聊人数
    private Integer memberCount;

    // 群聊人数限制
    private Integer memberLimit;

    // 群聊类型 (1-学习, 2-生活, 3-工具, 4-问题反馈, 5-社团, 6-比赛)
    private Integer type;

    // 是否需要审核: 0-否, 1-是
    private Boolean requiresApproval;


    // 群聊头像URL
    private String avatarUrl;

    // 群聊背景图片URL
    private String backgroundUrl;

    // 群聊更新时间
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "GMT+8")
    private LocalDateTime updateTime;

    // 群聊创建时间
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "GMT+8")
    private LocalDateTime createTime;

    // 群聊描述
    private String description;
    /**
     * 是否封禁: 0-未封禁，1-已封禁
     */
    private Integer isBanned;

    // 是否删除: 0-未删除, 1-已删除
    private Boolean isDeleted;

}
