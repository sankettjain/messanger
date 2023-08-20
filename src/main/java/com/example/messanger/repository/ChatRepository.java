package com.example.messanger.repository;

import com.example.messanger.models.Chat;
import com.example.messanger.models.ChatParticipants;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {
}
