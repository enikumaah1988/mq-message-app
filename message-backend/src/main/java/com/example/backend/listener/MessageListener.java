package com.example.backend.listener;

import com.example.backend.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MessageListener {

    private final MessageService messageService;

    @JmsListener(destination = "messageQueue")
    public void receiveMessage(String message) {
        try {
            log.info("=== メッセージ受信開始 ===");
            log.info("メッセージを受信しました: {}", message);
            messageService.processMessage(message);
            log.info("=== メッセージ受信完了 ===");
        } catch (Exception e) {
            log.error("メッセージ処理中にエラーが発生しました: {}", e.getMessage(), e);
            throw e; // JMSの再試行メカニズムを活用するため、例外を再スロー
        }
    }
} 