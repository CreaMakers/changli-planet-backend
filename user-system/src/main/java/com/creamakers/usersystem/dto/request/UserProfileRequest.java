package com.creamakers.usersystem.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileRequest implements Serializable {

    private String avatarUrl;
    private String bio;
    private int userLevel;
    private int gender;
    private int grade;
    private String birthdate; // 可以使用 LocalDate 类型更精确
    private String location;
    private String website;
}
