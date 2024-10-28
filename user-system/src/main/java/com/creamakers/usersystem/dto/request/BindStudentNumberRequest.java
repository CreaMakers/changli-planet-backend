package com.creamakers.usersystem.dto.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class BindStudentNumberRequest implements Serializable {
    private String studentNumber;
}
