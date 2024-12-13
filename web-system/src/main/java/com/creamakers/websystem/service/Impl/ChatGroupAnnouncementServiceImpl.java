package com.creamakers.websystem.service.Impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.creamakers.websystem.dao.ChatGroupAnnouncementMapper;
import com.creamakers.websystem.domain.dto.ChatGroupAnnouncement;
import com.creamakers.websystem.domain.vo.ResultVo;
import com.creamakers.websystem.domain.vo.request.ChatGroupAnnounceReq;
import com.creamakers.websystem.domain.vo.response.ChatGroupAnnouncementResp;
import com.creamakers.websystem.service.ChatGroupAnnouncementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.creamakers.websystem.constants.CommonConst.*;

@Service
public class ChatGroupAnnouncementServiceImpl implements ChatGroupAnnouncementService {
    @Autowired
    private ChatGroupAnnouncementMapper chatGroupAnnouncementMapper;

    @Override
    public ResultVo<List<ChatGroupAnnouncementResp>> getAllAnnouncements(Integer page, Integer pageSize) {
        Page<ChatGroupAnnouncement> pageParam = new Page<>(page, pageSize);
        Page<ChatGroupAnnouncement> Page = chatGroupAnnouncementMapper.selectPage(pageParam, new QueryWrapper<ChatGroupAnnouncement>().eq("is_deleted", 0));
        List<ChatGroupAnnouncement> records = Page.getRecords();
        List<ChatGroupAnnouncementResp> chatGroupRespList = records.stream().map(this::convertToChatGroupAnnouncementResp).toList();
        return ResultVo.success(chatGroupRespList);
    }

    @Override
    public ResultVo<ChatGroupAnnouncementResp> getAnnouncementByAnnouncementId(Long announcementId) {
        ChatGroupAnnouncement chatGroupAnnouncement = chatGroupAnnouncementMapper.selectById(announcementId);
        if (chatGroupAnnouncement == null) return ResultVo.fail(GROUP_BULLETIN_ID_NOT_EXIST);
        return ResultVo.success(convertToChatGroupAnnouncementResp(chatGroupAnnouncement));
    }

    @Override
    public ResultVo<ChatGroupAnnouncementResp> updateAnnouncementByAnnouncementId(Long announcementId, ChatGroupAnnounceReq chatGroupAnnounceReq) {
        ChatGroupAnnouncement chatGroupAnnouncement = chatGroupAnnouncementMapper.selectById(announcementId);
        if (chatGroupAnnouncement == null) return ResultVo.fail(GROUP_BULLETIN_ID_NOT_EXIST);
        // 更新公告字段
        chatGroupAnnouncement.setTitle(chatGroupAnnounceReq.getTitle());
        chatGroupAnnouncement.setContent(chatGroupAnnounceReq.getContent());

        boolean pinned = chatGroupAnnounceReq.isPinned();
        if (pinned) chatGroupAnnouncement.setIsPinned(1);
        else chatGroupAnnouncement.setIsPinned(0);

        chatGroupAnnouncement.setDescription(chatGroupAnnounceReq.getDescription());
        System.out.println("After Update: " + chatGroupAnnouncement);
        // 执行更新操作
        int updateCount = chatGroupAnnouncementMapper.updateById(chatGroupAnnouncement);

        // 如果更新失败，返回失败结果
        if (updateCount == 0) {
            return ResultVo.fail(UPDATE_GROUP_ANNOUNCEMENT_FAILED);
        }
        return ResultVo.success(convertToChatGroupAnnouncementResp(chatGroupAnnouncement));
    }

    @Override
    public ResultVo<Void> deleteAnnouncementByAnnouncementId(Long announcementId) {
        int i = chatGroupAnnouncementMapper.deleteById(announcementId);
        if (i < 1) return ResultVo.fail(DATA_DELETE_FAILED_NOT_FOUND);
        return ResultVo.success();
    }

    private ChatGroupAnnouncementResp convertToChatGroupAnnouncementResp(ChatGroupAnnouncement record) {
        ChatGroupAnnouncementResp ChatGroupAnnouncementResp = new ChatGroupAnnouncementResp();
        BeanUtil.copyProperties(record, ChatGroupAnnouncementResp);
        return ChatGroupAnnouncementResp;
    }
}
