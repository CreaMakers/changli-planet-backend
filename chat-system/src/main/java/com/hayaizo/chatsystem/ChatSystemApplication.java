package com.hayaizo.chatsystem;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
@MapperScan("com.hayaizo.chatsystem.mapper")
@ComponentScan(basePackages = {"com.hayaizo.chatsystem", "org.apache.rocketmq.spring"})
public class ChatSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChatSystemApplication.class, args);
    }

}