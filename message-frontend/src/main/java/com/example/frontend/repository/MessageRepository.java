package com.example.frontend.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class MessageRepository {
    private static final Logger log = LoggerFactory.getLogger(MessageRepository.class);
    private final JdbcTemplate jdbcTemplate;

    public MessageRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Map<String, Object>> findAllOrderByCreatedAtDescWithPaging(int offset, int limit) {
        log.debug("メッセージを取得します offset={}, limit={}", offset, limit);
        return jdbcTemplate.queryForList(
            "SELECT * FROM messages ORDER BY created_at DESC LIMIT ? OFFSET ?",
            limit, offset
        );
    }

    public int count() {
        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM messages", Integer.class);
    }

    public Optional<Map<String, Object>> findById(Long id) {
        log.debug("ID: {}のメッセージを取得します", id);
        try {
            Map<String, Object> message = jdbcTemplate.queryForMap(
                "SELECT * FROM messages WHERE id = ?", id
            );
            return Optional.of(message);
        } catch (Exception e) {
            log.error("メッセージの取得に失敗しました: {}", e.getMessage());
            return Optional.empty();
        }
    }
} 