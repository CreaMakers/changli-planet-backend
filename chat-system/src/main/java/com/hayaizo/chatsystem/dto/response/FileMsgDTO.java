package com.hayaizo.chatsystem.dto.response;

import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.checkerframework.checker.units.qual.N;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FileMsgDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("下载地址")
    @NotBlank
    private String url;

    @ApiModelProperty("大小（字节）")
    @NotNull
    private Long size;

    @ApiModelProperty("文件名（带后缀）")
    @NotBlank
    private String fileName;
}
