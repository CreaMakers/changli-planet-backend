package com.creamakers.websystem.service;

import com.creamakers.websystem.domain.vo.ResultVo;
import com.creamakers.websystem.domain.vo.response.ChatGroupMessageResp;
import com.creamakers.websystem.domain.vo.response.GroupUserResp;

import java.util.List;

public interface ChatService {

    ResultVo<List<GroupUserResp>> getGroupsByUserId(Long userId, Integer page, Integer pageSize);
    ResultVo<List<ChatGroupMessageResp>> getMessagesByGroupId(Long groupId,Integer page,Integer pageSize);
}
