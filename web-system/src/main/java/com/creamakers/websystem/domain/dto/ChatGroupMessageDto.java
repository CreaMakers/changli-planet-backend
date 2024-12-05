package com.creamakers.websystem.domain.dto;

import com.baomidou.mybatisplus.annotation.TableName;
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
    private Integer messageId;

    /**
     * 所属群聊ID
     */
    private Integer groupId;

    /**
     * 发送者用户ID
     */
    private Integer senderId;

    /**
     * 回复目标的用户ID（回复某人的消息）
     */
    private Integer receiverId;

    /**
     * 聊天内容
     */
    private String messageContent;

    /**
     * 文件存储的URL路径
     */
    private String fileUrl;

    /**
     * 文件类型: 1-图片, 2-文档, 3-视频, 4-音频, 5-其他
     */
    private Integer fileType;

    /**
     * 额外消息内容，JSON格式
     */
    private String extra;

    /**
     * 消息间隔数
     */
    private Integer gapCount;

    /**
     * 消息发送时间
     */
    private LocalDateTime createTime;

    /**
     * 消息更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 是否删除: 0-未删除, 1-已删除
     */
    private Integer isDeleted;

    /**
     * 消息描述
     */
    private String description;

}
