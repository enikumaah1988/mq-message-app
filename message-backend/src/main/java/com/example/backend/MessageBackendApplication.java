package com.example.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jms.annotation.EnableJms;

@SpringBootApplication
@EnableJms
public class MessageBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(MessageBackendApplication.class, args);
    }
} 