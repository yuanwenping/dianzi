package com.xinji;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.xinji.mapper")
public class XinjiApplication {
    public static void main(String[] args) {
        SpringApplication.run(XinjiApplication.class, args);
        System.out.println("========================================");
        System.out.println("  芯迹 Xinji Backend Started on :8088");
        System.out.println("========================================");
    }
}
