package com.creamakers.websystem.controller;

import com.creamakers.websystem.domain.vo.ResultVo;
import com.creamakers.websystem.domain.vo.response.ApkResp;
import com.creamakers.websystem.domain.vo.response.ChatGroupFileResp;
import com.creamakers.websystem.service.ApkService;
import com.creamakers.websystem.service.ChatGroupFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/web/files")
public class ChatGroupFileController {
    @Autowired
    private ChatGroupFileService chatGroupFileService;

    @Autowired
    private ApkService apkSerive;

    //获取所有的文件（可以按类型等搜索，如果为空就是所有文件）文件类型 1-图片, 2-文档, 3-视频, 4-音频, 5-其他
    @GetMapping
    public ResultVo<List<ChatGroupFileResp>> getFilesByFileType(@RequestParam(value = "fileType", required = false) Integer fileType,
                                                                @RequestParam(value = "page", defaultValue = "1", required = false) Integer page,
                                                                @RequestParam(value = "pageSize", defaultValue = "10", required = false) Integer pageSize) {
        return chatGroupFileService.getFilesByFileType(fileType, page, pageSize);
    }

    @DeleteMapping("/{fileId}")
    public ResultVo<Void> deleteFileById(@PathVariable("fileId") Long fileId) {
        return chatGroupFileService.deleteFileById(fileId);
    }

    @PostMapping("/apk")
    public ResultVo<ApkResp> updateApk(@RequestParam("versionCode") Integer versionCode,
                                       @RequestParam("versionName") String versionName,
                                       @RequestParam("updateMessage")String updateMessage,
                                       @RequestParam("apkFile")MultipartFile apkFile) throws IOException {
        return apkSerive.updateApk(versionCode,versionName,updateMessage,apkFile);
    }
}
