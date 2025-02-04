package com.hayaizo.chatsystem.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.hayaizo.chatsystem.dto.response.MessageExtra;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

/**
 * @description 群聊消息记录表
 * @author hayaizo
 * @date 2024-11-27
 */
@Data
@Builder
@AllArgsConstructor
@TableName(value = "chat_group_message",autoResultMap = true)
public class ChatGroupMessage {

    @TableId(type = IdType.AUTO)
    /**
     * 消息id
     */
    private Integer messageId;

    /**
     * 所属群聊id
     */
    private Integer groupId;

    /**
     * 发送者用户id
     */
    private Integer senderId;

    /**
     * 回复目标的用户id（回复某人的消息）
     */
    private Integer receiverId;

    /**
     * 聊天内容
     */
    private String messageContent;

    /**
     * 文件存储的url路径
     */
    private String fileUrl;

    /**
     * 消息类型: 1-正常文本消息， 2-撤回类型消息
     */
    private Integer messageType;

    /**
     * 文件类型: 1-图片， 2-文档， 3-视频， 4-音频， 5-其他
     */
    private Integer fileType;

    /**
     * 额外消息内容
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private MessageExtra extra;

    /**
     * 消息间隔数
     */
    private Integer gapCount;

    /**
     * 消息发送时间
     */
    private Date createTime;

    /**
     * 消息更新时间
     */
    private Date updateTime;

    /**
     * 是否删除: 0-未删除， 1-已删除
     */
    private Integer isDeleted;

    /**
     * 消息描述
     */
    private String description;

    public ChatGroupMessage() {}
}