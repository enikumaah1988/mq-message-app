package com.example.backend.service;

import com.example.backend.entity.Message;
import com.example.backend.repository.MessageRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;
import lombok.RequiredArgsConstructor;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MessageService {
    private static final Logger logger = LoggerFactory.getLogger(MessageService.class);
    private final MessageRepository messageRepository;
    private final ObjectMapper objectMapper;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void processMessage(String jsonMessage) {
        logger.info("=== メッセージ処理開始 ===");
        logger.debug("受信したメッセージ: {}", jsonMessage);
        logger.debug("現在のトランザクション: {}", org.springframework.transaction.support.TransactionSynchronizationManager.getCurrentTransactionName());
        
        try {
            Map<String, Object> messageMap = objectMapper.readValue(jsonMessage, Map.class);
            String action = (String) messageMap.get("action");
            Map<String, Object> data = (Map<String, Object>) messageMap.get("data");
            
            logger.info("アクション: {}, データ: {}", action, data);
            logger.debug("トランザクションアクティブ: {}", org.springframework.transaction.support.TransactionSynchronizationManager.isActualTransactionActive());

            switch (action) {
                case "CREATE":
                    handleCreate((String) data.get("content"));
                    break;
                case "UPDATE":
                    handleUpdate(Long.valueOf(data.get("id").toString()), (String) data.get("content"));
                    break;
                case "DELETE":
                    handleDelete(Long.valueOf(data.get("id").toString()));
                    break;
                default:
                    logger.warn("不明なアクション: {}", action);
                    break;
            }
            logger.info("=== メッセージ処理完了 ===");
        } catch (Exception e) {
            logger.error("メッセージ処理エラー: {}", e.getMessage(), e);
            logger.error("スタックトレース: ", e);
            throw new RuntimeException("メッセージ処理に失敗しました", e);
        }
    }

    @Transactional(propagation = Propagation.MANDATORY)
    protected void handleCreate(String content) {
        logger.info("=== CREATE処理開始 ===");
        logger.debug("トランザクションアクティブ: {}", org.springframework.transaction.support.TransactionSynchronizationManager.isActualTransactionActive());
        try {
            logger.debug("新規メッセージ作成: {}", content);
            Message message = new Message();
            message.setContent(content);
            message.setCreatedAt(LocalDateTime.now());
            
            logger.debug("保存前のメッセージ: {}", message);
            message = messageRepository.save(message);
            logger.info("メッセージ作成成功 - ID: {}", message.getId());
            logger.debug("保存後のメッセージ: {}", message);
        } catch (Exception e) {
            logger.error("メッセージ作成エラー: {}", e.getMessage());
            logger.error("スタックトレース: ", e);
            throw new RuntimeException("メッセージの作成に失敗しました", e);
        }
        logger.info("=== CREATE処理完了 ===");
    }

    @Transactional(propagation = Propagation.MANDATORY)
    protected void handleUpdate(Long id, String content) {
        try {
            logger.debug("メッセージを更新します - ID: {}, 内容: {}", id, content);
            messageRepository.findById(id).ifPresentOrElse(
                message -> {
                    message.setContent(content);
                    messageRepository.save(message);
                    logger.info("メッセージの更新が完了しました - ID: {}", id);
                },
                () -> {
                    logger.warn("更新対象のメッセージが見つかりません - ID: {}", id);
                    throw new RuntimeException("メッセージが見つかりません");
                }
            );
        } catch (Exception e) {
            logger.error("メッセージの更新に失敗しました - ID: {}, エラー: {}", id, e.getMessage(), e);
            throw new RuntimeException("メッセージの更新に失敗しました", e);
        }
    }

    @Transactional(propagation = Propagation.MANDATORY)
    protected void handleDelete(Long id) {
        try {
            logger.debug("メッセージを削除します - ID: {}", id);
            messageRepository.findById(id).ifPresentOrElse(
                message -> {
                    messageRepository.delete(message);
                    logger.info("メッセージの削除が完了しました - ID: {}", id);
                },
                () -> {
                    logger.warn("削除対象のメッセージが見つかりません - ID: {}", id);
                    throw new RuntimeException("メッセージが見つかりません");
                }
            );
        } catch (Exception e) {
            logger.error("メッセージの削除に失敗しました - ID: {}, エラー: {}", id, e.getMessage(), e);
            throw new RuntimeException("メッセージの削除に失敗しました", e);
        }
    }
} 