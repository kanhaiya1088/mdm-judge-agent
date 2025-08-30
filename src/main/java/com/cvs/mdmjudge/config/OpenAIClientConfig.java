package com.cvs.mdmjudge.config;

import com.theokanning.openai.service.OpenAiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;


@Configuration
public class OpenAIClientConfig {

    @Value("${openapi.apiKey}")
    private String apiKey;

    @Value("${openapi.timeout:60}")
    private Integer timeout;

    @Bean
    public OpenAiService openAiService() {
        return new OpenAiService(apiKey, Duration.ofSeconds(timeout));
    }
}