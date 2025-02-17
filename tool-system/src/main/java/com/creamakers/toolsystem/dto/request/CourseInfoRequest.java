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
@ApiModel(description = "课表请求实体类")
public class CourseInfoRequest {
  private String stuNum;
  private String password;
  private String week;
  public String termId;
}
