package com.example.demo.controller;

import com.example.demo.config.AiConfig;
import com.example.demo.config.ModelFactory;
import com.example.demo.dto.ChatDTO;
import com.example.demo.exception.BusinessException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.ollama.OllamaStreamingChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.TokenStream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import jakarta.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api")
public class ChatController {
    @Autowired
    AiConfig.Assistant openAIAssistant;
    
    @Value("${silicon.cloud.model:Qwen/QwQ-32B}")
    private String defaultModelName;
    
    // 创建基于特定模型的Assistant实例
    private AiConfig.Assistant createAssistantForModel(String modelName, String platform) {
        log.info("使用模型名称: {}, 平台: {}", modelName, platform);
        
        // 使用ModelFactory创建模型实例
        ChatLanguageModel chatModel = ModelFactory.createChatModel(modelName, platform);
        StreamingChatLanguageModel streamingChatModel = ModelFactory.createStreamingChatModel(modelName, platform);
        
        // 创建聊天记忆
        ChatMemory chatMemory = MessageWindowChatMemory.withMaxMessages(20);
        
        // 构建Assistant实例
        return AiServices.builder(AiConfig.Assistant.class)
                .chatLanguageModel(chatModel)
                .streamingChatLanguageModel(streamingChatModel)
                .chatMemory(chatMemory)
                .build();
    }
    
    // 重载方法，使用默认平台
    private AiConfig.Assistant createAssistantForModel(String modelName) {
        return createAssistantForModel(modelName, ModelFactory.PLATFORM_DASHSCOPE);
    }

    @PostMapping(value = "/chat", produces = "text/event-stream;charset=UTF-8")
    public Flux<String> chat(@RequestBody ChatDTO chatDTO, HttpServletResponse response) {
        log.info("用户询问：: {}", chatDTO.getMessage());
        
        // 设置禁用缓存的HTTP响应头
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("X-Accel-Buffering", "no");
        // response.setHeader("Expires", "0");
        
        // 使用传入的modelName或默认值
        String modelName = (chatDTO.getModelName() != null && !chatDTO.getModelName().isEmpty()) 
                ? chatDTO.getModelName() 
                : defaultModelName;
        
        // 获取平台参数，默认使用SiliconCloud
        String platform = (chatDTO.getPlatform() != null && !chatDTO.getPlatform().isEmpty())
                ? chatDTO.getPlatform()
                : ModelFactory.PLATFORM_DASHSCOPE;
        
        // 根据modelName和platform创建Assistant
        AiConfig.Assistant assistant;
            assistant = createAssistantForModel(modelName, platform);

        
        return Flux.create(sink -> {
            // 使用选定的assistant的stream方法获取TokenStream
            TokenStream tokenStream = assistant.stream(chatDTO.getMessage());
            tokenStream.onPartialResponse(s -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("message", s);
                    ObjectMapper objectMapper = new ObjectMapper();
                    String json = null;
                    try {
                        json = objectMapper.writeValueAsString(map);
                    } catch (JsonProcessingException e) {
                        throw new BusinessException(e);
                    }
//                    log.info("onPartialResponse {}", json);
                    sink.next(json);
                })
                .onCompleteResponse(chatResponse -> {
                    sink.next("[DONE]");
                    sink.complete();
                })
                .onError(sink::error)
                .start();
        });
    }
}
