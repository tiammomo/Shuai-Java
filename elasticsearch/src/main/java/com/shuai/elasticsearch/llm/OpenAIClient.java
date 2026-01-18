package com.shuai.elasticsearch.llm;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * OpenAI API 客户端
 *
 * 模块概述
 * ----------
 * 本模块提供 OpenAI API 的封装，用于 RAG 系统的 LLM 生成功能。
 *
 * 核心内容
 * ----------
 *   - 聊天补全: chatCompletion()
 *   - 流式输出: streamingChat()
 *   - 异步调用: asyncChatCompletion()
 *   - Token 统计: 统计输入输出 token 数
 *
 * 使用示例
 * ----------
 * {@code
 * OpenAIClient client = new OpenAIClient("sk-your-api-key");
 *
 * // 同步调用
 * String response = client.chatCompletion("你好，请介绍一下自己");
 *
 * // 异步调用
 * client.asyncChatCompletion("写一个快速排序算法").thenAccept(System.out::println);
 *
 * // 流式输出
 * client.streamingChat("解释一下什么是向量数据库", chunk -> System.out.print(chunk));
 * }
 *
 * 配置说明
 * ----------
 *   - OPENAI_API_KEY: OpenAI API Key
 *   - OPENAI_MODEL: 模型名称 (默认 gpt-3.5-turbo)
 *   - OPENAI_BASE_URL: API 基础 URL (可选)
 *   - OPENAI_TIMEOUT: 超时时间 (默认 60 秒)
 *
 * @author Shuai
 * @version 1.0
 * @since 2026-01-18
 * @see <a href="https://platform.openai.com/docs/api-reference/chat">OpenAI Chat API</a>
 */
public class OpenAIClient {

    private final String apiKey;
    private final String model;
    private final String baseUrl;
    private final int timeout;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    /**
     * 默认模型
     */
    public static final String DEFAULT_MODEL = "gpt-3.5-turbo";

    /**
     * 默认 API 地址
     */
    public static final String DEFAULT_BASE_URL = "https://api.openai.com/v1";

    /**
     * 默认超时时间 (秒)
     */
    public static final int DEFAULT_TIMEOUT = 60;

    /**
     * 消息角色枚举
     */
    public enum Role {
        SYSTEM("system"),
        USER("user"),
        ASSISTANT("assistant"),
        FUNCTION("function");

        private final String value;

