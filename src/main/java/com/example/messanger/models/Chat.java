package com.example.messanger.models;


import com.example.messanger.payload.enums.ChatType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "chats")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Chat extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private ChatType type;

    @Column(name = "last_seen_message")
    private String lastSeenMessage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "last_seen_user_id", referencedColumnName = "id")
    private User lastSeenUser;


}
