package com.creamakers.toolsystem.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.creamakers.toolsystem.dto.response.GeneralResponse;
import com.creamakers.toolsystem.dto.response.TopicSkinResponse;
import com.creamakers.toolsystem.mapper.TopicSkinMapper;
import com.creamakers.toolsystem.po.TopicSkin;
import com.creamakers.toolsystem.util.HUAWEIOBSUtil;
import com.obs.services.exception.ObsException;
import io.swagger.models.auth.In;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TopicSkinService {

    private final String path = "/usr/www/changli-planet-backend/skin/";
    private final Logger logger = LoggerFactory.getLogger(TopicSkinService.class);
    @Autowired
    private TopicSkinMapper topicSkinMapper;

    //上传主题皮肤资源
    public ResponseEntity<GeneralResponse<TopicSkinResponse>> uploadSkin(MultipartFile file, MultipartFile image, String name, String description) {
        //获取文件名并插入数据库
        String filePath = path + name + ".apk";
        TopicSkin topicSkin = new TopicSkin().setName(name).setPath(filePath).setDescription(description);

        try {
            //上传主题皮肤图片到OBS
            String imageUrl = HUAWEIOBSUtil.uploadAvatar(image, UUID.randomUUID().toString());
            topicSkin.setImageUrl(imageUrl);
        } catch (IOException | ObsException e) {
            logger.error("主题皮肤图片上传失败", e);
            return ResponseEntity.badRequest().body(new GeneralResponse<>("400", "上传失败",null));
        }

        try {
            //计算文件的md5值,用于文件完整性检测
            String md5 = DigestUtil.md5Hex(file.getInputStream());
            topicSkin.setHashMd5(md5);
            topicSkinMapper.insert(topicSkin);

            //将文件写入服务器指定路径
            byte[] bytes = file.getBytes();
            FileOutputStream fos = new FileOutputStream(filePath);
            fos.write(bytes);
            fos.close();

            //返回成功响应
            TopicSkinResponse respond = BeanUtil.copyProperties(topicSkin, TopicSkinResponse.class);
            return ResponseEntity
                    .ok(new GeneralResponse<>("200", "上传成功", respond));
        }catch (IOException e){
            logger.error("主题皮肤资源上传失败或者数据库插入失败", e);

            //删除已写入的文件
            File newFile = new File(filePath);
            if(newFile.exists()) {
                boolean deleted = newFile.delete();
                if (deleted) logger.info("资源文件删除成功");
                else logger.warn("资源文件删除失败");
            }

            //删除数据库记录
            topicSkinMapper.deleteById(topicSkin);
            //返回失败响应
            return ResponseEntity.badRequest().body(new GeneralResponse<>("400", "上传失败",null));
        }
    }

    //根据id或者name删除主题皮肤资源
    public ResponseEntity<GeneralResponse> deleteSkin(Integer id, String name) {
        logger.info("根据id-{} 或者name-{} 删除主题皮肤资源", id, name);

        //根据id或者name查询主题皮肤资源
        TopicSkin topicSkin = null;
        if(id != null){
            topicSkin = topicSkinMapper.selectById(id);
        }else {
            QueryWrapper<TopicSkin> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("name",name);
            topicSkin = topicSkinMapper.selectOne(queryWrapper);
        }
        if(topicSkin == null){
            logger.info("根据id-{} 或者name-{} 查询主题皮肤资源不存在", id, name);
            return ResponseEntity.badRequest().body(new GeneralResponse<>("400", "资源不存在",null));
        }

        //删除文件
        String filePath = topicSkin.getPath();
        File newFile = new File(filePath);
        if(!newFile.exists()) {
            logger.info("{} 主题皮肤资源不存在", filePath);
            return ResponseEntity.badRequest().body(new GeneralResponse<>("400", "资源不存在",null));
        }
        boolean deleted = newFile.delete();
        if(!deleted){
            logger.warn("资源文件删除失败");
            return ResponseEntity.badRequest().body(new GeneralResponse<>("400", "资源文件删除失败",null));
        }

        //删除数据库记录
        topicSkinMapper.deleteById(topicSkin);
        return ResponseEntity
                .ok(new GeneralResponse<>("200", "删除成功", null));
    }

    //根据name查询主题皮肤资源
    public ResponseEntity<?> getSkin(Integer id, String name) {
        logger.info("根据id-{} 或者name-{} 查询主题皮肤资源", id, name);

        TopicSkin topicSkin = null;
        //优先根据ID查询
        if(id != null){
            //根据id查询主题皮肤资源
            topicSkin = topicSkinMapper.selectById(id);
        }else {
            //根据name查询主题皮肤资源
            QueryWrapper<TopicSkin> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("name",name);
            topicSkin = topicSkinMapper.selectOne(queryWrapper);
        }

        if(topicSkin == null){
            logger.info("根据name-{} 查询主题皮肤资源不存在", name);
            return ResponseEntity.badRequest()
                    .body(new GeneralResponse<>("400", "资源不存在",null));
        }

        String filePath = topicSkin.getPath();
        File file = new File(filePath);
        if(!file.exists()){
            logger.info("{} 主题皮肤资源不存在", filePath);
            return ResponseEntity.badRequest()
                    .body(new GeneralResponse<>("400", "资源不存在",null));
        }

        try {
            FileSystemResource resource = new FileSystemResource(file);
            String fileName = file.getName();
            // 获取文件名并编码，确保文件名正常显示
            String encodedFileName = URLEncoder.encode(fileName, "UTF-8");

            // 设置响应头，确保浏览器直接下载文件
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedFileName);
            headers.add(HttpHeaders.CONTENT_TYPE, "application/octet-stream");
            headers.add(HttpHeaders.CONTENT_LENGTH, String.valueOf(file.length()));
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(resource);
        } catch (UnsupportedEncodingException e) {
            logger.error("获取主题皮肤资源失败{}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    //分页查询所有主题皮肤资源
    public ResponseEntity<GeneralResponse<List<TopicSkinResponse>>> getAllSkin(Integer page, Integer pageSize) {
        logger.info("查询所有主题皮肤资源，分页参数：page={}, pageSize={}", page, pageSize);

        // 分页查询
        Page<TopicSkin> topicSkinPage = new Page<>(page, pageSize);
        topicSkinMapper.selectPage(topicSkinPage, null);

        // 转换为响应体
        List<TopicSkinResponse> topicSkinResponses = topicSkinPage.getRecords()
                .stream()
                .map(topicSkin -> {
                    TopicSkinResponse response = new TopicSkinResponse();
                    BeanUtil.copyProperties(topicSkin,response);
                    return response;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(new GeneralResponse<>("200", "查询成功", topicSkinResponses));
    }
}
