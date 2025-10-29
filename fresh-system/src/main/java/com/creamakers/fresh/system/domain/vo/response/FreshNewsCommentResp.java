package com.creamakers.fresh.system.domain.vo.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FreshNewsCommentResp {
    //评论所属新鲜事ID
    @JsonProperty(value = "freshNewsId")
    private Long freshNewsId;

    //一级评论的数量
    @JsonProperty(value = "firstCommentCount")
    private Integer firstCommentCount;

    //是否显示评论区
    @JsonProperty(value = "isActive")
    private Integer isActive;

    //一级评论区列表
    @JsonProperty(value = "commentsList")
    private List<FreshNewsFatherCommentResp> commentsList;

}
