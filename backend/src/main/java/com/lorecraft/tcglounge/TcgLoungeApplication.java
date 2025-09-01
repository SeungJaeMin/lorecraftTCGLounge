package com.lorecraft.tcglounge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@ComponentScan(basePackages = "com.lorecraft.tcglounge")
@EnableCaching
@EnableAsync
public class TcgLoungeApplication {

    public static void main(String[] args) {
        SpringApplication.run(TcgLoungeApplication.class, args);
    }
}