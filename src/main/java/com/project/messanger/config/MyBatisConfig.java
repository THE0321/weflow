package com.project.messanger.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.project.messanger.mapper")
public class MyBatisConfig {
}
