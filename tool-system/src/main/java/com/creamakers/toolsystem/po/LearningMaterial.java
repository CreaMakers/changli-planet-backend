package com.creamakers.toolsystem.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 学习资料实体类
 * 映射数据库表 learning_material
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("learning_material")
public class LearningMaterial {
    
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 文件名
     */
    private String fileName;
    
    /**
     * OSS存储Key
     */
    private String ossKey;
    
    /**
     * 文件大小(字节)
     */
    private Long size;
    
    /**
     * 文件类型
     */
    private String type;
    
    /**
     * 分类名称
     */
    private String category;
    
    /**
     * 资料描述
     */
    private String description;
    
    /**
     * 上传者ID
     */
    private Long uploaderId;
    
    /**
     * 下载次数
     */
    private Integer downloadCount;
    
    /**
     * 点赞数
     */
    private Integer likeCount;
    
    /**
     * 状态(0:禁用,1:正常)
     */
    private Integer status;
    
    /**
     * 创建人
     */
    private String createBy;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 修改人
     */
    private String updateBy;
    
    /**
     * 修改时间
     */
    private LocalDateTime updateTime;
}