package com.creamakers.usersystem;



import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
@MapperScan("com.creamakers.usersystem.mapper")
public class UserSystemApplication {


	public static void main(String[] args) {
		SpringApplication.run(UserSystemApplication.class, args);
	}
}
