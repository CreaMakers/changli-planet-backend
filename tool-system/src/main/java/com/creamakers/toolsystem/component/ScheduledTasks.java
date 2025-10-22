package com.creamakers.toolsystem.component;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.creamakers.toolsystem.mapper.HomeWorkMapper;
import com.creamakers.toolsystem.po.HomeWork;
import com.creamakers.toolsystem.util.email.IEmailService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Logger;

@Component
public class ScheduledTasks implements SchedulingConfigurer {
    @Autowired
    private HomeWorkMapper homeWorkMapper;
    @Autowired
    private IEmailService emailService;
    @Autowired
    private TemplateEngine templateEngine;

    private static final Logger log = Logger.getLogger(ScheduledTasks.class.getName());
    public static final String EMAIL_SUBJECT = "作业过期提醒";
    private ThreadPoolTaskScheduler taskScheduler;

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setPoolSize(5);                         // 设置线程池大小为5
        threadPoolTaskScheduler.setThreadNamePrefix("homeReminder-");   // 线程池名称
        threadPoolTaskScheduler.initialize();
        taskRegistrar.setTaskScheduler(threadPoolTaskScheduler);
        this.taskScheduler = threadPoolTaskScheduler;
    }

    @Scheduled(cron = "0 0 3 * * ?") // 每天3点执行
    public void homeWorkRemind(){
        log.info(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))+" :Starting Home Work Remind");
        List<HomeWork> homeWorkList = getAllHomeWorks();
        for (HomeWork homeWork : homeWorkList){
            taskScheduler.execute(() -> reminder(homeWork));
        }
        log.info(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))+" :Finished Home Work Remind");
    }

    // 获取所有要提醒的作业
    public List<HomeWork> getAllHomeWorks(){
        log.info("select all remind home work");
        LocalDateTime time = LocalDateTime.now().plusDays(1);
        QueryWrapper<HomeWork> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status",0).eq("is_deleted",0).le("expire_time",time);
        return homeWorkMapper.selectList(queryWrapper);
    }

    // 发送作业过期提醒邮件
    public void reminder(HomeWork homeWork){
        String fromEmail = System.getenv("HOMEWORK_SEND_EMAIL_NAME");
        Context context = new Context();
        context.setVariable("homework", homeWork.getHomeWorkName());
        String content = templateEngine.process("template.html", context);
        emailService.sendHtml(fromEmail,homeWork.getMailbox(),EMAIL_SUBJECT,content);
    }
}
