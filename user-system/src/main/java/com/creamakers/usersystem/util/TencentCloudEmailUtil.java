package com.creamakers.usersystem.util;

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
    public String sendVerificationCodeEmail(String toEmail, String emailType) {
        // 验证码类型有效性检查
        if (emailType == null || (!EMAIL_TYPE_LOGIN.equals(emailType) && !EMAIL_TYPE_REGISTER.equals(emailType))) {
            logger.error("Invalid email verification code type: {}", emailType);
            throw new IllegalArgumentException("无效的验证码类型");
        }

        // 设置每日最大发送次数 - 根据不同类型设置不同的限制
        final int MAX_DAILY_LOGIN_ATTEMPTS = 10; // 登录验证码每天最多10次
        final int MAX_DAILY_REGISTER_ATTEMPTS = 5; // 注册验证码每天最多5次

        int maxAttempts = EMAIL_TYPE_LOGIN.equals(emailType) ?
                MAX_DAILY_LOGIN_ATTEMPTS : MAX_DAILY_REGISTER_ATTEMPTS;

        // 检查是否在60秒内重复发送
        if (!redisUtil.canSendVerificationCode(toEmail)) {
            throw new RuntimeException("请勿频繁发送验证码，请60秒后再试");
        }

        // 检查是否超过每日发送限制
        if (!redisUtil.checkDailyVerificationLimit(toEmail, maxAttempts, emailType)) {
            throw new RuntimeException("您今日的验证码请求次数已达上限，请明天再试");
        }

        // 生成验证码
        String verificationCode = generateVerificationCode();

        // 根据不同的验证码类型使用不同的存储方法
        if (EMAIL_TYPE_LOGIN.equals(emailType)) {
            redisUtil.storeLoginVerificationCode(toEmail, verificationCode);
            logger.info("Generated login verification code for email: {}", toEmail);
        } else if (EMAIL_TYPE_REGISTER.equals(emailType)) {
            redisUtil.storeRegisterVerificationCode(toEmail, verificationCode);
            logger.info("Generated register verification code for email: {}", toEmail);
        }

        // 发送验证码邮件
        try {
            // 根据不同类型使用不同的邮件主题
            String subject = EMAIL_TYPE_LOGIN.equals(emailType) ?
                    "长理星球登录验证码" : "长理星球注册验证码";

            sendEmail(toEmail, subject, verificationCode);
            return verificationCode;
        } catch (Exception e) {
            // 发送失败时删除验证码和限流标记
            if (EMAIL_TYPE_LOGIN.equals(emailType)) {
                redisUtil.deleteLoginVerificationCode(toEmail);
            } else if (EMAIL_TYPE_REGISTER.equals(emailType)) {
                redisUtil.deleteRegisterVerificationCode(toEmail);
            }

            redisUtil.removeRateLimit(toEmail);
            throw new RuntimeException("发送验证码邮件失败: " + e.getMessage(), e);
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