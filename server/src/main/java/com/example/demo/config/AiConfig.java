package com.example.demo.config;

import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.TokenStream;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {

    public interface Assistant {
        String chat(String message);
        // 流式响应
        TokenStream stream(String message);
    }

    @Bean("siliconCloud")
    public Assistant openAIAssistant(@Qualifier("siliconCloud") ChatLanguageModel chatLanguageModel,
                                     @Qualifier("siliconCloud") StreamingChatLanguageModel streamingChatLanguageModel) {
        ChatMemory chatMemory = MessageWindowChatMemory.withMaxMessages(20);


        Assistant assistant = AiServices.builder(Assistant.class)
                .chatLanguageModel(chatLanguageModel)
                .streamingChatLanguageModel(streamingChatLanguageModel)
                .chatMemory(chatMemory)
                .build();

        return  assistant;
    }
}
