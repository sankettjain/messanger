package com.example.messanger.repository;

import com.example.messanger.models.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

//    @Query(value = "select * from messages where chat_id= :chatId order by created_at desc")
    List<Message> findByChatIdOrderByCreatedAtDesc(/*@Param("chatId") */Long chatId);
}