        Role(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    /**
     * 聊天消息
     */
    public static class Message {
        private Role role;
        private String content;
        private String name;

        public Message() {
        }

        public Message(Role role, String content) {
            this.role = role;
            this.content = content;
        }

        public Message(Role role, String content, String name) {
            this.role = role;
            this.content = content;
            this.name = name;
        }

        public static Message system(String content) {
            return new Message(Role.SYSTEM, content);
        }

        public static Message user(String content) {
            return new Message(Role.USER, content);
        }

        public static Message assistant(String content) {
            return new Message(Role.ASSISTANT, content);
        }

        // Getters and Setters
        public Role getRole() { return role; }
        public void setRole(Role role) { this.role = role; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }

    /**
     * 聊天响应
     */
    public static class ChatResponse {
        private String id;
        private String object;
        private long created;
        private String model;
        private List<Choice> choices;
        private Usage usage;

        // Getters and Setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getObject() { return object; }
        public void setObject(String object) { this.object = object; }
        public long getCreated() { return created; }
        public void setCreated(long created) { this.created = created; }
        public String getModel() { return model; }
        public void setModel(String model) { this.model = model; }
        public List<Choice> getChoices() { return choices; }
        public void setChoices(List<Choice> choices) { this.choices = choices; }
        public Usage getUsage() { return usage; }
        public void setUsage(Usage usage) { this.usage = usage; }

        public String getFirstContent() {
            if (choices != null && !choices.isEmpty()) {
                Choice choice = choices.get(0);
                if (choice != null && choice.getMessage() != null) {
                    return choice.getMessage().getContent();
                }
            }
            return null;
        }
    }

    public static class Choice {
        private int index;
        private Message message;
        private String finishReason;

        public int getIndex() { return index; }
        public void setIndex(int index) { this.index = index; }
        public Message getMessage() { return message; }
        public void setMessage(Message message) { this.message = message; }
        public String getFinishReason() { return finishReason; }
        public void setFinishReason(String finishReason) { this.finishReason = finishReason; }
    }

    public static class Usage {
        private int promptTokens;
        private int completionTokens;
        private int totalTokens;

        public int getPromptTokens() { return promptTokens; }
        public void setPromptTokens(int promptTokens) { this.promptTokens = promptTokens; }
        public int getCompletionTokens() { return completionTokens; }
        public void setCompletionTokens(int completionTokens) { this.completionTokens = completionTokens; }
        public int getTotalTokens() { return totalTokens; }
        public void setTotalTokens(int totalTokens) { this.totalTokens = totalTokens; }
    }

    /**
     * 流式响应回调
     */
    @FunctionalInterface
    public interface StreamCallback {
        void onChunk(String content, float progress);
    }

    /**
     * 构造函数
     *
     * @param apiKey OpenAI API Key
     */
    public OpenAIClient(String apiKey) {
        this(apiKey, DEFAULT_MODEL);
    }

    /**
     * 构造函数
     *
     * @param apiKey OpenAI API Key
     * @param model 模型名称
     */
    public OpenAIClient(String apiKey, String model) {
        this(apiKey, model, DEFAULT_BASE_URL);
    }

    /**
     * 构造函数
     *
     * @param apiKey OpenAI API Key
     * @param model 模型名称
     * @param baseUrl API 基础 URL
     */
    public OpenAIClient(String apiKey, String model, String baseUrl) {
        this(apiKey, model, baseUrl, DEFAULT_TIMEOUT);
    }

    /**
     * 完整构造函数
     *
     * @param apiKey OpenAI API Key
     * @param model 模型名称
     * @param baseUrl API 基础 URL
     * @param timeout 超时时间 (秒)
     */
    public OpenAIClient(String apiKey, String model, String baseUrl, int timeout) {
        this.apiKey = apiKey;
        this.model = model;
        this.baseUrl = baseUrl;
        this.timeout = timeout;
        this.httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(timeout))
            .build();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * 聊天补全
     *
     * @param messages 消息列表
     * @return 助手回复内容
     * @throws IOException I/O 异常
     */
    public String chatCompletion(List<Message> messages) throws IOException {
        return chatCompletion(messages, null);
    }

    /**
     * 聊天补全
     *
     * @param messages 消息列表
     * @param systemPrompt 系统提示 (可选)
     * @return 助手回复内容
     * @throws IOException I/O 异常
     */
    public String chatCompletion(List<Message> messages, String systemPrompt) throws IOException {
        List<Message> allMessages = new ArrayList<>();

        if (systemPrompt != null && !systemPrompt.isEmpty()) {
            allMessages.add(Message.system(systemPrompt));
        }
        allMessages.addAll(messages);

        String requestBody = buildRequestBody(allMessages);

        HttpRequest request = buildRequest(requestBody);
        HttpResponse<String> response;
        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("API 调用被中断", e);
        }

        if (response.statusCode() != 200) {
            throw new IOException("API 请求失败: " + response.statusCode() + " - " + response.body());
        }

        ChatResponse chatResponse = parseResponse(response.body());
        return chatResponse.getFirstContent();
    }

    /**
     * 简单聊天
     *
     * @param userMessage 用户消息
     * @return 助手回复
     * @throws IOException I/O 异常
     */
    public String chat(String userMessage) throws IOException {
        List<Message> messages = new ArrayList<>();
        messages.add(Message.user(userMessage));
        return chatCompletion(messages);
    }

    /**
     * 异步聊天补全
     *
     * @param messages 消息列表
     * @return CompletableFuture 异步结果
     */
    public CompletableFuture<String> asyncChatCompletion(List<Message> messages) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return chatCompletion(messages);
            } catch (IOException e) {
                throw new RuntimeException("异步调用失败", e);
            }
        });
    }

    /**
     * 异步简单聊天
     *
     * @param userMessage 用户消息
     * @return CompletableFuture 异步结果
     */
    public CompletableFuture<String> asyncChat(String userMessage) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return chat(userMessage);
            } catch (IOException e) {
                throw new RuntimeException("异步调用失败", e);
            }
        });
    }

    /**
     * 流式聊天补全
     *
     * @param messages 消息列表
     * @param callback 流式回调
     * @throws IOException I/O 异常
     */
    public void streamingChat(List<Message> messages, StreamCallback callback) throws IOException {
        streamingChat(messages, null, callback);
    }

    /**
     * 流式聊天补全
     *
     * @param messages 消息列表
     * @param systemPrompt 系统提示 (可选)
     * @param callback 流式回调
     * @throws IOException I/O 异常
     */
    public void streamingChat(List<Message> messages, String systemPrompt, StreamCallback callback) throws IOException {
        List<Message> allMessages = new ArrayList<>();

        if (systemPrompt != null && !systemPrompt.isEmpty()) {
            allMessages.add(Message.system(systemPrompt));
        }
        allMessages.addAll(messages);

        String requestBody = buildRequestBody(allMessages, true);

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(baseUrl + "/chat/completions"))
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer " + apiKey)
            .POST(HttpRequest.BodyPublishers.ofString(requestBody))
            .build();

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofLines())
            .thenAccept(response -> {
                java.util.List<String> lines = response.body().collect(java.util.stream.Collectors.toList());
                StringBuilder fullResponse = new StringBuilder();
                for (String line : lines) {
                    if (line.startsWith("data: ")) {
                        String data = line.substring(6);
                        if ("[DONE]".equals(data)) {
                            break;
                        }
                        try {
                            String content = parseStreamContent(data);
                            if (content != null && !content.isEmpty()) {
                                fullResponse.append(content);
                                float progress = fullResponse.length() > 0 ? 0.5f : 0.0f;
                                callback.onChunk(content, progress);
                            }
                        } catch (Exception e) {
                            // 忽略解析错误
                        }
                    }
                }
                callback.onChunk(fullResponse.toString(), 1.0f);
            })
            .exceptionally(e -> {
                System.err.println("流式调用错误: " + e.getMessage());
                return null;
            })
            .join();
    }

    /**
     * 获取模型名称
     */
    public String getModel() {
        return model;
    }

    /**
     * 构建请求体
     */
    private String buildRequestBody(List<Message> messages) throws IOException {
        return buildRequestBody(messages, false);
    }

    /**
     * 构建请求体
     */
    private String buildRequestBody(List<Message> messages, boolean stream) throws IOException {
        java.util.Map<String, Object> requestMap = new java.util.HashMap<>();
        requestMap.put("model", model);
        requestMap.put("messages", messages.stream().map(m -> {
            java.util.Map<String, String> msgMap = new java.util.HashMap<>();
            msgMap.put("role", m.getRole().getValue());
            msgMap.put("content", m.getContent());
            if (m.getName() != null) {
                msgMap.put("name", m.getName());
            }
            return msgMap;
        }).collect(Collectors.toList()));
        requestMap.put("stream", stream);
        requestMap.put("max_tokens", 2000);
        requestMap.put("temperature", 0.7);
        return objectMapper.writeValueAsString(requestMap);
    }

    /**
     * 构建 HTTP 请求
     */
    private HttpRequest buildRequest(String requestBody) {
        return HttpRequest.newBuilder()
            .uri(URI.create(baseUrl + "/chat/completions"))
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer " + apiKey)
            .POST(HttpRequest.BodyPublishers.ofString(requestBody))
            .build();
    }

    /**
     * 解析响应
     */
    private ChatResponse parseResponse(String responseBody) throws IOException {
        return objectMapper.readValue(responseBody, ChatResponse.class);
    }

    /**
     * 解析流式内容
     */
    private String parseStreamContent(String data) throws IOException {
        JsonNode node = objectMapper.readTree(data);
        JsonNode choices = node.get("choices");
        if (choices != null && choices.isArray() && choices.size() > 0) {
            JsonNode delta = choices.get(0).get("delta");
            if (delta != null) {
                JsonNode content = delta.get("content");
                if (content != null) {
                    return content.asText();
                }
            }
        }
        return null;
    }

    /**
     * 静态工厂方法 - 创建客户端
     */
    public static OpenAIClient create(String apiKey) {
        return new OpenAIClient(apiKey);
    }

    /**
     * 静态工厂方法 - 创建客户端 (指定模型)
     */
    public static OpenAIClient create(String apiKey, String model) {
        return new OpenAIClient(apiKey, model);
    }
}
