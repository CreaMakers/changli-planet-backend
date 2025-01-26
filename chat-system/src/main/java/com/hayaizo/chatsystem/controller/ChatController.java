package com.hayaizo.chatsystem.controller;

import com.hayaizo.chatsystem.common.constant.HttpCode;
import com.hayaizo.chatsystem.dto.request.ChatMessagePageReq;
import com.hayaizo.chatsystem.dto.request.ChatMessageReq;
import com.hayaizo.chatsystem.dto.response.ChatMessageResp;
import com.hayaizo.chatsystem.dto.response.CursorPageBaseResp;
import com.hayaizo.chatsystem.dto.response.GeneralResponse;
import com.hayaizo.chatsystem.service.ChatService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.hayaizo.chatsystem.common.constant.ErrorMessage.USER_ROO_ERROR;

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
        Integer msgID = chatService.sendMsg(request, request.getUid());
        // 返回完整消息格式，方便前端展示
        GeneralResponse<ChatMessageResp> response = new GeneralResponse<>();
        if(msgID == -1) {
            response.setCode(HttpCode.FORBIDDEN);
            response.setMsg(USER_ROO_ERROR);
            return response;
        }
        response.setData(chatService.getMsgResp(msgID));
        response.setCode(HttpCode.OK);
        response.setMsg("发送成功");
        return response;
    }

    @GetMapping("/msg/page")
    @ApiOperation("消息列表")
    public GeneralResponse<CursorPageBaseResp<ChatMessageResp>> getMsgPage(@Valid ChatMessagePageReq request) {
        CursorPageBaseResp<ChatMessageResp> msgPage = chatService.getMsgPage(request);

        return null;
    }

}