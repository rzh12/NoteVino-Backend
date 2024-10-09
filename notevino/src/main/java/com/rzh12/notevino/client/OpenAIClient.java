package com.rzh12.notevino.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
public class OpenAIClient {

    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${openai.api.url}")
    private String apiUrl;

    // A general method for generating API requests.
    public String generateResponse(String systemPrompt, String userQuery, String assistantMessageContent) {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build();

        ObjectMapper objectMapper = new ObjectMapper();

        try {
            // The system message section
            Map<String, Object> systemMessage = new HashMap<>();
            systemMessage.put("role", "system");
            systemMessage.put("content", systemPrompt);

            // The user message section
            Map<String, Object> userMessage = new HashMap<>();
            userMessage.put("role", "user");
            userMessage.put("content", userQuery);

            // The assistant message section
            Map<String, Object> assistantMessage = new HashMap<>();
            assistantMessage.put("role", "assistant");
            assistantMessage.put("content", assistantMessageContent);

            // Request Body
            Map<String, Object> requestBodyMap = new HashMap<>();
            requestBodyMap.put("model", "gpt-4o-mini");
            requestBodyMap.put("messages", new Object[]{systemMessage, userMessage, assistantMessage});
            requestBodyMap.put("max_tokens", 1000);

            String requestBody = objectMapper.writeValueAsString(requestBodyMap);

            RequestBody body = RequestBody.create(MediaType.parse("application/json"), requestBody);
            Request request = new Request.Builder()
                    .url(apiUrl)
                    .post(body)
                    .addHeader("Authorization", "Bearer " + apiKey)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    return response.body().string();
                } else {
                    System.out.println("回應碼: " + response.code());
                    System.out.println("回應消息: " + response.message());
                    return "請求失敗: " + response.code();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "發生錯誤。";
        }
    }
}
