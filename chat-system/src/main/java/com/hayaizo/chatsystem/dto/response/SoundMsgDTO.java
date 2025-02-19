package com.hayaizo.chatsystem.dto.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class SoundMsgDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("时长（秒）")
    private Integer second;

    @ApiModelProperty("大小（字节）")
    @NotNull
    private Long size;

    @ApiModelProperty("下载地址")
    @NotBlank
    private String url;

}
