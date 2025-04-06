package com.example.backend.mapper;

import com.example.backend.model.Message;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.Optional;

@Mapper
public interface MessageMapper {
    void insert(Message message);
    
    void update(Message message);
    
    void delete(@Param("id") Long id);
    
    Optional<Message> findById(@Param("id") Long id);
} 