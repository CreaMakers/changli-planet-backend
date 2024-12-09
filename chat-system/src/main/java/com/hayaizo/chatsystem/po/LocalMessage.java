package com.hayaizo.chatsystem.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @description 本地消息表
 * @author zhengkai.blog.csdn.net
 * @date 2024-11-27
 */
@Data
@TableName("local_message")
public class LocalMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    /**
    * 本地消息唯一标识
    */
    private Long id;

    /**
    * 安全调用相关的 json 信息
    */
    private String secureInvokeJson;

    /**
    * 消息状态（例如待重试）
    */
    private Integer status;

    /**
    * 重试次数
    */
    private Integer retryTimes;

    /**
    * 下次重试的时间
    */
    private Date nextRetryTimes;

    /**
    * 失败原因
    */
    private String failReason;

    /**
    * 记录创建时间
    */
    private Date createTime;

    /**
    * 记录更新时间
    */
    private Date updateTime;

    /**
    * 是否删除: 0-未删除，1-已删除
    */
    private Integer isDeleted;

    /**
    * 消息描述
    */
    private String description;

    public LocalMessage() {}
}