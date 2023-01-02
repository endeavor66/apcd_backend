package com.nju.apcd;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.nju.apcd.mapper")
public class ApcdApplication {
    public static void main(String[] args) {
        SpringApplication.run(ApcdApplication.class, args);
    }

}
