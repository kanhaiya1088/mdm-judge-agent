package com.cvs.mdmjudge.service;

import com.cvs.mdmjudge.model.ProductRecord;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OpenAIService {
    private final OpenAiService openAiService;
    private final ObjectMapper objectMapper;
    private final PromptTemplates promptTemplates;

    @Value("${openapi.model}")
    private String model;

    @Value("${openapi.temperature}")
    private Double temperature;

    @Value("${openapi.maxTokens}")
    private Integer maxTokens;

    @Retryable(maxAttempts = 3)
    public String analyzeDiscrepancies(ProductRecord mdmProduct, List<ProductRecord> retailerProducts) {
        List<ChatMessage> messages = new ArrayList<>();

        // System message to set context
        messages.add(new ChatMessage("system", promptTemplates.getSystemPrompt()));

        // Create comparison prompt
        String comparisonPrompt = promptTemplates.getComparisonPrompt(mdmProduct, retailerProducts);
        messages.add(new ChatMessage("user", comparisonPrompt));

        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model(model)
                .messages(messages)
                .temperature(temperature)
                .maxTokens(maxTokens)
                .build();

        return openAiService.createChatCompletion(request)
                .getChoices().get(0).getMessage().getContent();
    }

    @Retryable(maxAttempts = 3)
    public String generateCorrectionSuggestions(String discrepanciesAnalysis) {
        List<ChatMessage> messages = new ArrayList<>();
        messages.add(new ChatMessage("system", promptTemplates.getSuggestionsSystemPrompt()));
        messages.add(new ChatMessage("user", promptTemplates.getSuggestionsPrompt(discrepanciesAnalysis)));

        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model(model)
                .messages(messages)
                .temperature(temperature)
                .maxTokens(maxTokens)
                .build();

        return openAiService.createChatCompletion(request)
                .getChoices().get(0).getMessage().getContent();
    }
}