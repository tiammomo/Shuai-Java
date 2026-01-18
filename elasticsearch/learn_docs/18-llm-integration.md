# LLM 集成

> 本章讲解如何集成 OpenAI API 实现 RAG 系统的 LLM 生成功能。

## 学习目标

完成本章学习后，你将能够：
- 理解 OpenAI Chat API 的调用方式
- 实现同步、异步、流式三种调用方式
- 集成 LLM 到 RAG 系统中

## 1. OpenAI API 集成

### 1.1 客户端初始化

**代码位置**: [OpenAIClient.java](src/main/java/com/shuai/elasticsearch/llm/OpenAIClient.java)

```java
// 简单初始化
OpenAIClient client = new OpenAIClient("sk-your-api-key");

// 指定模型
OpenAIClient client = new OpenAIClient("sk-your-api-key", "gpt-4");

// 完整配置
OpenAIClient client = new OpenAIClient(
    "sk-your-api-key",
    "gpt-3.5-turbo",
    "https://api.openai.com/v1",
    60  // 超时秒数
);
```

### 1.2 消息角色

OpenAI API 使用三种消息角色：

| 角色 | 说明 | 示例 |
|------|------|------|
| `system` | 系统提示，定义助手行为 | "你是一个专业的技术顾问" |
| `user` | 用户问题 | "如何配置 Elasticsearch 集群？" |
| `assistant` | 助手回复 | (API 生成) |

**使用示例**:
```java
// 创建消息
Message systemMsg = Message.system("你是一个技术专家，只回答技术相关问题");
Message userMsg = Message.user("请解释一下什么是向量数据库");

List<Message> messages = List.of(userMsg);
```

### 1.3 聊天补全

**同步调用**:
```java
// 简单聊天
String response = client.chat("你好，请介绍一下自己");

// 带系统提示
List<Message> messages = List.of(
    Message.user("北京有哪些好玩的地方？")
);
String response = client.chatCompletion(messages, "你是一个专业的导游");

// 指定系统提示
String response = client.chatCompletion(messages, "回答时使用 Markdown 格式");
```

**响应格式**:
```java
public static class ChatResponse {
    private String id;           // 响应 ID
    private String object;       // 对象类型
    private long created;        // 创建时间戳
    private String model;        // 模型名称
    private List<Choice> choices; // 选择列表
    private Usage usage;         // Token 使用统计

    public String getFirstContent() {
        // 获取第一条消息内容
        return choices.get(0).getMessage().getContent();
    }
}

public static class Usage {
    private int promptTokens;      // 输入 Token 数
    private int completionTokens;  // 输出 Token 数
    private int totalTokens;       // 总 Token 数
}
```

### 1.4 流式输出

流式输出适合长文本生成，可以实时显示生成内容：

```java
List<Message> messages = List.of(
    Message.user("写一篇关于人工智能的文章")
);

client.streamingChat(messages, (chunk, progress) -> {
    System.out.print(chunk);  // 实时输出
    // chunk: 本次收到的文本片段
    // progress: 进度 (0.0-1.0)
});
```

**流式响应格式**:
```
data: {"choices":[{"delta":{"content":"人"},"finish_reason":null}]}
data: {"choices":[{"delta":{"content":"工"},"finish_reason":null}]}
data: {"choices":[{"delta":{"content":"智"},"finish_reason":null}]}
data: {"choices":[{"delta":{"content":"能"},"finish_reason":null}]}
data: {"choices":[{"delta":{},"finish_reason":"stop"}]}
data: [DONE]
```

### 1.5 异步调用

异步调用适合需要并发处理的场景：

```java
// 异步聊天补全
CompletableFuture<String> future = client.asyncChatCompletion(messages);
future.thenAccept(response -> System.out.println(response));

// 异步简单聊天
CompletableFuture<String> future = client.asyncChat("解释一下什么是 RAG");
future.thenAccept(response -> System.out.println(response));

// 组合多个请求
List<CompletableFuture<String>> futures = messagesList.stream()
    .map(client::asyncChatCompletion)
    .collect(Collectors.toList());

CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
    .thenAccept(v -> System.out.println("所有请求完成"));
```

