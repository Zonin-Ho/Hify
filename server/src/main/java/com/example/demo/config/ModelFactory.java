package com.example.demo.config;

import dev.langchain4j.community.model.dashscope.QwenChatModel;
import dev.langchain4j.community.model.dashscope.QwenStreamingChatModel;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.ollama.OllamaStreamingChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;

public class ModelFactory {
    // 硅基流动
    private static final String SiliconCloud_BASE_URL = "https://api.siliconflow.cn/v1";
    private static final String SiliconCloud_API_KEY = "sk-urcexbcpvvpjifksqxyyckfawnzlbgcazxllcxgaycdtaoer";

    // 阿里百炼
    public static final String DashScope_BASE_URL = "https://dashscope.aliyuncs.com/compatible-mode/v1";
    public static final String DashScope_API_KEY = "sk-060735184f904aeda17b843a71469a61";
    
    // 平台类型常量
    public static final String PLATFORM_SILICON_CLOUD = "siliconcloud";
    public static final String PLATFORM_DASHSCOPE = "dashscope";

    /**
     * 根据平台类型获取基础URL
     * @param platform 平台类型，支持siliconcloud和dashscope
     * @return 对应平台的基础URL
     */
    private static String getBaseUrl(String platform) {
        if (PLATFORM_DASHSCOPE.equalsIgnoreCase(platform)) {
            return DashScope_BASE_URL;
        }
        // 默认使用SiliconCloud
        return SiliconCloud_BASE_URL;
    }
    
    /**
     * 根据平台类型获取API密钥
     * @param platform 平台类型，支持siliconcloud和dashscope
     * @return 对应平台的API密钥
     */
    private static String getApiKey(String platform) {
        if (PLATFORM_DASHSCOPE.equalsIgnoreCase(platform)) {
            return DashScope_API_KEY;
        }
        // 默认使用SiliconCloud
        return SiliconCloud_API_KEY;
    }
    
    /**
     * 创建聊天模型
     * @param modelName 模型名称
     * @param platform 平台类型，支持siliconcloud和dashscope
     * @return 聊天语言模型实例
     */
    public static ChatLanguageModel createChatModel(String modelName, String platform) {
        String baseUrl = getBaseUrl(platform);
        String apiKey = getApiKey(platform);
        
        return QwenChatModel
                .builder()
//                .baseUrl(baseUrl)
                .apiKey(apiKey)
                .modelName(modelName)
                .build();
    }
    
    /**
     * 创建聊天模型（使用默认平台dashscope）
     * @param modelName 模型名称
     * @return 聊天语言模型实例
     */
    public static ChatLanguageModel createChatModel(String modelName) {
        return createChatModel(modelName, PLATFORM_DASHSCOPE);
    }

    /**
     * 创建流式聊天模型
     * @param modelName 模型名称
     * @param platform 平台类型，支持siliconcloud和dashscope
     * @return 流式聊天语言模型实例
     */
    public static StreamingChatLanguageModel createStreamingChatModel(String modelName, String platform) {
        String baseUrl = getBaseUrl(platform);
        String apiKey = getApiKey(platform);
        
        return QwenStreamingChatModel
                .builder()
//                .baseUrl(baseUrl)
                .apiKey(apiKey)
                .modelName(modelName)
                .build();
    }
    
    /**
     * 创建流式聊天模型（使用默认平台dashscope）
     * @param modelName 模型名称
     * @return 流式聊天语言模型实例
     */
    public static StreamingChatLanguageModel createStreamingChatModel(String modelName) {
        return createStreamingChatModel(modelName, PLATFORM_DASHSCOPE);
    }
}