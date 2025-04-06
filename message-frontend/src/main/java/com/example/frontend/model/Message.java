package com.example.frontend.model;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class Message {
    private Long id;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 