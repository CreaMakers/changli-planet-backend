package com.creamakers.websystem.controller;

import com.creamakers.websystem.domain.vo.ResultVo;
import com.creamakers.websystem.domain.vo.request.ViolationRecordReq;
import com.creamakers.websystem.domain.vo.response.ChatGroupResp;
import com.creamakers.websystem.domain.vo.response.ViolationRecordResp;
import com.creamakers.websystem.service.ChatGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/web/groups")
public class ChatGroupController {
    @Autowired
    private ChatGroupService chatGroupService;
    @GetMapping
    public ResultVo<List<ChatGroupResp>> getAllChatGroups(@RequestParam(value = "page",defaultValue = "1") Integer page,
                                                          @RequestParam(value = "pageSize", defaultValue = "10")  Integer pageSize) {
        return chatGroupService.getAllChatGroups(page,pageSize);
    }
    @PutMapping("/{groupId}")
    public ResultVo<ChatGroupResp> updateChatGroupById(@PathVariable(value = "groupId") Long groupId, @RequestBody ViolationRecordReq violationRecordRep){
        return null;
    }

}
