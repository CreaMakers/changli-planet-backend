package com.creamakers.toolsystem.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
@ApiModel(description = "主题皮肤实体类")
@TableName("topic_skin")
public class TopicSkin {
    @ApiModelProperty(value = "主键id",example = "1")
    @TableId(type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "皮肤名称",example = "默认皮肤")
    @TableField("name")
    private String name;

    @ApiModelProperty(value = "皮肤路径",example = "/usr/www/changli-planet-backend/skin/default.apk")
    @TableField("path")
    private String path;

    @ApiModelProperty(value = "皮肤图片预览路径",example = "http://localhost:8080/topic-skin/123456.png")
    @TableField("image_url")
    private String imageUrl;

    @ApiModelProperty(value = "资源完整性校验值",example = "123456")
    @TableField("hash_md5")
    private String hashMd5;

    @ApiModelProperty(value = "创建时间",example = "2023-01-01 00:00:00")
    @TableField("create_time")
    private LocalDateTime createTime;
}
