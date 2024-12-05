package com.hayaizo.chatsystem.service;

import com.hayaizo.chatsystem.dto.request.ChatMessageReq;

public interface ChatService {

    void sendMsg(ChatMessageReq request, Integer uid);

}
