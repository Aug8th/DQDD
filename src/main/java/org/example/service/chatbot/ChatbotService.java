package org.example.service.chatbot;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.example.dto.request.ChatMessageRequest;
import org.example.dto.response.AiChatResult;
import org.example.dto.response.ChatReplyResponse;
import org.example.entity.chatbot.ChatMessage;
import org.example.entity.indoor.Recipe;
import org.example.mapper.ChatMessageMapper;
import org.example.mapper.RecipeMapper;
import org.example.repository.ChatMessageRepository;
import org.example.repository.ChatSessionRepository;
import org.example.repository.RecipeRepository;
import org.example.repository.UserFridgeRepository;
import org.example.repository.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ChatbotService {

    private final UserRepository users;
    private final ChatSessionRepository sessions;
    private final ChatMessageRepository messages;
    private final RecipeRepository recipes;
    private final UserFridgeRepository fridges;
    private final AiRecipeGenerationService generation;
    private final AiRecipeStorageService storage;
    private final ObjectMapper objectMapper;
    private final EntityManager entityManager;
    private final TransactionTemplate transaction;
    private final org.example.repository.AiRecipeRepository aiRecipes;
    private final org.example.repository.AiRecipeIngredientRepository aiIngredients;

    public ChatbotService(
            UserRepository users,
            ChatSessionRepository sessions,
            ChatMessageRepository messages,
            RecipeRepository recipes,
            UserFridgeRepository fridges,
            AiRecipeGenerationService generation,
            AiRecipeStorageService storage,
            ObjectMapper objectMapper,
            EntityManager entityManager,
            PlatformTransactionManager transactionManager,
            org.example.repository.AiRecipeRepository aiRecipes,
            org.example.repository.AiRecipeIngredientRepository aiIngredients
    ) {
        this.users = users;
        this.sessions = sessions;
        this.messages = messages;
        this.recipes = recipes;
        this.fridges = fridges;
        this.generation = generation;
        this.storage = storage;
        this.objectMapper = objectMapper;
        this.entityManager = entityManager;
        this.transaction = new TransactionTemplate(transactionManager);
        this.aiRecipes = aiRecipes;
        this.aiIngredients = aiIngredients;
    }

    public ChatReplyResponse sendMessage(
            String email,
            Integer sessionId,
            ChatMessageRequest request
    ) {
        if (request == null
                || request.message() == null
                || request.message().isBlank()
                || request.message().length() > 4_000) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Message must contain between 1 and 4000 characters"
            );
        }

        String question = request.message().trim();

        PreparedChat prepared = Objects.requireNonNull(
                transaction.execute(status -> prepare(
                        email,
                        sessionId,
                        question,
                        request.useFridge()
                ))
        );

        // Call AI outside the transaction to avoid holding a database connection while waiting.
        AiChatResult result = generation.generate(
                prepared.contents(),
                prepared.context()
        );

        return Objects.requireNonNull(
                transaction.execute(status -> saveReply(
                        email,
                        sessionId,
                        question,
                        prepared.allowedRecipeIds(),
                        result
                ))
        );
    }

    private PreparedChat prepare(
            String email,
            Integer sessionId,
            String question,
            boolean useFridge
    ) {
        var user = users.findByEmail(email)
                .orElseThrow(() -> notFound("User not found"));

        sessions.findByIdAndUserId(sessionId, user.getId())
                .orElseThrow(() -> notFound("Chat session not found"));

        var history = new ArrayList<>(
                messages.findBySessionIdAndSessionUserIdOrderByIdDesc(
                        sessionId,
                        user.getId(),
                        PageRequest.of(0, 12)
                )
        );

        Collections.reverse(history);

        List<Map<String, Object>> contents = new ArrayList<>();

        for (ChatMessage message : history) {
            String role = "USER".equals(message.getSender())
                    ? "user"
                    : "model";

            contents.add(Map.of(
                    "role", role,
                    "parts", List.of(
                            Map.of("text", historyContent(message, user.getId()))
                    )
            ));
        }

        contents.add(Map.of(
                "role", "user",
                "parts", List.of(Map.of("text", question))
        ));

        List<Recipe> availableRecipes = recipes
                .findByApprovalStatusOrderByCreatedAtDesc(
                        "approved",
                        PageRequest.of(0, 30)
                );

        var recipeContext = availableRecipes.stream()
                .map(recipe -> Map.of(
                        "id", recipe.getId(),
                        "name", recipe.getName(),
                        "instructions", recipe.getInstructions(),
                        "ingredients", recipe.getIngredients().stream()
                                .map(i -> Map.of(
                                        "ingredientName", i.getIngredientName(),
                                        "quantity", i.getQuantity() == null
                                                ? ""
                                                : i.getQuantity()
                                ))
                                .toList()
                ))
                .toList();

        LocalDate today = LocalDate.now(
                ZoneId.of("Asia/Ho_Chi_Minh")
        );

        List<Map<String, Object>> fridgeContext = new ArrayList<>();

        if (useFridge) {
            var items = fridges.findByUserIdOrderByExpiryDateAsc(
                    user.getId(),
                    PageRequest.of(0, 100)
            );

            for (var item : items) {
                Map<String, Object> data = new java.util.LinkedHashMap<>();

                data.put("ingredientName", item.getIngredientName());
                data.put("quantity", item.getQuantity());
                data.put("unit", item.getUnit());
                data.put("storageType", item.getStorageType());
                data.put("expiryDate", item.getExpiryDate());
                data.put(
                        "expired",
                        item.getExpiryDate() != null
                                && item.getExpiryDate().isBefore(today)
                );

                fridgeContext.add(data);
            }
        }

        Map<String, Object> context = Map.of(
                "today", today.toString(),
                "availableRecipes", recipeContext,
                "fridgeEnabled", useFridge,
                "fridgeItems", fridgeContext
        );

        Set<Integer> allowedIds = availableRecipes.stream()
                .map(Recipe::getId)
                .collect(Collectors.toSet());

        try {
            return new PreparedChat(
                    contents,
                    objectMapper.writeValueAsString(context),
                    allowedIds
            );
        } catch (JsonProcessingException exception) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Unable to prepare chatbot context"
            );
        }
    }

    private ChatReplyResponse saveReply(
            String email,
            Integer sessionId,
            String question,
            Set<Integer> allowedRecipeIds,
            AiChatResult result
    ) {
        var user = users.findByEmail(email)
                .orElseThrow(() -> notFound("User not found"));

        if (!user.isActive()) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Account is locked"
            );
        }

        var session = sessions.findByIdAndUserId(
                        sessionId,
                        user.getId()
                )
                .orElseThrow(() -> notFound("Chat session not found"));

        List<Integer> suggestedIds = result.suggestedRecipeIds()
                .stream()
                .distinct()
                .toList();

        if (!allowedRecipeIds.containsAll(suggestedIds)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "AI suggested a recipe ID that is not in the context"
            );
        }

        var suggestedRecipes = recipes.findAllById(suggestedIds)
                .stream()
                .filter(recipe ->
                        "approved".equals(recipe.getApprovalStatus())
                )
                .map(RecipeMapper::toResponse)
                .toList();

        ChatMessage userMessage = messages.saveAndFlush(
                new ChatMessage(session, "USER", question)
        );

        ChatMessage assistantMessage = messages.saveAndFlush(
                new ChatMessage(session, "ASSISTANT", result.answer())
        );

        entityManager.refresh(userMessage);
        entityManager.refresh(assistantMessage);

        var generatedRecipes = storage.saveGeneratedRecipes(
                user,
                assistantMessage,
                result.generatedRecipes()
        );

        sessions.updateLastActivity(sessionId, user.getId());

        return new ChatReplyResponse(
                sessionId,
                ChatMessageMapper.toResponse(userMessage),
                new org.example.dto.response.ChatMessageResponse(
                        assistantMessage.getId(),
                        assistantMessage.getSender(),
                        assistantMessage.getContent(),
                        assistantMessage.getCreatedAt(),
                        generatedRecipes
                ),
                suggestedRecipes,
                generatedRecipes
        );
    }

    private String historyContent(ChatMessage message, Integer userId) {
        if (!"ASSISTANT".equals(message.getSender())) {
            return message.getContent();
        }

        var cards = aiRecipes
                .findBySourceMessageIdAndUserIdOrderByIdAsc(message.getId(), userId)
                .stream()
                .map(recipe -> org.example.mapper.AiRecipeMapper.toResponse(
                        recipe,
                        aiIngredients.findByRecipeIdOrderByIdAsc(recipe.getId())
                ))
                .toList();

        if (cards.isEmpty()) {
            return message.getContent();
        }

        try {
            return objectMapper.writeValueAsString(Map.of(
                    "answer", message.getContent(),
                    "generatedRecipes", cards
            ));
        } catch (JsonProcessingException exception) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Unable to read recipes from chat history"
            );
        }
    }

    private ResponseStatusException notFound(String message) {
        return new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                message
        );
    }

    private record PreparedChat(
            List<Map<String, Object>> contents,
            String context,
            Set<Integer> allowedRecipeIds
    ) {
    }
}