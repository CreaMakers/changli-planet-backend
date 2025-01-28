package com.hayaizo.chatsystem.dto.response;

import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class VideoMsgDTO  implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("缩略图宽度（像素）")
    @NotNull
    private Integer thumbWidth;

    @ApiModelProperty("缩略图高度（像素）")
    @NotNull
    private Integer thumbHeight;

    @ApiModelProperty("缩略图大小（字节）")
    @NotNull
    private Long thumbSize;

    @ApiModelProperty("缩略图下载地址")
    @NotBlank
    private String thumbUrl;

    @ApiModelProperty("大小（字节）")
    @NotNull
    private Long size;

    @ApiModelProperty("下载地址")
    @javax.validation.constraints.NotBlank
    private String url;

}
