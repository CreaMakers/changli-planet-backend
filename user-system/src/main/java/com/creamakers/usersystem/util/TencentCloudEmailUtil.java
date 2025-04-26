package com.creamakers.usersystem.util;

import com.creamakers.usersystem.consts.Config;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.ses.v20201002.SesClient;
import com.tencentcloudapi.ses.v20201002.models.SendEmailRequest;
import com.tencentcloudapi.ses.v20201002.models.SendEmailResponse;
import com.tencentcloudapi.ses.v20201002.models.Template;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static com.creamakers.usersystem.consts.Config.EMAIL_TYPE_LOGIN;
import static com.creamakers.usersystem.consts.Config.EMAIL_TYPE_REGISTER;

/**
 * 腾讯云SES邮件工具类 - 用于发送验证码邮件和验证
 */
@Component
public class TencentCloudEmailUtil {
    private static final String REGION = "ap-guangzhou"; // 地域
    private static final String EMAIL_FROM = "创想精工 <job@creamaker.cn>"; // 发件人
    private static final String EMAIL_REPLY_TO = "job@creamaker.cn"; // 回复地址
    private static final long TEMPLATE_ID = 31527L; // 邮件模板ID
    private static final Logger logger = LoggerFactory.getLogger(TencentCloudEmailUtil.class);

    @Autowired
    private RedisUtil redisUtil;
    /**
     * 生成6位随机验证码
     *
     * @return 6位数字验证码
     */
    public String generateVerificationCode() {
        Random random = new Random();
        int code = 1000 + random.nextInt(9000); // 生成1000-9999之间的随机数
        return String.valueOf(code);
    }

    /**
     * 发送验证码邮件
     *
     * @param toEmail        收件人邮箱
     * @param emailType
     * @return 返回生成的验证码
     */
    /**
     * 发送邮箱验证码
     *
     * @param toEmail 接收邮箱
     * @param emailType 邮件类型（如登录、注册、更新邮箱等）
     * @return 生成的验证码
     */
    public String sendVerificationCodeEmail(String toEmail, String emailType) {
        // 验证邮件类型
        validateEmailType(emailType);

        // 获取当前类型的发送限制配置
        EmailLimitConfig limitConfig = getEmailLimitConfig(emailType);

        // 检查发送频率限制
        checkRateLimit(toEmail);

        // 检查每日发送次数限制
        checkDailyLimit(toEmail, limitConfig.maxDailyAttempts, emailType);

        // 生成验证码
        String verificationCode = generateVerificationCode();

        try {
            // 存储验证码
            storeVerificationCode(toEmail, verificationCode, emailType);

            // 获取对应的邮件主题
            String subject = getEmailSubject(emailType);

            // 发送邮件
            sendEmail(toEmail, subject, verificationCode);
            return verificationCode;
        } catch (Exception e) {
            // 发送失败时清理验证码和限流标记
            cleanupFailedAttempt(toEmail, emailType);
            throw new RuntimeException("发送验证码邮件失败: " + e.getMessage(), e);
        }
    }

    // 验证邮件类型
    private void validateEmailType(String emailType) {
        // 扩展支持的类型列表
        List<String> validTypes = Arrays.asList(
                Config.EMAIL_TYPE_LOGIN,
                Config.EMAIL_TYPE_REGISTER,
                Config.EMAIL_TYPE_UPDATE_EMAIL,
                Config.EMAIL_TYPE_RESET_PASSWORD
        );

        if (emailType == null || !validTypes.contains(emailType)) {
            logger.error("Invalid email verification code type: {}", emailType);
            throw new IllegalArgumentException("无效的验证码类型");
        }
    }

    // 获取邮件类型对应的限制配置
    private EmailLimitConfig getEmailLimitConfig(String emailType) {
        switch (emailType) {
            case Config.EMAIL_TYPE_LOGIN:
                return new EmailLimitConfig(10); // 登录每天10次
            case Config.EMAIL_TYPE_REGISTER:
                return new EmailLimitConfig(5);  // 注册每天5次
            case Config.EMAIL_TYPE_UPDATE_EMAIL:
                return new EmailLimitConfig(3);  // 更新邮箱每天3次
            case Config.EMAIL_TYPE_RESET_PASSWORD:
                return new EmailLimitConfig(3);  // 重置密码每天3次
            default:
                return new EmailLimitConfig(5);  // 默认每天5次
        }
    }

    // 检查发送频率限制
    private void checkRateLimit(String toEmail) {
        if (!redisUtil.canSendVerificationCode(toEmail)) {
            throw new RuntimeException("请勿频繁发送验证码，请60秒后再试");
        }
    }

    // 检查每日发送次数限制
    private void checkDailyLimit(String toEmail, int maxAttempts, String emailType) {
        if (!redisUtil.checkDailyVerificationLimit(toEmail, maxAttempts, emailType)) {
            throw new RuntimeException("您今日的验证码请求次数已达上限，请明天再试");
        }
    }

