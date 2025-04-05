package com.example.backend.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MessageListener {

    @JmsListener(destination = "messageQueue")
    public void receiveMessage(String message) {
        log.info("Received message: {}", message);
    }
} 