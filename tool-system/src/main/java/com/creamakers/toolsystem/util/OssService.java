package com.creamakers.toolsystem.util;

import com.aliyun.oss.OSS;
import com.aliyun.oss.common.auth.CredentialsProvider;
import com.aliyun.oss.common.auth.DefaultCredentialProvider;
import com.aliyun.oss.model.GeneratePresignedUrlRequest;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.auth.sts.AssumeRoleRequest;
import com.aliyuncs.auth.sts.AssumeRoleResponse;
import com.aliyuncs.exceptions.ClientException;
import com.creamakers.toolsystem.configuration.OssProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.Date;

/**
 * 阿里云OSS服务工具类
 * 提供文件上传、删除、下载URL生成等功能
 */
@Slf4j
@Service
public class OssService {
    
    @Autowired
    private OSS ossClient;
    
    @Autowired
    private IAcsClient stsClient;
    
    @Autowired
    private OssProperties ossProperties;
    
    /**
     * 生成临时STS凭证，用于前端直传文件
     * 同时构造对象Key，格式为：pending/{userId}/{timestamp}_{filename}
     * 
     * @param userId 用户ID
     * @param filename 文件名
     * @return STS凭证信息
     * @throws ClientException STS客户端异常
     */
    public AssumeRoleResponse.Credentials generateUploadToken(Long userId, String filename) throws ClientException {
        log.info("生成上传临时凭证，userId: {}, filename: {}", userId, filename);
        
        // 构造对象Key
        String objectKey = buildObjectKey(userId, filename);
        log.info("构造的对象Key: {}", objectKey);
        
        // 创建AssumeRole请求
        AssumeRoleRequest request = new AssumeRoleRequest();
        request.setRoleArn(ossProperties.getRoleArn());
        request.setRoleSessionName("oss-upload-session-" + userId);
        request.setDurationSeconds(3600L); // 凭证有效期1小时
        
        // 设置权限策略，限制只能上传到指定路径
        String policy = String.format(
            "{\n" +
            "  \"Version\": \"1\",\n" +
            "  \"Statement\": [\n" +
            "    {\n" +
            "      \"Effect\": \"Allow\",\n" +
            "      \"Action\": [\n" +
            "        \"oss:PutObject\"\n" +
            "      ],\n" +
            "      \"Resource\": [\n" +
            "        \"acs:oss:*:*:%s/%s\"\n" +
            "      ]\n" +
            "    }\n" +
            "  ]\n" +
            "}",
            ossProperties.getBucketName(),
            objectKey
        );
        request.setPolicy(policy);
        
        // 发起STS请求
        AssumeRoleResponse response = stsClient.getAcsResponse(request);
        AssumeRoleResponse.Credentials credentials = response.getCredentials();
        
        log.info("临时凭证生成成功，过期时间: {}", credentials.getExpiration());
        return credentials;
    }
    
    /**
     * 构造对象Key
     * 格式：pending/{userId}/{timestamp}_{filename}
     * 
     * @param userId 用户ID
     * @param filename 文件名
     * @return 对象Key
     */
    private String buildObjectKey(Long userId, String filename) {
        long timestamp = System.currentTimeMillis();
        return String.format("pending/%d/%d_%s", userId, timestamp, filename);
    }
    
    /**
     * 删除OSS上的文件
     * 
     * @param objectKey 对象Key
     * @return 是否删除成功
     */
    public boolean deleteFile(String objectKey) {
        log.info("删除OSS文件，objectKey: {}", objectKey);
        
        try {
            ossClient.deleteObject(ossProperties.getBucketName(), objectKey);
            log.info("文件删除成功");
            return true;
        } catch (Exception e) {
            log.error("删除文件失败，objectKey: {}", objectKey, e);
            return false;
        }
    }
    
    /**
     * 生成带签名的下载URL
     * 用于正式资料的下载
     * 
     * @param objectKey 对象Key
     * @param expireSeconds 过期时间（秒）
     * @return 带签名的下载URL
     */
    public String generateDownloadUrl(String objectKey, Integer expireSeconds) {
        log.info("生成下载URL，objectKey: {}, expireSeconds: {}", objectKey, expireSeconds);
        
        // 设置过期时间
        Date expiration = new Date(System.currentTimeMillis() + expireSeconds * 1000L);
        
        // 生成带签名的URL
        URL url = ossClient.generatePresignedUrl(
            ossProperties.getBucketName(),
            objectKey,
            expiration
        );
        
        String signedUrl = url.toString();
        log.info("下载URL生成成功，URL: {}", signedUrl);
        
        return signedUrl;
    }
}
