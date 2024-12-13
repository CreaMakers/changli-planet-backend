package com.creamakers.websystem.controller;

import com.creamakers.websystem.domain.vo.ResultVo;
import com.creamakers.websystem.domain.vo.request.ChatGroupAnnounceReq;
import com.creamakers.websystem.domain.vo.response.ChatGroupAnnouncementResp;
import com.creamakers.websystem.service.ChatGroupAnnouncementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/web/announcements")
public class ChatGroupAnnouncementController {
    @Autowired
    private ChatGroupAnnouncementService chatGroupAnnouncementService;

    @GetMapping
    public ResultVo<List<ChatGroupAnnouncementResp>> getAllAnnouncements(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                                                         @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        return chatGroupAnnouncementService.getAllAnnouncements(page, pageSize);
    }

    @GetMapping("/{announcement_id}")
    public ResultVo<ChatGroupAnnouncementResp> getAnnouncementByAnnouncementId(@PathVariable("announcement_id") Long AnnouncementId) {
        return chatGroupAnnouncementService.getAnnouncementByAnnouncementId(AnnouncementId);
    }

    @PutMapping("/{announcement_id}")
    public ResultVo<ChatGroupAnnouncementResp> updateAnnouncementByAnnouncementId(@PathVariable("announcement_id") Long AnnouncementId, @RequestBody ChatGroupAnnounceReq chatGroupAnnounceReq) {
        return chatGroupAnnouncementService.updateAnnouncementByAnnouncementId(AnnouncementId, chatGroupAnnounceReq);
    }

    @DeleteMapping("/{announcement_id}")
    public ResultVo<Void> deleteAnnouncementByAnnouncementId(@PathVariable("announcement_id") Long AnnouncementId) {
        return chatGroupAnnouncementService.deleteAnnouncementByAnnouncementId(AnnouncementId);
    }
}
