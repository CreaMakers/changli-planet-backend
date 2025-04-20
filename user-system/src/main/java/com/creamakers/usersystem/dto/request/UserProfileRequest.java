package com.creamakers.usersystem.dto.request;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileRequest implements Serializable {

    private String avatarUrl;
    private String bio;
    private int userLevel;
    private String username;
    private String account;
    private int gender;
    private String grade;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date birthDate;
    private String location;
    private String website;
}
