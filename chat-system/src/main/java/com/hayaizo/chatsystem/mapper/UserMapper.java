package com.hayaizo.chatsystem.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hayaizo.chatsystem.po.User;
import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface UserMapper extends BaseMapper<User> {

}
