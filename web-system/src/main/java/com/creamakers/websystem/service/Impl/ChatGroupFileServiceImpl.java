package com.creamakers.websystem.service.Impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.creamakers.websystem.dao.ChatGroupFileMapper;
import com.creamakers.websystem.domain.dto.ChatGroupFile;
import com.creamakers.websystem.domain.vo.ResultVo;
import com.creamakers.websystem.domain.vo.response.ChatGroupFileResp;
import com.creamakers.websystem.service.ChatGroupFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.creamakers.websystem.constants.CommonConst.DATA_DELETE_FAILED_NOT_FOUND;

@Service
public class ChatGroupFileServiceImpl implements ChatGroupFileService {
    @Autowired
    private ChatGroupFileMapper chatGroupFileMapper;

    @Override
    public ResultVo<List<ChatGroupFileResp>> getFilesByFileType(Integer fileType, Integer page, Integer pageSize) {

        Page<ChatGroupFile> pageParam = new Page<>(page, pageSize);
        QueryWrapper<ChatGroupFile> queryWrapper = new QueryWrapper<>();
        if (fileType != null) {
            queryWrapper.eq("file_type", fileType);
        }
        Page<ChatGroupFile> filePage = chatGroupFileMapper.selectPage(pageParam, queryWrapper);

        List<ChatGroupFileResp> fileResponses = filePage.getRecords().stream().map(this::convertChatGroupToFileResp).collect(Collectors.toList());
        return ResultVo.success(fileResponses);
    }

    @Override
    public ResultVo<Void> deleteFileById(Long fileId) {
        int i = chatGroupFileMapper.deleteById(fileId);
        if (i < 1) return ResultVo.fail(DATA_DELETE_FAILED_NOT_FOUND);
        return ResultVo.success();
    }

    private ChatGroupFileResp convertChatGroupToFileResp(ChatGroupFile record) {
        ChatGroupFileResp chatGroupFileResp = new ChatGroupFileResp();
        BeanUtil.copyProperties(record, chatGroupFileResp);
        return chatGroupFileResp;
    }
}
