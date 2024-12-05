package com.hayaizo.chatsystem.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @description 用户-群聊关联表
 * @date 2024-11-27
 */
@Data
@TableName("chat_group_user")
public class ChatGroupUser {

    @TableId(type = IdType.AUTO)
    /**
    * id
    */
    private Integer id;

    /**
    * 群聊id
    */
    private Integer groupId;

    /**
    * 用户id
    */
    private Integer userId;

    /**
    * 用户加入群聊时间
    */
    private Date joinedTime;

    /**
    * 用户角色: 0-普通成员， 1-管理员， 2-群主
    */
    private Integer role;

    /**
    * 是否删除: 0-未删除， 1-已删除
    */
    private Integer isDeleted;

    /**
    * 是否在该群聊被禁言: 0-未禁言， 1-已禁言
    */
    private Integer isMuted;

    /**
    * 禁言开始时间
    */
    private Date muteStartTime;

    /**
    * 禁言持续时间（分钟）
    */
    private Integer muteDuration;

    /**
    * 记录创建时间
    */
    private Date createTime;

    /**
    * 记录更新时间
    */
    private Date updateTime;

    /**
    * 用户入群状态: 0-审核中， 1-已入群， 2-审核拒绝
    */
    private Integer joinStatus;

    /**
    * 用户入群申请信息
    */
    private String joinRequestInfo;

    /**
    * 用户-群聊关联表描述
    */
    private String description;

    public ChatGroupUser() {}
}