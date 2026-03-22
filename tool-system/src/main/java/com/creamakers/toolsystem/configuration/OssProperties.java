package com.creamakers.toolsystem.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 阿里云OSS配置属性类
 * 用于映射application.yml中的aliyun.oss配置参数
 */
@Data
@Component
@ConfigurationProperties(prefix = "aliyun.oss")
public class OssProperties {
    
    /**
     * 阿里云OSS的AccessKey ID
     */
    private String accessKey;
    
    /**
     * 阿里云OSS的SecretKey
     */
    private String secretKey;
    
    /**
     * OSS存储桶名称
     */
    private String bucketName;
    
    /**
     * 用于STS临时授权的角色ARN
     */
    private String roleArn;
    
    /**
     * OSS服务所在的地域，例如：oss-cn-hangzhou
     */
    private String region;
    
    /**
     * OSS服务的Endpoint地址，例如：oss-cn-hangzhou.aliyuncs.com
     */
    private String endpoint;
}
