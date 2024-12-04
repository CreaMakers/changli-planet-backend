package com.creamakers.websystem.domain.dto;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("chat_group_user")
public class GroupUser {
    /**
     * 用户-群聊关联ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 群聊ID
     */
    @TableField("group_id")
    private Long groupId;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 用户加入群聊时间
     */
    @TableField(value = "joined_time")
    private LocalDateTime joinedTime;

    /**
     * 用户角色: 0-普通成员, 1-管理员, 2-群主
     */
    @TableField("role")
    private Integer role;

    /**
     * 是否删除: 0-未删除, 1-已删除
     */
    @TableLogic(value = "0", delval = "1")
    @TableField("is_deleted")
    private Integer isDeleted;

    /**
     * 是否在该群聊被禁言: 0-未禁言, 1-已禁言
     */
    @TableField("is_muted")
    private Integer isMuted;

    /**
     * 禁言开始时间
     */
    @TableField("mute_start_time")
    private LocalDateTime muteStartTime;

    /**
     * 禁言持续时间（分钟）
     */
    @TableField("mute_duration")
    private Integer muteDuration;

    /**
     * 记录创建时间
     */
    @TableField(value = "create_time")
    private LocalDateTime createTime;

    /**
     * 记录更新时间
     */
    @TableField(value = "update_time")
    private LocalDateTime updateTime;

    /**
     * 用户入群状态: 0-审核中, 1-已入群, 2-审核拒绝
     */
    @TableField("join_status")
    private Integer joinStatus;

    /**
     * 用户入群申请信息
     */
    @TableField("join_request_info")
    private String joinRequestInfo;

    /**
     * 用户-群聊关联表描述
     */
    @TableField("description")
    private String description;
}
