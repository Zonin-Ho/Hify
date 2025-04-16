package com.example.demo.config;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;

public class ModelFactory {
    
    private static final String BASE_URL = "https://api.siliconflow.cn/v1";
    private static final String API_KEY = "sk-urcexbcpvvpjifksqxyyckfawnzlbgcazxllcxgaycdtaoer";
    private static final double DEFAULT_TEMPERATURE = 0.2;

    public static ChatLanguageModel createChatModel(String modelName) {
        return OpenAiChatModel
                .builder()
                .baseUrl(BASE_URL)
                .apiKey(API_KEY)
                .modelName(modelName)
                .temperature(DEFAULT_TEMPERATURE)
                .build();
    }

    public static StreamingChatLanguageModel createStreamingChatModel(String modelName) {
        return OpenAiStreamingChatModel
                .builder()
                .baseUrl(BASE_URL)
                .apiKey(API_KEY)
                .modelName(modelName)
                .temperature(DEFAULT_TEMPERATURE)
                .build();
    }
}