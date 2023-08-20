package com.example.messanger.repository;

import com.example.messanger.models.ChatParticipants;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatParticipantsRepository extends JpaRepository<ChatParticipants, Long> {

    @Query(value = "SELECT * from chat_participants where user_id= :fromUserId or user_id= :toUserId", nativeQuery = true)
    List<ChatParticipants> getChatParticipantsByFromAndTo(@Param("fromUserId") String fromUserId,
                                                          @Param("toUserId") String toUserId);


}
