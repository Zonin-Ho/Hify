package com.example.demo;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DemoApplicationTests {

    @Test
    void contextLoads() {
        ChatLanguageModel model = OllamaChatModel
                .builder()
                .baseUrl("http://localhost:11434")
                .modelName("deepseek-r1:8b").build();
        String anwser = model.chat("你好");
        System.out.println(anwser);
    }

}
