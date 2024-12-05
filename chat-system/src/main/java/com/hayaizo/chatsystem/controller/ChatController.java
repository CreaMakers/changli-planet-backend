package com.hayaizo.chatsystem.controller;

import com.hayaizo.chatsystem.dto.request.ChatMessageReq;
import com.hayaizo.chatsystem.dto.response.ChatMessageResp;
import com.hayaizo.chatsystem.dto.response.GeneralResponse;
import com.hayaizo.chatsystem.service.ChatService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@Api(tags = "聊天室相关接口")
@RequestMapping("/app/chat")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @PostMapping("/msg")
    @ApiOperation("发送消息")
    public GeneralResponse<ChatMessageResp> sendMsg(@Valid @RequestBody ChatMessageReq request) {
        chatService.sendMsg(request, request.getUid());
        // 返回完整消息格式，方便前端展示
        return null;
    }

    @GetMapping("")
    public String test(){
        return "test";
    }

}