package com.creamakers.toolsystem.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.creamakers.toolsystem.dto.response.GeneralResponse;
import com.creamakers.toolsystem.po.LearningMaterial;
import com.creamakers.toolsystem.service.LearningMaterialService;
import org.springframework.http.ResponseEntity;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 学习资料控制器
 * 提供学习资料的查询、下载等HTTP接口
 */
@Slf4j
@RestController
@RequestMapping("/material")
@Api(tags = "学习资料管理")
public class MaterialController {
    
    @Autowired
    private LearningMaterialService learningMaterialService;
    
    /**
     * 分页查询学习资料列表
     * 
     * @param pageNum 页码（默认1）
     * @param pageSize 每页大小（默认10）
     * @param category 分类（可选）
     * @param keyword 关键词（可选，用于文件名模糊搜索）
     * @return 分页结果
     */
    @GetMapping("/list")
    @ApiOperation(value = "分页查询学习资料列表", notes = "支持按分类筛选和关键词搜索")
    public ResponseEntity<GeneralResponse<Page<LearningMaterial>>> listMaterials(
            @ApiParam(value = "页码", example = "1") @RequestParam(required = false, defaultValue = "1") Integer pageNum,
            @ApiParam(value = "每页大小", example = "10") @RequestParam(required = false, defaultValue = "10") Integer pageSize,
            @ApiParam(value = "分类") @RequestParam(required = false) String category,
            @ApiParam(value = "关键词") @RequestParam(required = false) String keyword) {
        
        log.info("接收到分页查询请求，pageNum={}, pageSize={}, category={}, keyword={}", 
                pageNum, pageSize, category, keyword);
        
        try {
            Page<LearningMaterial> result = learningMaterialService.queryPage(pageNum, pageSize, category, keyword);
            return ResponseEntity.ok(GeneralResponse.success(result));
        } catch (Exception e) {
            log.error("分页查询失败", e);
            return ResponseEntity.ok(GeneralResponse.fail("查询失败：" + e.getMessage()));
        }
    }
    
    /**
     * 生成下载链接
     * 
     * @param id 资料ID
     * @return OSS签名下载URL
     */
    @GetMapping("/download/{id}")
    @ApiOperation(value = "生成下载链接", notes = "生成有效期15分钟的OSS签名下载URL")
    public ResponseEntity<GeneralResponse<String>> getDownloadUrl(
            @ApiParam(value = "资料ID", required = true) @PathVariable Long id) {
        
        log.info("接收到生成下载链接请求，id={}", id);
        
        try {
            String downloadUrl = learningMaterialService.getDownloadUrl(id, null);
            return ResponseEntity.ok(GeneralResponse.success(downloadUrl));
        } catch (Exception e) {
            log.error("生成下载链接失败，id={}", id, e);
            return ResponseEntity.ok(GeneralResponse.fail(e.getMessage()));
        }
    }
    
    /**
     * 获取分类列表
     * 
     * @return 分类名称列表
     */
    @GetMapping("/categories")
    @ApiOperation(value = "获取分类列表", notes = "查询所有有效的分类名称")
    public ResponseEntity<GeneralResponse<List<String>>> getCategories() {
        
        log.info("接收到获取分类列表请求");
        
        try {
            List<String> categories = learningMaterialService.getCategoryList();
            return ResponseEntity.ok(GeneralResponse.success(categories));
        } catch (Exception e) {
            log.error("获取分类列表失败", e);
            return ResponseEntity.ok(GeneralResponse.fail("获取分类列表失败：" + e.getMessage()));
        }
    }
}