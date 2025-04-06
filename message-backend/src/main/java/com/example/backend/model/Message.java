package com.example.backend.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Message {
    private Long id;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 