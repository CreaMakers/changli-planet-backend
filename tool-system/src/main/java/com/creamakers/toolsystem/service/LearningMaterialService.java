package com.creamakers.toolsystem.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.creamakers.toolsystem.po.LearningMaterial;

import java.util.List;

/**
 * 学习资料服务接口
 * 提供学习资料的查询、下载,上传,审核等功能
 */
public interface LearningMaterialService {
    
    /**
     * 分页查询学习资料列表
     * 
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @param category 分类（可选）
     * @param keyword 关键词（可选，用于文件名模糊搜索）
     * @return 分页结果
     */
    Page<LearningMaterial> queryPage(Integer pageNum, Integer pageSize, String category, String keyword);
    
    /**
     * 获取下载链接
     * 根据资料ID生成OSS签名下载URL，并增加下载次数
     * 
     * @param id 资料ID
     * @param userId 用户ID（暂时未使用）
     * @return OSS签名下载URL（有效期15分钟）
     */
    String getDownloadUrl(Long id, Long userId);
    
    /**
     * 获取分类列表
     * 查询所有有效的分类名称（排除null和'其他'）
     * 
     * @return 分类列表
     */
    List<String> getCategoryList();
}