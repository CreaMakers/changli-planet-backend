package com.creamakers.fresh.system.utils;

import com.obs.services.ObsClient;
import com.obs.services.ObsConfiguration;
import com.obs.services.exception.ObsException;
import com.obs.services.model.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

import static com.obs.services.model.AccessControlList.REST_CANNED_PUBLIC_READ;

public class HUAWEIOBSUtil {

    private static final String AK = System.getenv("HUAWEIYUN_OBS_ACCESS_KEY_ID");
    private static final String SK = System.getenv("HUAWEIYUN_OBS_SECRET_ACCESS_KEY_ID");
    private static final String ENDPOINT = "https://obs.cn-south-1.myhuaweicloud.com";
    private static final String BUCKET_NAME = "csustplant";

    // 创建 OBS 客户端

    private static ObsClient createObsClient() {
        ObsConfiguration configuration = new ObsConfiguration();
        configuration.setEndPoint(ENDPOINT);
        return new ObsClient(AK, SK, configuration);
    }



    // 校验图片扩展名是否合法
    public static boolean isValidImageExtension(String extension) {
        String[] validExtensions = {".jpg", ".jpeg", ".png", ".gif", ".bmp"};
        for (String validExtension : validExtensions) {
            if (extension.equalsIgnoreCase(validExtension)) {
                return true;
            }
        }
        return false;
    }
    

    // 上传文件到 OBS 并设置为公开
    public static String uploadImage(MultipartFile image, String username) throws ObsException, IOException {
        if (image == null || image.isEmpty()) {
            return null;
        }
        ObsClient obsClient = createObsClient();
        File tempFile = File.createTempFile(username + "_freshNewsImage_", ".png");
        image.transferTo(tempFile);

        String fileName = "freshNewsImage/" + username + ".png";
        PutObjectRequest request = new PutObjectRequest(BUCKET_NAME, fileName, tempFile);
        PutObjectResult response = obsClient.putObject(request);

        // 设置文件为公开读取权限
        setFilePublicReadPermission(obsClient, fileName);

        // 清理临时文件
        if (tempFile.exists()) {
            tempFile.delete();
        }

        if (response != null && response.getStatusCode() == 200) {
            // 返回公开的 URL
            return "https://csustplant.obs.cn-south-1.myhuaweicloud.com/" + fileName;
        } else {
            throw new ObsException("Failed to upload fresh news image, status code: " + response.getStatusCode());
        }
    }

    // 设置文件为公开读取权限
    private static void setFilePublicReadPermission(ObsClient obsClient, String fileName) throws ObsException {
        // 创建 SetObjectAclRequest 请求对象
        SetObjectAclRequest aclRequest = new SetObjectAclRequest(BUCKET_NAME, fileName, REST_CANNED_PUBLIC_READ);
    }


    // 生成临时签名 URL
    public static String generateTemporaryUrl(String fileName, long expireSeconds) throws ObsException {
        ObsClient obsClient = createObsClient();
        TemporarySignatureRequest temporarySignatureRequest = new TemporarySignatureRequest(HttpMethodEnum.GET, expireSeconds);
        temporarySignatureRequest.setBucketName(BUCKET_NAME);
        temporarySignatureRequest.setObjectKey(fileName);

        TemporarySignatureResponse response = obsClient.createTemporarySignature(temporarySignatureRequest);
        return response.getSignedUrl();
    }
}
