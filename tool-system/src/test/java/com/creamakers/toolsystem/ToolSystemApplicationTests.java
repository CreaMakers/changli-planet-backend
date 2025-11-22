package com.creamakers.toolsystem;

import com.creamakers.toolsystem.util.HUAWEIOBSUtil;
import com.creamakers.toolsystem.util.email.IEmailService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.*;

@SpringBootTest
@ContextConfiguration(classes = {HUAWEIOBSUtil.class})
class ToolSystemApplicationTests {

    @Autowired
    private IEmailService emailService;
    @Autowired
    private TemplateEngine templateEngine;

    @Test
	void contextLoads() {
	}

    @Test
    void testSendEmail() {
        emailService.sendHtml("creamker@163.com", "2898791337@qq.com", "作业过期提醒", "大学物理");
    }

    @Test
    void testSendEmail2() {
        Context context = new Context();
        context.setVariable("homework", "大学物理");
        String content = templateEngine.process("template.html", context);
        emailService.sendHtml("creamker@163.com", "2898791337@qq.com", "作业过期提醒", content);
    }

    @Test
    public void uploadImage() throws IOException {
        String path = "D:\\Soft\\QQ\\QQ文件\\dark.png";
        File file = new File(path);
        InputStream inputStream = new FileInputStream(file);
        MultipartFile multipartFile = new MockMultipartFile("dark.png",inputStream);
        String url = HUAWEIOBSUtil.uploadAvatar(multipartFile, "dark");
        System.out.println(url);
    }
}
