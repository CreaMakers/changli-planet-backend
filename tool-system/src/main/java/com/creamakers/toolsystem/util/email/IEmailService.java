package com.creamakers.toolsystem.util.email;

import java.io.File;
import java.util.List;

public interface IEmailService {
    /**
     * 发送文本邮件
     * @param fromEmail     发送者邮箱
     * @param toEmail       接收者邮箱
     * @param subject       邮件主题
     * @param content       邮件内容
     */
    public void sendTxt(String fromEmail, String toEmail, String subject, String content);

    /**
     * Html邮件发送
     *
     * @param form    发送人
     * @param to      发送对象
     * @param subject 主题
     * @param content 内容
     */
    void sendHtml(String form, String to, String subject, String content);

    /**
     * 邮件发送
     *
     * @param sendName          发送人名称
     * @param fromEmail     发送人
     * @param toEmail       发送对象
     * @param subject       主题
     * @param content       内容
     * @param isHtml        是否为html
     * @param cc            抄送，多人用逗号隔开
     * @param bcc           密送，多人用逗号隔开
     * @param files         文件
     */
    void send(String sendName, String fromEmail, String toEmail, String subject, String content, Boolean isHtml, String cc, String bcc, List<File> files);

}
