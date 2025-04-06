package com.example.frontend.mapper;

import com.example.frontend.model.Message;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Optional;

@Mapper
public interface MessageMapper {
    List<Message> findAll(@Param("offset") int offset,
                         @Param("limit") int limit,
                         @Param("sortField") String sortField,
                         @Param("sortDirection") String sortDirection,
                         @Param("keyword") String keyword);

    int count(@Param("keyword") String keyword);

    Optional<Message> findById(@Param("id") Long id);

    void insert(Message message);

    void update(Message message);

    void delete(@Param("id") Long id);
} 