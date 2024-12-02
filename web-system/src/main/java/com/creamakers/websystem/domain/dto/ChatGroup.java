package com.creamakers.websystem.domain.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("chat_group")
public class ChatGroup {
    @TableId(value = "group_id", type = IdType.AUTO)
    private Long groupId;

    // 群聊名称
    @TableField(value = "group_name")
    private String groupName;

    // 当前群聊人数
    @TableField(value = "member_count")
    private Integer memberCount;

    // 群聊人数限制
    @TableField(value = "member_limit")
    private Integer memberLimit;

    // 群聊类型 (1-学习, 2-生活, 3-工具, 4-问题反馈, 5-社团, 6-比赛)
    @TableField(value = "type")
    private Integer type;

    // 是否需要审核: 0-否, 1-是
    @TableField(value = "requires_approval")
    private Integer requiresApproval;

    // 是否删除: 0-未删除, 1-已删除
    @TableField(value = "is_deleted")
    private Integer isDeleted;
    /**
     * 是否封禁: 0-未封禁，1-已封禁
     */
    @TableField("is_banned")
    private Integer isBanned;

    // 群聊头像URL
    @TableField(value = "avatar_url")
    private String avatarUrl;

    // 群聊背景图片URL
    @TableField(value = "background_url")
    private String backgroundUrl;

    // 群聊更新时间
    @TableField(value = "update_time")
    private LocalDateTime updateTime;

    // 群聊创建时间
    @TableField(value = "create_time")
    private LocalDateTime createTime;

    // 群聊描述
    @TableField(value = "description")
    private String description;


}
