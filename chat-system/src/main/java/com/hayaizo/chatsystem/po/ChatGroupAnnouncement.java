package com.hayaizo.chatsystem.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @description 公告表
 * @author hayaizo
 * @date 2024-11-27
 */
@Data
@TableName("chat_group_announcement")
public class ChatGroupAnnouncement implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    /**
    * 公告id
    */
    private Integer announcementId;

    /**
    * 所属群聊id
    */
    private Integer groupId;

    /**
    * 发布用户id
    */
    private Integer userId;

    /**
    * 公告标题
    */
    private String title;

    /**
    * 公告内容
    */
    private String content;

    /**
    * 是否置顶公告: 1-置顶， 0-不置顶
    */
    private Integer isPinned;

    /**
    * 公告创建时间
    */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "GMT+8")
    private Date createTime;

    /**
    * 公告更新时间
    */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "GMT+8")
    private Date updateTime;

    /**
    * 是否删除: 0-未删除， 1-已删除
    */
    private Integer isDeleted;

    /**
    * 公告描述
    */
    private String description;

    public ChatGroupAnnouncement() {}
}