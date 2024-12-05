package com.hayaizo.chatsystem.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
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
     * 文件类型: 1-图片， 2-文档， 3-视频， 4-音频， 5-其他
     */
    private Integer fileType;

    /**
     * 额外消息内容
     */
    private String extra;

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