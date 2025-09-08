package com.lorecraft.tcglounge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@ComponentScan(basePackages = "com.lorecraft.tcglounge")
@EnableJpaAuditing
public class TcgLoungeApplication {

    public static void main(String[] args) {
        SpringApplication.run(TcgLoungeApplication.class, args);
    }
}