package com.creamakers.websystem.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.creamakers.websystem.domain.dto.ViolationRecord;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ViolationRecordMapper extends BaseMapper<ViolationRecord> {
}
