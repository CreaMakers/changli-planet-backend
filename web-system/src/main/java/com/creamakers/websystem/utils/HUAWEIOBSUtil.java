package com.creamakers.websystem.utils;

import com.obs.services.ObsClient;
import com.obs.services.ObsConfiguration;
import com.obs.services.exception.ObsException;
import com.obs.services.model.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

import static com.obs.services.model.AccessControlList.REST_CANNED_PUBLIC_READ;

public class HUAWEIOBSUtil {

    private static final String AK = "";
    private static final String SK = "";
    private static final String ENDPOINT = "https://obs.cn-south-1.myhuaweicloud.com";
    private static final String BUCKET_NAME = "csustplant";

    // 创建 OBS 客户端
    private static ObsClient createObsClient() {
        ObsConfiguration configuration = new ObsConfiguration();
        configuration.setEndPoint(ENDPOINT);
        return new ObsClient(AK, SK, configuration);
    }

    // 校验文件扩展名是否合法
    public static boolean isValidApk(String extension) {
        return ".apk".equalsIgnoreCase(extension);
    }

    // 上传文件到 OBS
    public static String uploadFile(MultipartFile file, String custom) throws ObsException, IOException {
        String extension = getFileExtension(file.getOriginalFilename());
//        if (!isValidApk(extension)) {
//            throw new IllegalArgumentException("文件类型错误，请上传.apk结尾的文件");
//        }

        // 创建临时文件
        File tempFile = File.createTempFile(custom + "_apk_", extension);

        // 将 MultipartFile 内容写入临时文件
        file.transferTo(tempFile);

        // 上传到 OBS
        ObsClient obsClient = createObsClient();
        String fileName = "apkFiles/" + custom + extension;
        PutObjectRequest request = new PutObjectRequest(BUCKET_NAME, fileName, tempFile);
        PutObjectResult response = obsClient.putObject(request);

        // 设置文件为公开读取权限
        setFilePublicReadPermission(obsClient, fileName);
        if (tempFile.exists()) {
            tempFile.delete();
        }

        // 检查上传是否成功，并返回文件的公开 URL
        if (response != null && response.getStatusCode() == 200) {
            return "https://csustplant.obs.cn-south-1.myhuaweicloud.com/" + fileName;
        } else {
            throw new ObsException("Failed to upload APK file, status code: " + response.getStatusCode());
        }
    }

    // 设置文件为公开读取权限
    private static void setFilePublicReadPermission(ObsClient obsClient, String fileName) throws ObsException {
        // 创建 SetObjectAclRequest 请求对象
        SetObjectAclRequest aclRequest = new SetObjectAclRequest(BUCKET_NAME, fileName,REST_CANNED_PUBLIC_READ);
    }

    // 获取文件扩展名
    private static String getFileExtension(String filename) {
        int dotIndex = filename.lastIndexOf(".");
        if (dotIndex > 0) {
            return filename.substring(dotIndex);
        }
        return "";
    }
}
