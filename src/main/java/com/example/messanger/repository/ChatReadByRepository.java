package com.example.messanger.repository;

import com.example.messanger.models.ChatReadBy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ChatReadByRepository extends JpaRepository<ChatReadBy, Long> {

    @Query(value = "select * from chat_read_status where message_id in :messageId and user_id= :userId", nativeQuery = true)
    List<ChatReadBy> getChatReadByMessageAndUser(List<Long> messageId, String userId);

    @Query(value = "select * from chat_read_status where user_id= :userId and is_read = :IsRead order by created_at desc", nativeQuery = true)
    List<ChatReadBy> getChatByUserAndStatus(String userId, Boolean IsRead);
}
