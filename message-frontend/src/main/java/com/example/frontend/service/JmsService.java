package com.example.frontend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class JmsService {
    private static final Logger log = LoggerFactory.getLogger(JmsService.class);
    private final JmsTemplate jmsTemplate;
    private final ObjectMapper objectMapper;

    public void sendMessage(Map<String, Object> message) {
        try {
            log.info("メッセージをJSONに変換します: {}", message);
            String jsonMessage = objectMapper.writeValueAsString(message);
            log.info("メッセージをキューに送信します: {}", jsonMessage);
            jmsTemplate.convertAndSend("messageQueue", jsonMessage);
            log.info("メッセージの送信が完了しました");
        } catch (Exception e) {
            log.error("メッセージの送信に失敗しました: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to send message", e);
        }
    }
} 