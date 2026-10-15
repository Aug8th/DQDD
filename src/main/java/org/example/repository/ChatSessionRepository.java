package org.example.repository;

import org.example.entity.chatbot.ChatSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

@org.springframework.stereotype.Repository
public interface ChatSessionRepository
        extends JpaRepository<ChatSession, Integer> {

    List<ChatSession> findByUserIdOrderByUpdatedAtDescIdDesc(
            Integer userId
    );

    Optional<ChatSession> findByIdAndUserId(
            Integer id,
            Integer userId
    );

    @Modifying
    @Query("""
            UPDATE ChatSession session
            SET session.updatedAt = CURRENT_TIMESTAMP
            WHERE session.id = :sessionId
              AND session.user.id = :userId
            """)
    int updateLastActivity(
            @Param("sessionId") Integer sessionId,
            @Param("userId") Integer userId
    );
}