## 2. 在 RAG 中集成 LLM

### 2.1 RAG 生成流程

```
┌─────────────────────────────────────────────────────────────────────┐
│                         RAG 生成流程                                  │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   用户查询                                                           │
│       │                                                             │
│       ▼                                                             │
│   检索相关文档 → 构建议问上下文                                        │
│       │                                                             │
│       ▼                                                             │
│   ┌─────────────────────────────────────────────────────────────┐   │
│   │                     Prompt 构建                              │   │
│   │                                                             │   │
│   │   System: 你是一个技术专家，基于以下参考信息回答问题。        │   │
│   │   --------------------------------------------------------   │   │
│   │   Context:                                                   │   │
│   │   [检索到的文档片段 1]                                       │   │
│   │   [检索到的文档片段 2]                                       │   │
│   │   ...                                                        │   │
│   │   --------------------------------------------------------   │   │
│   │   Question: 用户问题                                         │   │
│   │   Answer:                                                    │   │
│   └─────────────────────────────────────────────────────────────┘   │
│                              │                                      │
│                              ▼                                      │
│                      LLM 生成回答                                    │
│                              │                                      │
│                              ▼                                      │
│                      返回带引用的答案                                 │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### 2.2 RAG 提示词模板

```java
public class RagPromptTemplate {

    private static final String SYSTEM_PROMPT = """
        你是一个技术专家，请基于以下参考信息回答用户的问题。

        规则：
        1. 只使用提供的参考信息回答，不要编造内容
        2. 如果参考信息不足以回答，请明确说明
        3. 回答时使用 Markdown 格式
        4. 在答案末尾标注参考来源
        """;

    private static final String USER_PROMPT_TEMPLATE = """
        ## 参考信息

        %s

        ---

        ## 用户问题

        %s

        ## 回答

        """;

    /**
     * 构建 RAG 提示词
     */
    public static List<Message> build(String context, String question) {
        String userContent = String.format(USER_PROMPT_TEMPLATE, context, question);
        return List.of(
            Message.system(SYSTEM_PROMPT),
            Message.user(userContent)
        );
    }

    /**
     * 构建简单提示词
     */
    public static List<Message> buildSimple(String question) {
        return List.of(Message.user(question));
    }
}
```

### 2.3 完整 RAG 示例

```java
public class RagWithLLMDemo {

    private final OpenAIClient llmClient;
    private final ElasticsearchClient esClient;
    private final BgeEmbeddingService embeddingService;

    public RagWithLLMDemo() {
        this.llmClient = new OpenAIClient(System.getenv("OPENAI_API_KEY"));
        this.esClient = ElasticsearchConfig.getClient();
        this.embeddingService = new BgeEmbeddingService();
    }

    /**
     * RAG 问答
     */
    public String ask(String question) throws IOException {
        // 1. 问题向量化
        List<Float> questionVector = embeddingService.embed(question);

        // 2. 向量检索
        SearchResponse<BlogDocument> response = esClient.search(s -> s
            .index("blog")
            .knn(knn -> knn
                .field("content_vector")
                .k(5)
                .queryVector(questionVector)
            )
        , BlogDocument.class);

        // 3. 提取检索结果
        StringBuilder context = new StringBuilder();
        for (Hit<BlogDocument> hit : response.hits().hits()) {
            BlogDocument doc = hit.source();
            if (doc != null) {
                context.append("- ").append(doc.getContent()).append("\n");
            }
        }

        // 4. 构建提示词
        List<Message> messages = RagPromptTemplate.build(
            context.toString(),
            question
        );

        // 5. 调用 LLM
        String answer = llmClient.chatCompletion(messages);

        return answer;
    }
}
```

## 3. 最佳实践

### 3.1 Token 管理

```java
public class TokenManager {

    private static final int MAX_TOKENS = 4000;  // GPT-3.5-turbo 限制

