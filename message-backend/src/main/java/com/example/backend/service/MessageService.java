package com.example.backend.service;

import com.example.backend.entity.Message;
import com.example.backend.repository.MessageRepository;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor
public class MessageService {
    private static final Logger logger = LoggerFactory.getLogger(MessageService.class);
    private final MessageRepository messageRepository;

    @JmsListener(destination = "messageQueue")
    public void receiveMessage(String content) {
        logger.info("Received message: {}", content);
        try {
            Message message = new Message();
            message.setContent(content);
            message.setCreatedAt(LocalDateTime.now());
            messageRepository.save(message);
            logger.info("Message saved successfully");
        } catch (Exception e) {
            logger.error("Error saving message: {}", e.getMessage(), e);
            throw e;
        }
    }
} 