package com.creamakers.toolsystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.creamakers.toolsystem.po.LearningMaterial;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * 学习资料Mapper接口
 * 继承MyBatis-Plus的BaseMapper，提供基础CRUD操作
 * 
 * @author Prologue
 */
@Mapper
@Repository
public interface LearningMaterialMapper extends BaseMapper<LearningMaterial> {
    
    // MyBatis-Plus已提供基础CRUD方法，无需手动编写
    // 如需自定义SQL，可在resources/mapper目录下创建XML文件
}