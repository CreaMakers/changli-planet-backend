package com.creamakers.usersystem.service;

import com.creamakers.usersystem.dto.response.GeneralResponse;
import org.springframework.http.ResponseEntity;

public interface ApkService {
    ResponseEntity<GeneralResponse> checkApkVersion(Integer versionCode, String versionName);
}
