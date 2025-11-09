package com.project.messanger;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.project.messanger.mapper")
public class MessangerApplication {

	public static void main(String[] args) {
		SpringApplication.run(MessangerApplication.class, args);
	}

}
