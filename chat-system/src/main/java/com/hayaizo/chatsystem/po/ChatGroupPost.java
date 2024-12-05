package com.hayaizo.chatsystem.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @description 知识区帖子表
 * @author hayaizo
 * @date 2024-11-27
 */
@Data
public class ChatGroupPost implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    /**
    * 帖子id
    */
    private Integer postId;

    /**
    * 所属群聊id
    */
    private Integer groupId;

    /**
    * 发布用户id
    */
    private Integer userId;

    /**
    * 帖子标题
    */
    private String title;

    /**
    * 帖子内容
    */
    private String content;

    /**
    * 帖子类别: 0-general， 1-tutorial， 2-article， 3-experience
    */
    private Integer category;

    /**
    * 是否加精: 0-否， 1-是
    */
    private Integer isPinned;

    /**
    * 浏览人数
    */
    private Integer viewCount;

    /**
    * 被投币量
    */
    private Integer coinCount;

    /**
    * 帖子创建时间
    */
    private Date createTime;

    /**
    * 帖子更新时间
    */
    private Date updateTime;

    /**
    * 是否删除: 0-未删除， 1-已删除
    */
    private Integer isDeleted;

    /**
    * 帖子描述
    */
    private String description;

    public ChatGroupPost() {}
}