    /**
     * 估算文本 Token 数 (简单估算)
     */
    public static int estimateTokens(String text) {
        // 英文: 约 4 字符/Token
        // 中文: 约 2 字符/Token
        return text.length() / 2;
    }

    /**
     * 截断文本以适应 Token 限制
     */
    public static String truncate(String text, int maxTokens) {
        int estimatedTokens = estimateTokens(text);
        if (estimatedTokens <= maxTokens) {
            return text;
        }
        int targetLength = maxTokens * 2;
        return text.substring(0, Math.min(targetLength, text.length()));
    }
}
```

### 3.2 错误处理

```java
public class LLMErrorHandler {

    /**
     * 处理 API 错误
     */
    public String handleError(Exception e) {
        if (e instanceof IOException) {
            return "网络错误，请检查连接后重试";
        }
        if (e.getMessage().contains("401")) {
            return "认证失败，请检查 API Key";
        }
        if (e.getMessage().contains("429")) {
            return "请求频率过高，请稍后重试";
        }
        if (e.getMessage().contains("rate limit")) {
            return "超出速率限制，请降低请求频率";
        }
        return "未知错误: " + e.getMessage();
    }

    /**
     * 重试机制
     */
    public String retryWithBackoff(Supplier<String> supplier, int maxRetries) {
        for (int i = 0; i < maxRetries; i++) {
            try {
                return supplier.get();
            } catch (Exception e) {
                if (i == maxRetries - 1) {
                    throw e;
                }
                try {
                    Thread.sleep((long) Math.pow(2, i) * 1000);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException(ie);
                }
            }
        }
        throw new RuntimeException("重试次数耗尽");
    }
}
```

### 3.3 缓存策略

```java
public class LLMResponseCache {

    private static final Map<String, String> cache = new ConcurrentHashMap<>();
    private static final long CACHE_TTL_MS = 60 * 60 * 1000;  // 1 小时

    /**
     * 获取缓存的响应
     */
    public static String get(String question) {
        CacheEntry entry = cache.get(question);
        if (entry != null && !entry.isExpired()) {
            return entry.response;
        }
        return null;
    }

    /**
     * 缓存响应
     */
    public static void put(String question, String response) {
        cache.put(question, new CacheEntry(response, System.currentTimeMillis()));
    }

    private static class CacheEntry {
        final String response;
        final long timestamp;

        CacheEntry(String response, long timestamp) {
            this.response = response;
            this.timestamp = timestamp;
        }

        boolean isExpired() {
            return System.currentTimeMillis() - timestamp > CACHE_TTL_MS;
        }
    }
}
```

## 4. 常见问题

| 问题 | 解决方案 |
|------|----------|
| API 调用超时 | 增加超时时间，使用异步调用 |
| 响应速度慢 | 使用流式输出，开启流式渲染 |
| Token 超出限制 | 截断上下文，减少检索数量 |
| 内容重复 | 使用系统提示避免重复 |
| 幻觉问题 | 明确提示只使用参考信息，增加引用验证 |

## 5. 扩展阅读

**代码位置**:
- [OpenAIClient.java](src/main/java/com/shuai/elasticsearch/llm/OpenAIClient.java) - OpenAI 客户端
- [OpenAIClientTest.java](src/test/java/com/shuai/elasticsearch/llm/OpenAIClientTest.java) - OpenAI 测试
- [RagPromptTemplate.java](src/main/java/com/shuai/elasticsearch/rag/RagPromptTemplate.java) - 提示词模板

- [OpenAI Chat API 文档](https://platform.openai.com/docs/api-reference/chat)
- [OpenAI Token 计算](https://platform.openai.com/tokenizer)
- [Prompt Engineering Guide](https://www.promptingguide.ai/)
- [LangChain LLM 集成](https://python.langchain.com/docs/modules/model_io/llms/)
- [上一章: 两阶段检索](13-two-stage-retrieval.md) | [下一章: 监控告警](19-monitoring.md)

---

**学习进度**: ⬜⬜⬜⬜⬜⬜⬜⬜ 0/19 文档完成

**最后更新**: 2026-01-18
