package com.hayaizo.chatsystem.dto.request;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

@Data
@ApiModel("游标翻页请求")
@AllArgsConstructor
@NoArgsConstructor
public class CursorPageBaseReq {

    @ApiModelProperty("页面大小")
    @Min(0)
    @Max(100)
    private Integer pageSize = 10;

    @ApiModelProperty("游标（初始为null，后续请求附带上次翻页的游标）")
    private String cursor;

    public Page plusPage() {
        return new Page(1, this.pageSize, false);
    }

    @JsonIgnore
    public Boolean isFirstPage() {
        return StringUtils.isEmpty(cursor);
    }
}
