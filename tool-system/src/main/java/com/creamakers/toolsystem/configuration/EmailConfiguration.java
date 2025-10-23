package com.creamakers.toolsystem.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.web.context.annotation.ApplicationScope;

import java.util.Properties;

@Configuration

// 配置JavaMailSender 邮件工具类
public class EmailConfiguration {
    @Value("${spring.mail.host}")
    private String host;

    @Value("${spring.mail.port}")
    private int port;

    private String username;

    private String password;

    @Value("${spring.mail.properties.mail.smtp.auth}")
    private boolean auth;

    @Value("${spring.mail.properties.mail.smtp.ssl.enable}")
    private boolean startSSL;
    @Bean
    public JavaMailSender javaMailSender() {
        // 从环境变量中获取发送邮件的用户名和密码
        username = System.getenv("HOMEWORK_SEND_EMAIL_NAME");
        password = System.getenv("HOMEWORK_SEND_EMAIL_PASSWORD");
        if (username == null || password == null) {
            throw new RuntimeException("未从环境变量中获取到发送邮件的用户名或密码");
        }

        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        // 设置邮件服务器信息
        mailSender.setHost(host);
        mailSender.setPort(port);
        mailSender.setUsername(username);
        mailSender.setPassword(password);
        mailSender.setProtocol("smtps");     //设置协议为smtps

        // 设置JavaMail属性
        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.smtp.auth", auth);
        props.put("mail.smtp.ssl.enable", startSSL);
        props.put("mail.smtp.ssl.socketFactoryClass", "javax.net.ssl.SSLSocketFactory"); // SSL工厂类
        props.put("mail.smtp.ssl.socketFactoryPort", port); // 绑定SSL端口（与setPort保持一致）
        props.put("mail.debug", "false"); // 调试模式

        return mailSender;
    }
}
