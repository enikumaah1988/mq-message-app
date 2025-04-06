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
        log.info("=== メッセージ送信開始 ===");
        log.debug("送信メッセージの内容: {}", message);
        try {
            log.debug("JMSTemplate設定: destination={}, deliveryMode={}, priority={}, timeToLive={}",
                jmsTemplate.getDefaultDestinationName(),
                jmsTemplate.getDeliveryMode(),
                jmsTemplate.getPriority(),
                jmsTemplate.getTimeToLive());

            log.info("メッセージをJSONに変換します: {}", message);
            String jsonMessage = objectMapper.writeValueAsString(message);
            log.debug("変換後のJSON: {}", jsonMessage);
            
            log.info("メッセージをキューに送信します");
            jmsTemplate.convertAndSend("messageQueue", jsonMessage);
            log.info("メッセージの送信が完了しました");
        } catch (Exception e) {
            log.error("メッセージの送信に失敗しました: {}", e.getMessage());
            log.error("スタックトレース: ", e);
            throw new RuntimeException("メッセージの送信に失敗しました", e);
        }
        log.info("=== メッセージ送信完了 ===");
    }
} 