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

    @JmsListener(destination = "messageQueue", concurrency = "1")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void receiveMessage(String jsonMessage) {
        logger.debug("Received raw message: {}", jsonMessage);
        try {
            Map<String, Object> messageMap = objectMapper.readValue(jsonMessage, Map.class);
            String action = (String) messageMap.get("action");
            Map<String, Object> data = (Map<String, Object>) messageMap.get("data");
            
            logger.info("Processing message - Action: {}, Data: {}", action, data);

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
                    logger.warn("Unknown action received: {}", action);
                    break;
            }
        } catch (Exception e) {
            logger.error("Error processing message: {}", e.getMessage(), e);
            throw new RuntimeException("Error processing message", e);
        }
    }

    @Transactional(propagation = Propagation.MANDATORY)
    protected void handleCreate(String content) {
        try {
            logger.debug("Creating new message with content: {}", content);
            Message message = new Message();
            message.setContent(content);
            message.setCreatedAt(LocalDateTime.now());
            message = messageRepository.save(message);
            logger.info("Message created successfully with ID: {}", message.getId());
        } catch (Exception e) {
            logger.error("Error creating message: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create message", e);
        }
    }

    @Transactional(propagation = Propagation.MANDATORY)
    protected void handleUpdate(Long id, String content) {
        try {
            logger.debug("Updating message {} with content: {}", id, content);
            messageRepository.findById(id).ifPresentOrElse(
                message -> {
                    message.setContent(content);
                    messageRepository.save(message);
                    logger.info("Message {} updated successfully", id);
                },
                () -> {
                    logger.warn("Message {} not found for update", id);
                    throw new RuntimeException("Message not found");
                }
            );
        } catch (Exception e) {
            logger.error("Error updating message {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Failed to update message", e);
        }
    }

    @Transactional(propagation = Propagation.MANDATORY)
    protected void handleDelete(Long id) {
        try {
            logger.debug("Deleting message: {}", id);
            messageRepository.findById(id).ifPresentOrElse(
                message -> {
                    messageRepository.delete(message);
                    logger.info("Message {} deleted successfully", id);
                },
                () -> {
                    logger.warn("Message {} not found for deletion", id);
                    throw new RuntimeException("Message not found");
                }
            );
        } catch (Exception e) {
            logger.error("Error deleting message {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Failed to delete message", e);
        }
    }
} 