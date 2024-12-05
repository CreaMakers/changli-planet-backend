package com.creamakers.websystem.service;

import com.creamakers.websystem.domain.vo.ResultVo;
import com.creamakers.websystem.domain.vo.response.ChatGroupResp;
import com.creamakers.websystem.domain.vo.response.ViolationRecordResp;

import java.util.List;

public interface ChatGroupService {
    ResultVo<List<ChatGroupResp>> getAllChatGroups(Integer page, Integer pageSize);
}
