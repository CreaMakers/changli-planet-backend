package com.creamakers.fresh.system.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.creamakers.fresh.system.domain.dto.FreshNews;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FreshNewsMapper extends BaseMapper<FreshNews> {
}
