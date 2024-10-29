package com.creamakers.toolsystem.dto.request;


import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
@ApiModel(description = "考试范围")
public class ExamArrangeInfoRequest {
    private String stuNum;
    private String password;
    private String term;
    private String examType;
}
