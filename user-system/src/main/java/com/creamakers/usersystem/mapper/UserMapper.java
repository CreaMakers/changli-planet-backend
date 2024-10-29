package com.creamakers.usersystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.creamakers.usersystem.po.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * 注意信息:
 *
 * 1. 请确保遵循代码风格规范。
 * 2. 不可以使用过时的方法。
 * 3. 所有的输入参数必须经过验证。
 *
 * @throws IllegalArgumentException 如果输入参数无效
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

}
