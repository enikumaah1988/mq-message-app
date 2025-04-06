package com.example.frontend.service;

import com.example.frontend.mapper.MessageMapper;
import com.example.frontend.model.Message;
import com.example.frontend.repository.MessageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class MessageService {
    private static final Logger log = LoggerFactory.getLogger(MessageService.class);
    private final MessageRepository messageRepository;
    private final RabbitTemplate rabbitTemplate;
    private final MessageMapper messageMapper;

    public MessageService(MessageRepository messageRepository, RabbitTemplate rabbitTemplate, MessageMapper messageMapper) {
        this.messageRepository = messageRepository;
        this.rabbitTemplate = rabbitTemplate;
        this.messageMapper = messageMapper;
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> findAllWithSortAndPaging(int offset, int limit, String sortField, String sortDirection, String keyword) {
        return messageRepository.findAllWithSortAndPaging(offset, limit, sortField, sortDirection, keyword);
    }

    @Transactional(readOnly = true)
    public int count(String keyword) {
        return messageRepository.count(keyword);
    }

    @Transactional(readOnly = true)
    public Optional<Map<String, Object>> findById(Long id) {
        return messageRepository.findById(id);
    }

    @Transactional
    public void create(String content) {
        Message message = new Message();
        message.setContent(content);
        message.setCreatedAt(LocalDateTime.now());
        message.setUpdatedAt(LocalDateTime.now());
        messageMapper.insert(message);
        rabbitTemplate.convertAndSend("message", "create", Map.of(
            "id", message.getId(),
            "content", message.getContent(),
            "created_at", message.getCreatedAt(),
            "updated_at", message.getUpdatedAt()
        ));
    }

    @Transactional
    public void update(Long id, String content) {
        Message message = messageMapper.findById(id)
            .orElseThrow(() -> new RuntimeException("Message not found: " + id));
        message.setContent(content);
        message.setUpdatedAt(LocalDateTime.now());
        messageMapper.update(message);
        rabbitTemplate.convertAndSend("message", "update", Map.of(
            "id", message.getId(),
            "content", message.getContent(),
            "created_at", message.getCreatedAt(),
            "updated_at", message.getUpdatedAt()
        ));
    }

    @Transactional
    public void delete(Long id) {
        messageMapper.delete(id);
        rabbitTemplate.convertAndSend("message", "delete", Map.of("id", id));
    }
} 