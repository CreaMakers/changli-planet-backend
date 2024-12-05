package com.creamakers.websystem.service.Impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.creamakers.websystem.constants.CommonConst;
import com.creamakers.websystem.context.UserContext;
import com.creamakers.websystem.dao.ChatGroupMessageMapper;
import com.creamakers.websystem.dao.GroupUserMapper;
import com.creamakers.websystem.dao.UserMapper;
import com.creamakers.websystem.domain.dto.ChatGroupMessageDto;
import com.creamakers.websystem.domain.dto.GroupUser;
import com.creamakers.websystem.domain.dto.User;
import com.creamakers.websystem.domain.vo.ResultVo;
import com.creamakers.websystem.domain.vo.response.ChatGroupMessageResp;
import com.creamakers.websystem.domain.vo.response.GroupUserResp;
import com.creamakers.websystem.enums.ErrorEnums;
import com.creamakers.websystem.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatServiceImpl implements ChatService {
    @Autowired
    private GroupUserMapper groupUserMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ChatGroupMessageMapper chatGroupMessageMapper;
    /*
    获取某个用户的群聊列表
     */
    @Override
    public ResultVo<List<GroupUserResp>> getGroupsByUserId(Long userId, Integer page, Integer pageSize) {
        User user = userMapper.selectById(userId);
        if(user == null) {
            return ResultVo.fail(CommonConst.BAD_REQUEST_CODE, CommonConst.BAD_USERINFO_QUERY);
        }
        /*
         * 权限不足
         * */
        if(user.getIsAdmin() == 0) {
            return ResultVo.fail(ErrorEnums.FORBIDDEN.getCode(), ErrorEnums.FORBIDDEN.getMsg());
        }
        Page<GroupUser> pageParam = new Page<>(page, pageSize);
        Page<GroupUser> Page = groupUserMapper.selectPage(pageParam, new QueryWrapper<GroupUser>().eq("is_deleted", 0).eq("user_id",userId));
        List<GroupUser> records = Page.getRecords();
        List<GroupUserResp> chatGroupRespList = records.stream().map(this:: convertToGroupUserResp).toList();
        return ResultVo.success(chatGroupRespList);
    }

    @Override
    public ResultVo<List<ChatGroupMessageResp>> getMessagesByGroupId(Long groupId, Integer page, Integer pageSize) {
        Long userId = UserContext.getUserId();
        User user = userMapper.selectById(userId);
        if(user == null) {
            return ResultVo.fail(CommonConst.ACCOUNT_NOT_FOUND);
        }
        /*
         * 权限不足
         * */
        if(user.getIsAdmin() == 0) {
            return ResultVo.fail(ErrorEnums.FORBIDDEN.getCode(), ErrorEnums.FORBIDDEN.getMsg());
        }
        Page<ChatGroupMessageDto> pagePram = new Page<>(page,pageSize);
        Page<ChatGroupMessageDto> Page = chatGroupMessageMapper.selectPage(pagePram,new QueryWrapper<ChatGroupMessageDto>()
                                                                .eq("is_deleted", 0)
                                                                .eq("group_id",groupId));
        List<ChatGroupMessageDto> records = Page.getRecords();
        List<ChatGroupMessageResp> list = records.stream().map(this::convertToChatGroupMessageResp).toList();
        return ResultVo.success(list);
    }

    private GroupUserResp convertToGroupUserResp(GroupUser record) {
        GroupUserResp groupUserResp = new GroupUserResp();
        BeanUtil.copyProperties(record, groupUserResp);
        return groupUserResp;
    }
    private ChatGroupMessageResp convertToChatGroupMessageResp(ChatGroupMessageDto record){
        ChatGroupMessageResp chatGroupMessageResp = new ChatGroupMessageResp();
        BeanUtil.copyProperties(record,chatGroupMessageResp);
        return chatGroupMessageResp;
    }
}
