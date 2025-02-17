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
@ApiModel(description = "成绩详细请求实体类")
public class GradeDetailInfoRequest {
  private String stuNum;
  private String password;
  private String pscjUrl;
}
