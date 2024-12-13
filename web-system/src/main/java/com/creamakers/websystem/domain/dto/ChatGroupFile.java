package com.creamakers.websystem.domain.dto;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("chat_group_file")
public class ChatGroupFile {

    /**
     * 文件ID
     */
    @TableId(value = "file_id", type = IdType.AUTO)
    private Long fileId;

    /**
     * 所属群聊ID
     */
    @TableField("group_id")
    private Long groupId;

    /**
     * 上传用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 文件名称
     */
    @TableField("file_name")
    private String fileName;

    /**
     * 文件大小（字节）
     */
    @TableField("file_size")
    private Long fileSize;

    /**
     * 文件类型: 1-图片, 2-文档, 3-视频, 4-音频, 5-其他
     */
    @TableField("file_type")
    private Integer fileType;

    /**
     * 文件存储的URL路径
     */
    @TableField("file_url")
    private String fileUrl;

    /**
     * 文件上传时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;

    /**
     * 文件更新时间
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
     * 文件描述
     */
    @TableField("description")
    private String description;
}

