package com.example.frontend.repository;

import com.example.frontend.mapper.MessageMapper;
import com.example.frontend.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.HashMap;

@Repository
public class MessageRepository {
    private static final Logger log = LoggerFactory.getLogger(MessageRepository.class);
    private final MessageMapper messageMapper;

    public MessageRepository(MessageMapper messageMapper) {
        this.messageMapper = messageMapper;
    }

    public List<Map<String, Object>> findAllWithSortAndPaging(int offset, int limit, String sortField, String sortDirection, String keyword) {
        log.debug("メッセージを取得します offset={}, limit={}, sortField={}, sortDirection={}, keyword={}", 
                  offset, limit, sortField, sortDirection, keyword);
        
        List<Message> messages = messageMapper.findAll(offset, limit, sortField, sortDirection, keyword);
        return messages.stream()
            .map(message -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", message.getId());
                map.put("content", message.getContent());
                map.put("created_at", message.getCreatedAt());
                map.put("updated_at", message.getUpdatedAt());
                return map;
            })
            .collect(Collectors.toList());
    }

    public int count(String keyword) {
        return messageMapper.count(keyword);
    }

    public Optional<Map<String, Object>> findById(Long id) {
        log.debug("ID: {}のメッセージを取得します", id);
        return messageMapper.findById(id)
            .map(message -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", message.getId());
                map.put("content", message.getContent());
                map.put("created_at", message.getCreatedAt());
                map.put("updated_at", message.getUpdatedAt());
                return map;
            });
    }
} 