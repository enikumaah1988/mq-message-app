package com.example.frontend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class JmsService {
    private final JmsTemplate jmsTemplate;
    private final ObjectMapper objectMapper;

    public void sendMessage(String content) {
        try {
            Map<String, Object> message = new HashMap<>();
            message.put("action", "CREATE");
            
            Map<String, Object> data = new HashMap<>();
            data.put("content", content);
            message.put("data", data);

            String jsonMessage = objectMapper.writeValueAsString(message);
            jmsTemplate.convertAndSend("messageQueue", jsonMessage);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send message", e);
        }
    }
} 