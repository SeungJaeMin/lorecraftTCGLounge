package com.lorecraft.tcglounge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class TcgLoungeApplication {

    public static void main(String[] args) {
        SpringApplication.run(TcgLoungeApplication.class, args);
    }
}