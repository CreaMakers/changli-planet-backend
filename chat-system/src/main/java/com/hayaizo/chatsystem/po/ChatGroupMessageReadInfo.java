package com.hayaizo.chatsystem.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @description 用户未读消息表
 * @author hayaizo
 * @date 2024-11-27
 */
@Data
@TableName("chat_group_message_read_info")
public class ChatGroupMessageReadInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    /**
    * 记录id
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
    * 消息id
    */
    private Integer messageId;

    /**
    * 消息是否已读: 0-未读， 1-已读
    */
    private Integer isRead;

    /**
    * 记录创建时间
    */
    private Date createTime;

    /**
    * 记录更新时间
    */
    private Date updateTime;

    public ChatGroupMessageReadInfo() {}
}