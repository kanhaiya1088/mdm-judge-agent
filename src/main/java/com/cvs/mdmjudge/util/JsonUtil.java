package com.cvs.mdmjudge.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JsonUtil {
    private final ObjectMapper objectMapper;

    /**
     * Extracts assistant content text from OpenAI chat response
     */

    public static String extractAssistantContent(String openAiResponseJson) {
        try {
            JsonNode root = new ObjectMapper().readTree(openAiResponseJson);
            JsonNode content =
                    root.path("choices").get(0).path("message").path("content");
            if (content.isMissingNode() || content.isNull()) {
                return "{}";
            }
            return content.asText();
        } catch (Exception e) {
            return "{}";
        }
    }

    public String toJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting object to JSON", e);
        }
    }

    public <T> T fromJson(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error parsing JSON", e);
        }
    }
}