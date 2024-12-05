package com.hayaizo.chatsystem.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @description 用户申请加入群聊表
 * @author hayaizo
 * @date 2024-11-27
 */
@Data
public class ChatGroupApply implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    /**
    * 申请id
    */
    private Integer applyId;

    /**
    * 群聊id
    */
    private Integer groupId;

    /**
    * 申请用户id
    */
    private Integer userId;

    /**
    * 申请理由或附加信息
    */
    private String applyMessage;

    /**
    * 申请状态: 0-待审核， 1-已批准， 2-已拒绝
    */
    private Integer status;

    /**
    * 申请时间
    */
    private Date applyTime;

    /**
    * 处理时间
    */
    private Date processedTime;

    /**
    * 处理申请的管理员id
    */
    private Integer processedBy;

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
    * 用户申请动态数据描述
    */
    private String description;

    public ChatGroupApply() {}
}