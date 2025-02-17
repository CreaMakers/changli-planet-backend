package com.hayaizo.chatsystem.service;

import com.hayaizo.chatsystem.dto.request.ChatMessageReq;
import com.hayaizo.chatsystem.dto.response.ChatMessageResp;

public interface ChatService {

    Integer sendMsg(ChatMessageReq request, Integer uid);

    ChatMessageResp getMsgResp(Integer msgID);
}
