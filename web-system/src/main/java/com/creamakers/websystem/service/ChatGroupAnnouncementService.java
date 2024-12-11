package com.creamakers.websystem.service;

import com.creamakers.websystem.domain.vo.ResultVo;
import com.creamakers.websystem.domain.vo.request.ChatGroupAnnounceReq;
import com.creamakers.websystem.domain.vo.response.ChatGroupAnnouncementResp;

import java.util.List;

public interface ChatGroupAnnouncementService {
    ResultVo<List<ChatGroupAnnouncementResp>> getAllAnnouncements(Integer page, Integer pageSize);

    ResultVo<ChatGroupAnnouncementResp> getAnnouncementByAnnouncementId(Long announcementId);

    ResultVo<ChatGroupAnnouncementResp> updateAnnouncementByAnnouncementId(Long announcementId, ChatGroupAnnounceReq chatGroupAnnounceReq);

    ResultVo<Void> deleteAnnouncementByAnnouncementId(Long announcementId);
}
