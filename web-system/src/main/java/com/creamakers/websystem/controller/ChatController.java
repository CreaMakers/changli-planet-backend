package com.creamakers.websystem.controller;

import com.creamakers.websystem.domain.vo.ResultVo;
import com.creamakers.websystem.domain.vo.response.ChatGroupMessageResp;
import com.creamakers.websystem.domain.vo.response.GroupUserResp;
import com.creamakers.websystem.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/web")
public class ChatController {
    @Autowired
    private ChatService chatService;
    @GetMapping("/users/{userId}/groups")
    public ResultVo<List<GroupUserResp>> getGroupsByUserId(@PathVariable("userId") Long userId,
                                                                   @RequestParam(value = "page",defaultValue = "1") Integer page,
                                                                @RequestParam(value = "pageSize", defaultValue = "10")  Integer pageSize) {
        return chatService.getGroupsByUserId(userId,page,pageSize);
    }
    @GetMapping("/groups/{groupId}/messages")
    public ResultVo<List<ChatGroupMessageResp>> getMessagesByGroupId(@PathVariable("groupId")Long groupId,
                                                               @RequestParam(value = "page",defaultValue = "1") Integer page,
                                                               @RequestParam(value = "pageSize", defaultValue = "10")  Integer pageSize){
        return chatService.getMessagesByGroupId(groupId,page,pageSize);
    }
}
