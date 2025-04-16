package com.example.demo.config;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SiliconCloudConfig {
    
    @Value("${silicon.cloud.model:Qwen/QwQ-32B}")
    private String modelName;
    
    @Bean
    @Qualifier("siliconCloud")
    public ChatLanguageModel siliconCloudModel() {
        return ModelFactory.createChatModel(modelName);
    }

    @Bean
    @Qualifier("siliconCloud")
    public StreamingChatLanguageModel siliconCloudStreamingModel() {
        return ModelFactory.createStreamingChatModel(modelName);
    }
}
