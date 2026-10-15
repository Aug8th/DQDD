package org.example.controller.user;

import org.example.dto.request.ChatMessageRequest;
import org.example.dto.request.ChatSessionRequest;
import org.example.dto.response.AiRecipeResponse;
import org.example.dto.response.AiRecipeSummaryResponse;
import org.example.dto.response.ChatMessageResponse;
import org.example.dto.response.ChatReplyResponse;
import org.example.dto.response.ChatSessionResponse;
import org.example.repository.UserRepository;
import org.example.service.chatbot.AiRecipeStorageService;
import org.example.service.chatbot.ChatbotService;
import org.example.service.chatbot.ChatSessionService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/chatbot")
public class ChatbotController {

    private final ChatSessionService sessions;
    private final ChatbotService chatbot;
    private final AiRecipeStorageService aiRecipes;
    private final UserRepository users;

    public ChatbotController(
            ChatSessionService sessions,
            ChatbotService chatbot,
            AiRecipeStorageService aiRecipes,
            UserRepository users
    ) {
        this.sessions = sessions;
        this.chatbot = chatbot;
        this.aiRecipes = aiRecipes;
        this.users = users;
    }

    @PostMapping("/sessions")
    @ResponseStatus(HttpStatus.CREATED)
    public ChatSessionResponse createSession(
            Authentication auth,
            @RequestBody(required = false) ChatSessionRequest request
    ) {
        return sessions.create(auth.getName(), request);
    }

    @GetMapping("/sessions")
    public List<ChatSessionResponse> getMySessions(
            Authentication auth
    ) {
        return sessions.getMySessions(auth.getName());
    }

    @GetMapping("/sessions/{sessionId}/messages")
    public List<ChatMessageResponse> getHistory(
            Authentication auth,
            @PathVariable Integer sessionId
    ) {
        return sessions.getHistory(
                auth.getName(),
                sessionId
        );
    }

    @PostMapping("/sessions/{sessionId}/messages")
    public ChatReplyResponse sendMessage(
            Authentication auth,
            @PathVariable Integer sessionId,
            @RequestBody ChatMessageRequest request
    ) {
        return chatbot.sendMessage(
                auth.getName(),
                sessionId,
                request
        );
    }

    @DeleteMapping("/sessions/{sessionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSession(
            Authentication auth,
            @PathVariable Integer sessionId
    ) {
        sessions.delete(auth.getName(), sessionId);
    }

    @GetMapping("/recipes")
    public List<AiRecipeSummaryResponse> getMyRecipes(
            Authentication auth
    ) {
        return aiRecipes.getMyRecipes(
                currentUserId(auth)
        );
    }

    @GetMapping("/recipes/{recipeId}")
    public AiRecipeResponse getMyRecipe(
            Authentication auth,
            @PathVariable Integer recipeId
    ) {
        return aiRecipes.getMyRecipe(
                currentUserId(auth),
                recipeId
        );
    }

    @DeleteMapping("/recipes/{recipeId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMyRecipe(
            Authentication auth,
            @PathVariable Integer recipeId
    ) {
        aiRecipes.deleteMyRecipe(
                currentUserId(auth),
                recipeId
        );
    }

    private Integer currentUserId(Authentication auth) {
        return users.findByEmail(auth.getName())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Không tìm thấy user"
                ))
                .getId();
    }
}