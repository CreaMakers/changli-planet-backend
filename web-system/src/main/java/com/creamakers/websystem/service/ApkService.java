package com.creamakers.websystem.service;

import com.creamakers.websystem.domain.vo.ResultVo;
import com.creamakers.websystem.domain.vo.response.ApkResp;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ApkService {
    ResultVo<ApkResp> updateApk(Integer versionCode, String versionName, String updateMessage, MultipartFile apkFile) throws IOException;
}
