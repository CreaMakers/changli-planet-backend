package com.creamakers.websystem.domain.vo.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatGroupFileResp {

    /**
     * 文件ID
     */
    private Long fileId;

    /**
     * 所属群聊ID
     */
    private Long groupId;

    /**
     * 上传用户ID
     */
    private Long userId;

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 文件大小（字节）
     */
    private Long fileSize;

    /**
     * 文件类型: 1-图片, 2-文档, 3-视频, 4-音频, 5-其他
     */
    private Integer fileType;

    /**
     * 文件存储的URL路径
     */
    private String fileUrl;

    /**
     * 文件上传时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private LocalDateTime createTime;

    /**
     * 文件更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private LocalDateTime updateTime;

    /**
     * 是否删除: false-未删除, true-已删除
     */
    private Boolean isDeleted;

    /**
     * 文件描述
     */
    private String description;
}

