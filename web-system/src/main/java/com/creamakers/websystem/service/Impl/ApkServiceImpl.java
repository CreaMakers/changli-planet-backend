package com.creamakers.websystem.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.creamakers.websystem.dao.ApkUpdateMapper;
import com.creamakers.websystem.domain.dto.ApkUpdate;
import com.creamakers.websystem.domain.vo.ResultVo;
import com.creamakers.websystem.domain.vo.response.ApkResp;
import com.creamakers.websystem.service.ApkService;
import com.creamakers.websystem.utils.HUAWEIOBSUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

import static com.creamakers.websystem.constants.CommonConst.VERSION_ERROR_MESSAGE;

@Service
public class ApkServiceImpl implements ApkService {
    @Autowired
    private ApkUpdateMapper apkUpdateMapper;

    @Override
    public ResultVo<ApkResp> updateApk(Integer versionCode, String versionName, String updateMessage, MultipartFile apkFile) throws IOException {
        LambdaQueryWrapper<ApkUpdate> wrapper = new LambdaQueryWrapper<>();
        // 排序字段为 versionCode，降序排列，取最新的记录
        wrapper.orderByDesc(ApkUpdate::getVersionCode).last("LIMIT 1");

        // 执行查询获取最新版本
        ApkUpdate latestApkUpdate = apkUpdateMapper.selectOne(wrapper);

        // 如果最新版本号大于等于传入的版本号，返回版本号错误
        if (latestApkUpdate != null && latestApkUpdate.getVersionCode() >= versionCode) {
            return ResultVo.fail(VERSION_ERROR_MESSAGE+" 最新版本号为 " + latestApkUpdate.getVersionCode());
        }

        String url = HUAWEIOBSUtil.uploadFile(apkFile, UUID.randomUUID().toString());

        ApkUpdate apkUpdate = new ApkUpdate();
        apkUpdate.setVersionCode(versionCode)
                .setVersionName(versionName)
                .setUpdateMessage(updateMessage)
                .setDownloadUrl(url)
                .setCreateTime(LocalDateTime.now())
                .setUpdateTime(LocalDateTime.now());
        apkUpdateMapper.insert(apkUpdate);

        ApkResp apkResp = new ApkResp();
        BeanUtils.copyProperties(apkUpdate, apkResp);
        apkResp.setDownloadUrl(url);
        if(latestApkUpdate != null) apkResp.setId((long) (latestApkUpdate.getId()+1));
        else apkResp.setId(1L);
        return ResultVo.success(apkResp);
    }


}
