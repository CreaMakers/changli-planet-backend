package com.creamakers.toolsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.creamakers.toolsystem.exception.BusinessException;
import com.creamakers.toolsystem.mapper.LearningMaterialMapper;
import com.creamakers.toolsystem.po.LearningMaterial;
import com.creamakers.toolsystem.service.LearningMaterialService;
import com.creamakers.toolsystem.util.OssService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

import static com.creamakers.toolsystem.consts.CommonConstant.*;

/**
 * 学习资料服务实现类
 */
@Slf4j
@Service
public class LearningMaterialServiceImpl implements LearningMaterialService {
    
    @Autowired
    private LearningMaterialMapper learningMaterialMapper;
    
    @Autowired
    private OssService ossService;
    
    /**
     * 分页查询学习资料列表
     */
    @Override
    public Page<LearningMaterial> queryPage(Integer pageNum, Integer pageSize, String category, String keyword) {
        log.info("分页查询学习资料列表，pageNum={}, pageSize={}, category={}, keyword={}", 
                pageNum, pageSize, category, keyword);
        
        // 创建分页对象
        Page<LearningMaterial> page = new Page<>(pageNum, pageSize);
        
        // 构建查询条件
        LambdaQueryWrapper<LearningMaterial> queryWrapper = new LambdaQueryWrapper<>();
        
        // 只查询status=1的正常状态数据
        queryWrapper.eq(LearningMaterial::getStatus, STATUS_NORMAL);
        
        // 如果分类不为空，添加分类筛选条件
        if (StringUtils.hasText(category)) {
            queryWrapper.eq(LearningMaterial::getCategory, category);
        }
        
        // 如果关键词不为空，添加文件名模糊搜索条件
        if (StringUtils.hasText(keyword)) {
            queryWrapper.like(LearningMaterial::getFileName, keyword);
        }
        
        // 按创建时间倒序排列
        queryWrapper.orderByDesc(LearningMaterial::getCreateTime);
        
        // 执行分页查询
        Page<LearningMaterial> result = learningMaterialMapper.selectPage(page, queryWrapper);
        
        log.info("查询完成，共{}条记录", result.getTotal());
        
        return result;
    }
    
    /**
     * 获取下载链接
     */
    @Override
    public String getDownloadUrl(Long id, Long userId) {
        log.info("获取下载链接，id={}, userId={}", id, userId);
        
        // 根据id查询资料
        LearningMaterial material = learningMaterialMapper.selectById(id);
        
        // 校验资料是否存在
        if (material == null) {
            log.error("资料不存在，id={}", id);
            throw new BusinessException("资料不存在");
        }
        
        // 校验资料状态是否正常
        if (material.getStatus() == null || material.getStatus() != 1) {
            log.error("资料已失效，id={}, status={}", id, material.getStatus());
            throw new BusinessException("资料已失效");
        }
        
        // 增加下载次数（使用数据库行锁保证并发安全）
        LambdaUpdateWrapper<LearningMaterial> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(LearningMaterial::getId, id)
                     .setSql("download_count = download_count + 1");
        int updateResult = learningMaterialMapper.update(null, updateWrapper);
        
        if (updateResult > 0) {
            log.info("下载次数+1成功，id={}", id);
        } else {
            log.warn("下载次数更新失败，id={}", id);
        }
        
        // 生成OSS签名URL
        String signedUrl = ossService.generateDownloadUrl(material.getOssKey(), OSS_URL_EXPIRE_SECONDS);
        
        log.info("生成下载链接成功，id={}, url={}", id, signedUrl);
        
        return signedUrl;
    }
    
    /**
     * 获取分类列表
     */
    @Override
    public List<String> getCategoryList() {
        log.info("获取分类列表");
        
        // 构建查询条件
        LambdaQueryWrapper<LearningMaterial> queryWrapper = new LambdaQueryWrapper<>();
        
        // 只查询status=1的正常状态数据
        queryWrapper.eq(LearningMaterial::getStatus, STATUS_NORMAL);
        
        // 查询所有资料
        List<LearningMaterial> materials = learningMaterialMapper.selectList(queryWrapper);
        
        // 提取分类名称，去重，过滤null和'其他'，并排序
        List<String> categories = materials.stream()
                .map(LearningMaterial::getCategory)
                .filter(category -> category != null && !OTHER_CATEGORY.equals(category))
                .distinct()
                .sorted()
                .collect(Collectors.toList());
        
        log.info("获取分类列表完成，共{}个分类", categories.size());
        
        return categories;
    }
}