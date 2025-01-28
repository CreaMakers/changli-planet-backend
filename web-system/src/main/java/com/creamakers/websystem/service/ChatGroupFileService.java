package com.creamakers.websystem.service;

import com.creamakers.websystem.domain.vo.ResultVo;
import com.creamakers.websystem.domain.vo.response.ChatGroupFileResp;

import java.util.List;

public interface ChatGroupFileService {
    ResultVo<List<ChatGroupFileResp>> getFilesByFileType(Integer fileType, Integer page, Integer pageSize);

    ResultVo<Void> deleteFileById(Long fileId);
}
