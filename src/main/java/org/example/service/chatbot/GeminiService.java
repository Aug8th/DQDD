package org.example.service.chatbot;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class GeminiService {

    private static final Logger log =
            LoggerFactory.getLogger(GeminiService.class);

    private final RestClient client;
    private final String apiKey;
    private final String model;
    private final JsonNode schema;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public GeminiService(
            @Value("${app.gemini.api-key:}") String apiKey,
            @Value("${app.gemini.base-url}") String baseUrl,
            @Value("${app.gemini.model:}") String model
    ) {
        this.apiKey = apiKey == null ? "" : apiKey.trim();

        String configuredModel = model == null ? "" : model.trim();
        this.model = configuredModel.startsWith("models/")
                ? configuredModel.substring(7)
                : configuredModel;

        try (var stream = new ClassPathResource(
                "ai-recipe-schema.json"
        ).getInputStream()) {
            this.schema = objectMapper.readTree(stream);

            if (this.schema == null || !this.schema.isObject()) {
                throw new IllegalStateException(
                        "The AI recipe schema must be a JSON object"
                );
            }
        } catch (IOException ex) {
            throw new IllegalStateException(
                    "Unable to read the AI recipe schema",
                    ex
            );
        }

        var requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(10_000);
        requestFactory.setReadTimeout(90_000);

        this.client = RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(requestFactory)
                .build();
    }

    public String generateText(
            String systemInstruction,
            List<Map<String, Object>> contents
    ) {
        if (apiKey.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "GEMINI_API_KEY is not configured"
            );
        }

        if (model.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "Gemini model is not configured"
            );
        }

        Map<String, Object> body = Map.of(
                "systemInstruction", Map.of(
                        "parts", List.of(
                                Map.of("text", systemInstruction)
                        )
                ),
                "generationConfig", Map.of(
                        "responseMimeType", "application/json",
                        "responseSchema", schema
                ),
                "contents", contents
        );

        JsonNode response;

        try {
            response = client.post()
                    .uri(
                            "/v1beta/models/{model}:generateContent",
                            model
                    )
                    .header("x-goog-api-key", apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .body(JsonNode.class);

        } catch (RestClientResponseException exception) {
            int status = exception.getStatusCode().value();

            // Log upstream error details without logging the API key or request.
            log.error(
                    "Gemini HTTP {}: {}",
                    status,
                    readErrorMessage(exception)
            );

            if (status == 429) {
                throw new ResponseStatusException(
                        HttpStatus.TOO_MANY_REQUESTS,
                        "Gemini usage limit reached. Please try again later. HTTP 429"
                );
            }

            if (status == 400 || status == 401
                    || status == 403 || status == 404) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_GATEWAY,
                        "Gemini rejected the request. Check the API key, model "
                                + "and configuration. HTTP " + status
                );
            }

            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "Gemini service returned an error. HTTP " + status
                            + ". Please check the server log for details."
            );

        } catch (ResourceAccessException exception) {
            log.warn("Gemini connection failed or the request timed out.");

            throw new ResponseStatusException(
                    HttpStatus.GATEWAY_TIMEOUT,
                    "Unable to connect to Gemini or the request timed out."
            );

        } catch (RestClientException exception) {
            log.warn("Unable to process the Gemini HTTP response.");

            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "Unable to read the Gemini response."
            );
        }

        if (response == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "Gemini returned an empty response."
            );
        }

        JsonNode candidates = response.path("candidates");

        if (!candidates.isArray() || candidates.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "Gemini could not generate an answer for this request."
            );
        }

        JsonNode candidate = candidates.get(0);
        String finishReason = candidate.path("finishReason").asText();

        if (!"STOP".equals(finishReason)) {
            log.warn(
                    "Gemini response was incomplete. finishReason={}",
                    finishReason
            );

            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "Gemini did not return a complete answer. "
                            + "Please try a shorter request."
            );
        }

        StringBuilder answer = new StringBuilder();
        JsonNode parts = candidate.path("content").path("parts");

        if (parts.isArray()) {
            for (JsonNode part : parts) {
                if (part.path("thought").asBoolean(false)) {
                    continue;
                }

                JsonNode text = part.get("text");

                if (text != null && text.isTextual()) {
                    answer.append(text.asText());
                }
            }
        }

        if (answer.toString().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "Gemini did not return text content."
            );
        }

        return answer.toString().trim();
    }

    private String readErrorMessage(RestClientResponseException exception) {
        try {
            JsonNode errorBody = objectMapper.readTree(
                    exception.getResponseBodyAsString()
            );

            JsonNode error = errorBody == null
                    ? null
                    : errorBody.get("error");

            if (error == null) {
                return "No error details were provided.";
            }

            String details = error.toString();

            return apiKey.isBlank()
                    ? details
                    : details.replace(apiKey, "[REDACTED]");
        } catch (IOException ignored) {
            return "Unable to read Gemini error details.";
        }
    }
}