    // 存储验证码
    private void storeVerificationCode(String toEmail, String verificationCode, String emailType) {
        switch (emailType) {
            case Config.EMAIL_TYPE_LOGIN:
                redisUtil.storeLoginVerificationCode(toEmail, verificationCode);
                break;
            case Config.EMAIL_TYPE_REGISTER:
                redisUtil.storeRegisterVerificationCode(toEmail, verificationCode);
                break;
            case Config.EMAIL_TYPE_UPDATE_EMAIL:
                redisUtil.storeUpdateEmailVerificationCode(toEmail, verificationCode);
                break;
            case Config.EMAIL_TYPE_RESET_PASSWORD:
                redisUtil.storeResetPasswordVerificationCode(toEmail, verificationCode);
                break;
        }
        logger.info("Generated {} verification code for email: {}", emailType, toEmail);
    }

    // 获取邮件主题
    private String getEmailSubject(String emailType) {
        switch (emailType) {
            case Config.EMAIL_TYPE_LOGIN:
                return "长理星球登录验证码";
            case Config.EMAIL_TYPE_REGISTER:
                return "长理星球注册验证码";
            case Config.EMAIL_TYPE_UPDATE_EMAIL:
                return "长理星球更改邮箱验证码";
            case Config.EMAIL_TYPE_RESET_PASSWORD:
                return "长理星球重置密码验证码";
            default:
                return "长理星球验证码";
        }
    }

    // 清理失败的验证码和限流标记
    private void cleanupFailedAttempt(String toEmail, String emailType) {
        switch (emailType) {
            case Config.EMAIL_TYPE_LOGIN:
                redisUtil.deleteLoginVerificationCode(toEmail);
                break;
            case Config.EMAIL_TYPE_REGISTER:
                redisUtil.deleteRegisterVerificationCode(toEmail);
                break;
            case Config.EMAIL_TYPE_UPDATE_EMAIL:
                redisUtil.deleteUpdateEmailVerificationCode(toEmail);
                break;
            case Config.EMAIL_TYPE_RESET_PASSWORD:
                redisUtil.deleteResetPasswordVerificationCode(toEmail);
                break;
        }
        redisUtil.removeRateLimit(toEmail);
    }

    // 内部配置类，用于存储每种邮件类型的限制
    private static class EmailLimitConfig {
        final int maxDailyAttempts;

        EmailLimitConfig(int maxDailyAttempts) {
            this.maxDailyAttempts = maxDailyAttempts;
        }
    }
    /**
     * 验证用户提交的验证码是否正确
     *
     * @param email     邮箱
     * @param code      用户提交的验证码
     * @param emailType 验证码类型（登录或注册）
     * @return 验证结果，true为验证通过，false为验证失败
     */
    public boolean verifyCode(String email, String code, String emailType) {
        String storedCode;

        // 根据验证码类型获取存储的验证码
        if (EMAIL_TYPE_LOGIN.equals(emailType)) {
            storedCode = redisUtil.getLoginVerificationCode(email);
        } else if (EMAIL_TYPE_REGISTER.equals(emailType)) {
            storedCode = redisUtil.getRegisterVerificationCode(email);
        } else {
            logger.error("Invalid verification code type: {}", emailType);
            return false;
        }

        // 验证验证码
        if (storedCode == null || !storedCode.equals(code)) {
            logger.warn("Verification failed for email: {} with code type: {}", email, emailType);
            return false;
        }

        // 验证成功后删除验证码
        if (EMAIL_TYPE_LOGIN.equals(emailType)) {
            redisUtil.deleteLoginVerificationCode(email);
        } else if (EMAIL_TYPE_REGISTER.equals(emailType)) {
            redisUtil.deleteRegisterVerificationCode(email);
        }

        logger.info("Verification successful for email: {} with code type: {}", email, emailType);
        return true;
    }

    /**
     * 发送邮件
     *
     * @param toEmail           收件人邮箱
     * @param subject           邮件主题
     * @param verificationCode  验证码
     */
    private void sendEmail(String toEmail, String subject, String verificationCode) {
        try {
            // 从环境变量获取SecretId和SecretKey
            String secretId = System.getenv("TENCENTYUN_SECRET_ID");
            String secretKey = System.getenv("TENCENTYUN_SECRET_KEY");

            // 检查是否成功获取到密钥
            if (secretId == null || secretKey == null) {
                throw new RuntimeException("未能从环境变量获取到TENCENTYUN_SECRET_ID或TENCENTYUN_SECRET_KEY");
            }

            // 实例化认证对象
            Credential cred = new Credential(secretId, secretKey);

            // 实例化http选项
            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setEndpoint("ses.tencentcloudapi.com");

            // 实例化client选项
            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);

            // 实例化SES客户端
            SesClient client = new SesClient(cred, REGION, clientProfile);

            // 构建邮件请求
            SendEmailRequest req = new SendEmailRequest();
            req.setFromEmailAddress(EMAIL_FROM);

            String[] destination = {toEmail};
            req.setDestination(destination);

            req.setSubject(subject);
            req.setReplyToAddresses(EMAIL_REPLY_TO);

            // 设置模板
            Template template = new Template();
            template.setTemplateID(TEMPLATE_ID);
            template.setTemplateData("{\"verification_code\":\"" + verificationCode + "\"}");
            req.setTemplate(template);

            // 发送请求
            SendEmailResponse resp = client.SendEmail(req);

            // 可以记录响应信息
            System.out.println("邮件发送成功: " + resp);

        } catch (TencentCloudSDKException e) {
            throw new RuntimeException("发送邮件失败: " + e.getMessage(), e);
        }
    }
}