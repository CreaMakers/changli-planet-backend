package com.creamakers.fresh.system.domain.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("user") // 查询用户是否是管理员
public class UserAdmin {

    @TableId(type = IdType.AUTO)
    private Integer userId;

    /**
     * 是否是管理员: 0-普通用户, 1-运营组, 2-开发组
     */
    @TableField("is_admin")
    private Byte isAdmin;
}
