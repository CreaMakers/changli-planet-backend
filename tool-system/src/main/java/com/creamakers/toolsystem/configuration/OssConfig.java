package com.creamakers.toolsystem.configuration;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.profile.DefaultProfile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 阿里云OSS配置类
 * 负责创建和管理OSSClient和StsClient的实例
 */
@Slf4j
@Configuration
public class OssConfig {
    
    @Autowired
    private OssProperties ossProperties;
    
    /**
     * 创建OSSClient Bean实例
     * 用于执行OSS文件操作（上传、下载、删除等）
     * 
     * @return OSS客户端实例
     */
    @Bean
    public OSS ossClient() {
        log.info("初始化阿里云OSS客户端，endpoint: {}, bucket: {}", 
                ossProperties.getEndpoint(), ossProperties.getBucketName());
        
        // 创建OSSClient实例
        OSS ossClient = new OSSClientBuilder().build(
            ossProperties.getEndpoint(),
            ossProperties.getAccessKey(),
            ossProperties.getSecretKey()
        );
        
        log.info("阿里云OSS客户端初始化成功");
        return ossClient;
    }
    
    /**
     * 创建STS客户端Bean实例
     * 用于生成临时访问凭证
     * 
     * @return STS客户端实例
     */
    @Bean
    public IAcsClient stsClient() {
        log.info("初始化阿里云STS客户端，region: {}", ossProperties.getRegion());
        
        // 创建DefaultProfile实例
        DefaultProfile profile = DefaultProfile.getProfile(
            ossProperties.getRegion(),
            ossProperties.getAccessKey(),
            ossProperties.getSecretKey()
        );

        // 手动设置 STS 服务的 Endpoint
        // STS 的 Endpoint 格式为：sts.{region}.aliyuncs.com
        String stsEndpoint = "sts." + ossProperties.getRegion() + ".aliyuncs.com";
        log.info("设置 STS Endpoint: {}", stsEndpoint);
        DefaultProfile.addEndpoint(ossProperties.getRegion(), "Sts", stsEndpoint);

        // 创建STS客户端
        IAcsClient stsClient = new DefaultAcsClient(profile);
        
        log.info("阿里云STS客户端初始化成功");
        return stsClient;
    }
}
