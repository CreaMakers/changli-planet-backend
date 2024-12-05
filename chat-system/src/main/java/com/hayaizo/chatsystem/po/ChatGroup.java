package com.hayaizo.chatsystem.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @description 群聊表
 * @author hayaizo
 * @date 2024-11-27
 */
@Data
public class ChatGroup implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    /**
    * 群聊id
    */
    private Integer groupId;

    /**
    * 群聊名称
    */
    private String groupName;

    /**
    * 当前群聊人数
    */
    private Integer memberCount;

    /**
    * 群聊人数限制
    */
    private Integer memberLimit;

    /**
    * 群聊类型: 1-学习， 2-生活， 3-工具， 4-问题反馈， 5-社团， 6-比赛
    */
    private Integer type;

    /**
    * 是否需要审核: 0-否， 1-是
    */
    private Integer requiresApproval;

    /**
    * 是否删除: 0-未删除， 1-已删除
    */
    private Integer isDeleted;

    /**
    * 是否封禁: 0-未封禁，1-已封禁
    */
    private Integer isBanned;

    /**
    * 群聊头像url
    */
    private String avatarUrl;

    /**
    * 群聊背景图片url
    */
    private String backgroundUrl;

    /**
    * 群聊更新时间
    */
    private Date updateTime;

    /**
    * 群聊创建时间
    */
    private Date createTime;

    /**
    * 群聊描述
    */
    private String description;

    public ChatGroup() {}
}