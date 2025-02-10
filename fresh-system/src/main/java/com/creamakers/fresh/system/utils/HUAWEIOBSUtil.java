package com.creamakers.fresh.system.utils;

import com.obs.services.ObsClient;
import com.obs.services.ObsConfiguration;
import com.obs.services.exception.ObsException;
import com.obs.services.model.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

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

    // 上传文件到 OBS
    public static String uploadAvatar(MultipartFile avatar, String username) throws ObsException, IOException {
        ObsClient obsClient = createObsClient();
        File tempFile = File.createTempFile(username + "_avatar_", ".png");
        avatar.transferTo(tempFile);

        String fileName = "userAvatar/" + username + ".png";
        PutObjectRequest request = new PutObjectRequest(BUCKET_NAME, fileName, tempFile);
        PutObjectResult response = obsClient.putObject(request);

        // 清理临时文件
        if (tempFile.exists()) {
            tempFile.delete();
        }

        if (response != null && response.getStatusCode() == 200) {
            return "https://csustplant.obs.cn-south-1.myhuaweicloud.com/" + fileName;
        } else {
            throw new ObsException("Failed to upload avatar, status code: " + response.getStatusCode());
        }
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
