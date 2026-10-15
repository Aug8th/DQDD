package org.example.service.chatbot;

import jakarta.persistence.EntityManager;
import org.example.dto.request.ChatSessionRequest;
import org.example.dto.response.ChatMessageResponse;
import org.example.dto.response.ChatSessionResponse;
import org.example.entity.chatbot.ChatSession;
import org.example.entity.common.User;
import org.example.mapper.ChatMessageMapper;
import org.example.mapper.ChatSessionMapper;
import org.example.repository.ChatMessageRepository;
import org.example.repository.ChatSessionRepository;
import org.example.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ChatSessionService {

    private final UserRepository users;
    private final ChatSessionRepository sessions;
    private final ChatMessageRepository messages;
    private final EntityManager entityManager;
    private final org.example.repository.AiRecipeRepository aiRecipes;
    private final org.example.repository.AiRecipeIngredientRepository aiIngredients;

    public ChatSessionService(
            UserRepository users,
            ChatSessionRepository sessions,
            ChatMessageRepository messages,
            EntityManager entityManager,
            org.example.repository.AiRecipeRepository aiRecipes,
            org.example.repository.AiRecipeIngredientRepository aiIngredients
    ) {
        this.users = users;
        this.sessions = sessions;
        this.messages = messages;
        this.entityManager = entityManager;
        this.aiRecipes = aiRecipes;
        this.aiIngredients = aiIngredients;
    }

    @Transactional
    public ChatSessionResponse create(
            String email,
            ChatSessionRequest request
    ) {
        User user = findUser(email);

        String title = request == null || request.title() == null
                ? "New conversation"
                : request.title().trim();

        if (title.isBlank()) {
            title = "New conversation";
        }

        if (title.length() > 255) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Chat title must not exceed 255 characters"
            );
        }

        ChatSession session = sessions.saveAndFlush(
                new ChatSession(user, title)
        );

        entityManager.refresh(session);

        return ChatSessionMapper.toResponse(session);
    }

    @Transactional(readOnly = true)
    public List<ChatSessionResponse> getMySessions(String email) {
        User user = findUser(email);

        return sessions
                .findByUserIdOrderByUpdatedAtDescIdDesc(user.getId())
                .stream()
                .map(ChatSessionMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ChatMessageResponse> getHistory(
            String email,
            Integer sessionId
    ) {
        User user = findUser(email);

        findOwnedSession(sessionId, user.getId());

        return messages
                .findBySessionIdAndSessionUserIdOrderByIdAsc(
                        sessionId,
                        user.getId()
                )
                .stream()
                .map(message -> new ChatMessageResponse(
                        message.getId(),
                        message.getSender(),
                        message.getContent(),
                        message.getCreatedAt(),
                        aiRecipes
                                .findBySourceMessageIdAndUserIdOrderByIdAsc(
                                        message.getId(),
                                        user.getId()
                                )
                                .stream()
                                .map(recipe -> org.example.mapper.AiRecipeMapper.toResponse(
                                        recipe,
                                        aiIngredients.findByRecipeIdOrderByIdAsc(recipe.getId())
                                ))
                                .toList()
                ))
                .toList();
    }

    @Transactional
    public void delete(
            String email,
            Integer sessionId
    ) {
        User user = findUser(email);

        ChatSession session = findOwnedSession(
                sessionId,
                user.getId()
        );

        sessions.delete(session);
        sessions.flush();
    }

    private ChatSession findOwnedSession(
            Integer sessionId,
            Integer userId
    ) {
        return sessions.findByIdAndUserId(sessionId, userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Chat session not found"
                ));
    }

    private User findUser(String email) {
        return users.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found"
                ));
    }
}