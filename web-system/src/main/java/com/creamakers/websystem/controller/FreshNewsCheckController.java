package com.creamakers.websystem.controller;


import com.creamakers.websystem.domain.vo.ResultVo;
import com.creamakers.websystem.domain.vo.response.FreshNewsCheckResp;
import com.creamakers.websystem.service.FreshNewsCheckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/web/fresh_news/check")
public class FreshNewsCheckController {
    @Autowired
    private FreshNewsCheckService freshNewsCheckService;

    /**
     * 获取所有新鲜事审核图片记录
     * @param page 页码
     * @param pageSize 每页数量
     * @return 新鲜事审核图片记录列表
     */
    @GetMapping(value = "/image_all")
    public ResultVo<List<FreshNewsCheckResp>> getAllFreshNewsCheck(
            @RequestParam("page") Integer page, @RequestParam("pageSize") Integer pageSize){
        return freshNewsCheckService.findAllFreshNewsCheck(page,pageSize);
    }

    /**
     * 条件查询新鲜事审核图片记录
     * @param freshNewsCheckId 审核记录ID
     * @param freshNewsId 新鲜事ID
     * @param checkStatus 审核状态
     * @param page 页码
     * @param pageSize 每页数量
     * @return 新鲜事审核图片记录列表
     */
    @GetMapping(value = "/image_query")
    public ResultVo<List<FreshNewsCheckResp>> getFreshNewsCheckByQuery(
            @RequestParam(name = "freshNewsCheckId", required = false) Long freshNewsCheckId,
            @RequestParam(name = "freshNewsId", required = false) Long freshNewsId,
            @RequestParam(name = "checkStatus", required = false) Integer checkStatus,
            @RequestParam("page") Integer page,
            @RequestParam("pageSize") Integer pageSize){
        return freshNewsCheckService.findFreshNewsCheckByQuery(freshNewsCheckId,freshNewsId,checkStatus,page,pageSize);
    }

    /**
    * 新鲜事图片审核
    * @param freshNewsCheckId 审核记录ID
    * @param checkStatus 审核状态
    * @return 审核结果
    */
    @PutMapping(value = "/image")
    public ResultVo<Void> checkFreshNews(
            @RequestParam("freshNewsCheckId") Long freshNewsCheckId, @RequestParam("checkStatus") Integer checkStatus){
        return freshNewsCheckService.checkFreshNews(freshNewsCheckId,checkStatus);
    }
}
