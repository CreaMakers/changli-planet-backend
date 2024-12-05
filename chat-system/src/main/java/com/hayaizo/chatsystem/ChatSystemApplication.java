package com.hayaizo.chatsystem;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@MapperScan("com.hayaizo.chatsystem.mapper")
public class ChatSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChatSystemApplication.class, args);
    }

}
