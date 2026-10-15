package org.example.entity.chatbot;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@lombok.Getter
@lombok.Setter
@lombok.NoArgsConstructor
@Entity
@Table(name = "chat_messages")
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "session_id", nullable = false)
    private ChatSession session;

    @Column(
            nullable = false,
            columnDefinition = "enum('USER','ASSISTANT')"
    )
    private String sender;

    @Column(nullable = false, columnDefinition = "MEDIUMTEXT")
    private String content;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    public ChatMessage(
            ChatSession session,
            String sender,
            String content
    ) {
        this.session = session;
        this.sender = sender;
        this.content = content;
    }

}