package com.hayaizo.chatsystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hayaizo.chatsystem.po.UserProfile;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserProfileMapper extends BaseMapper<UserProfile> {
}
