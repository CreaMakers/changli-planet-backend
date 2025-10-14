package com.hayaizo.chatsystem.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @description 文件系统表
 * @author hayaizo
 * @date 2024-11-27
 */
@Data
@TableName("chat_group_file")
public class ChatGroupFile implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    /**
    * 文件id
    */
    private Integer fileId;

    /**
    * 所属群聊id
    */
    private Integer groupId;

    /**
    * 上传用户id
    */
    private Integer userId;

    /**
    * 文件名称
    */
    private String fileName;

    /**
    * 文件大小（字节）
    */
    private Long fileSize;

    /**
    * 文件类型: 1-图片， 2-文档， 3-视频， 4-音频， 5-其他
    */
    private Integer fileType;

    /**
    * 文件存储的url路径
    */
    private String fileUrl;

    /**
    * 文件上传时间
    */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "GMT+8")
    private Date createTime;

    /**
    * 文件更新时间
    */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "GMT+8")
    private Date updateTime;

    /**
    * 是否删除: 0-未删除， 1-已删除
    */
    private Integer isDeleted;

    /**
    * 文件描述
    */
    private String description;

    public ChatGroupFile() {}
}