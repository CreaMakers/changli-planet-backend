package com.hayaizo.chatsystem.service;

import com.hayaizo.chatsystem.dto.request.ChatMessagePageReq;
import com.hayaizo.chatsystem.dto.request.ChatMessageReq;
import com.hayaizo.chatsystem.dto.response.ChatMessageResp;
import com.hayaizo.chatsystem.dto.response.CursorPageBaseResp;

public interface ChatService {

    Integer sendMsg(ChatMessageReq request, Integer uid);

    ChatMessageResp getMsgResp(Integer msgID);

    CursorPageBaseResp<ChatMessageResp> getMsgPage(ChatMessagePageReq request);
}
