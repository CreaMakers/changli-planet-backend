package com.creamakers.toolsystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.creamakers.toolsystem.po.User;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
