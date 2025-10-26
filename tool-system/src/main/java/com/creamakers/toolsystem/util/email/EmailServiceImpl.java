package com.creamakers.toolsystem.util.email;


import cn.hutool.core.collection.CollectionUtil;
import jakarta.mail.internet.InternetAddress;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class EmailServiceImpl implements IEmailService{

    @Autowired
    private JavaMailSender javaMailSender;//注入邮件工具类

    @Override
    public void sendTxt(String fromEmail, String toEmail, String subject, String content) {
        send("长理星球", fromEmail, toEmail, subject, content, false, null, null, null);
    }

    @Override
    public void sendHtml(String form, String to, String subject, String content) {
        send("长理星球", form, to, subject, content, true, null, null, null);
    }

    @Override
    public void send(String sendName, String fromEmail, String toEmail, String subject, String content, Boolean isHtml, String cc, String bcc, List<File> files) {
        try {
            //true表示支持复杂类型
            MimeMessageHelper messageHelper = new MimeMessageHelper(javaMailSender.createMimeMessage(), true, "utf-8");
            //邮件发信人
            messageHelper.setFrom(new InternetAddress(sendName + "<" + fromEmail + ">"));
            //邮件收信人
            messageHelper.setTo(toEmail.split(","));
            //邮件主题
            messageHelper.setSubject(subject);
            //邮件内容
            messageHelper.setText(content, isHtml);
            //抄送
            if (!StringUtils.isEmpty(cc)) {
                messageHelper.setCc(cc.split(","));
            }
            //密送
            if (!StringUtils.isEmpty(bcc)) {
                messageHelper.setCc(bcc.split(","));
            }
            //添加邮件附件
            if (CollectionUtil.isNotEmpty(files)) {
                for (File file : files) {
                    messageHelper.addAttachment(file.getName(), file);
                }
            }
            // 邮件发送时间
            messageHelper.setSentDate(new Date());
            //正式发送邮件
            javaMailSender.send(messageHelper.getMimeMessage());
            log.info("发送邮件给{}成功", toEmail);
        }catch (Exception e){
            log.error("发送邮件失败", e);
        }
    }
}
