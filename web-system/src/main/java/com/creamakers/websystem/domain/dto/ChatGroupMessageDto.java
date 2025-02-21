package com.creamakers.websystem.domain.dto;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("chat_group_message")
public class ChatGroupMessageDto {


    /**
     * 消息ID
     */
    @TableId(value = "message_id", type = IdType.AUTO)
    private Integer messageId;

    /**
     * 所属群聊ID
     */
    @TableField("group_id")
    private Integer groupId;

    /**
     * 发送者用户ID
     */
    @TableField("sender_id")
    private Integer senderId;

    /**
     * 回复目标的用户ID（回复某人的消息）
     */
    @TableField("receiver_id")
    private Integer receiverId;

    /**
     * 聊天内容
     */
    @TableField("message_content")
    private String messageContent;

    /**
     * 文件存储的URL路径
     */
    @TableField("file_url")
    private String fileUrl;

    /**
     * 文件类型: 1-图片, 2-文档, 3-视频, 4-音频, 5-其他
     */
    @TableField("file_type")
    private Integer fileType;

    /**
     * 额外消息内容，JSON格式
     */
    @TableField("extra")
    private String extra;

    /**
     * 消息间隔数
     */
    @TableField("gap_count")
    private Integer gapCount;

    /**
     * 消息发送时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;

    /**
     * 消息更新时间
     */
    @TableField("update_time")
    private LocalDateTime updateTime;

    /**
     * 是否删除: 0-未删除, 1-已删除
     */
    @TableLogic(value = "0", delval = "1")
    @TableField("is_deleted")
    private Integer isDeleted;

    /**
     * 消息描述
     */
    @TableField("description")
    private String description;
}
