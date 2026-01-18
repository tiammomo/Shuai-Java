# 10 - 大模型应用中的缓存策略

## 10.1 概述

在大模型（LLM）应用中，API 调用成本高、响应延迟大，合理使用缓存可以显著降低成本、提升响应速度。Caffeine 作为高性能本地缓存，是大模型应用的理想选择。

### 大模型应用痛点

| 痛点 | 影响 | 缓存解决方案 |
|------|------|--------------|
| API 成本高 | 每次调用收费 | 缓存相同请求 |
| 响应延迟大 | 几秒到几十秒 | 缓存热点结果 |
| Token 消耗大 | 按 Token 计费 | 缓存 tokenization |
| 重复查询多 | 相同问题重复问 | 缓存问答对 |

---

## 10.2 API 响应缓存

### 场景描述

对于相同的 prompt，大模型返回的结果往往相似。可以缓存 API 响应，避免重复调用。

```java
LoadingCache<String, String> responseCache = Caffeine.newBuilder()
    .maximumSize(1000)
    .expireAfterWrite(Duration.ofHours(24))  // 24小时过期
    .build(prompt -> openaiApi.call(prompt));
```

### 实现要点

- 使用 prompt 的哈希值或规范化文本作为 key
- 设置合理的过期时间（根据业务需求）
- 考虑缓存穿透问题（相同 prompt 返回空结果）

详见 [LoadingCacheDemo.java:51-58](../src/main/java/com/shuai/caffeine/demo/loading/LoadingCacheDemo.java#L51-L58)

---

## 10.3 Tokenization 缓存

### 场景描述

大模型使用 token 而非单词计价，重复 tokenization 浪费计算资源。

```java
Cache<String, List<Integer>> tokenCache = Caffeine.newBuilder()
    .maximumSize(10000)
    .expireAfterWrite(Duration.ofHours(1))
    .build();

// 使用
List<Integer> tokens = tokenCache.getIfPresent(text);
if (tokens == null) {
    tokens = tokenizer.encode(text);
    tokenCache.put(text, tokens);
}
```

### 适用场景

| 场景 | 缓存内容 | 收益 |
|------|----------|------|
| 提示词模板 | 预编码的模板 tokens | 减少每次编码开销 |
| 常用短语 | 常见问候语、指令 | 显著降低延迟 |
| 系统提示词 | 固定的 system prompt | 稳定减少开销 |

---

## 10.4 Embedding 缓存

### 场景描述

文本 embedding 是 RAG 的核心操作，相同文本的 embedding 可重复使用。

```java
LoadingCache<String, float[]> embeddingCache = Caffeine.newBuilder()
    .maximumSize(10000)
    .expireAfterWrite(Duration.ofDays(7))  // embedding 相对稳定
    .build(text -> embeddingService.encode(text));
```

### 使用场景

| 场景 | 缓存内容 | 收益 |
|------|----------|------|
| 文档分块 | 文档片段 embedding | 加速检索 |
| 用户查询 | 查询文本 embedding | 减少重复计算 |
| 知识库 | 静态知识 embedding | 长期复用 |

详见 [LoadingCacheDemo.java:79-96](../src/main/java/com/shuai/caffeine/demo/loading/LoadingCacheDemo.java#L79-L96)

---

## 10.5 RAG 检索缓存

### 场景描述

RAG（检索增强生成）中，相同查询可能多次检索知识库。

```java
LoadingCache<String, List<Document>> retrievalCache = Caffeine.newBuilder()
    .maximumSize(1000)
    .expireAfterWrite(Duration.ofHours(1))
    .build(query -> vectorStore.similaritySearch(query));
```

### 缓存策略

| 数据类型 | 推荐配置 | 说明 |
|----------|----------|------|
| 热门查询 | maximumSize(500), expireAfterWrite(1h) | 高频查询 |
| 精确匹配 | expireAfterAccess(30min) | 基于访问时间 |
| 长尾查询 | maximumWeight(100MB) | 限制内存占用 |

详见 [WeightBasedEviction.java:52-60](../src/main/java/com/shuai/caffeine/demo/eviction/impl/WeightBasedEviction.java#L52-L60)

---

## 10.6 对话历史缓存

### 场景描述

多轮对话中需要维护和检索历史上下文。

```java
Cache<String, List<Message>> conversationCache = Caffeine.newBuilder()
    .maximumSize(1000)
    .expireAfterAccess(Duration.ofMinutes(30))  // 30分钟不活跃则过期
    .removalListener((key, value, cause) -> {
        if (cause == RemovalCause.EXPIRED) {
            log.info("Conversation {} expired", key);
        }
    })
    .build();
```

详见 [ReferenceEviction.java:52-65](../src/main/java/com/shuai/caffeine/demo/eviction/impl/ReferenceEviction.java#L52-L65)

---

## 10.7 模型配置缓存

### 场景描述

缓存模型配置、参数模板等静态配置。

```java
// 预定义的模型配置
Cache<String, ModelConfig> configCache = CacheConfigs.largeCache();

// 自定义配置
Cache<String, GenerationConfig> genConfigCache = Caffeine.newBuilder()
    .maximumSize(100)
    .expireAfterWrite(Duration.ofDays(1))
    .recordStats()
    .build();
```

详见 [CacheConfigs.java:49-55](../src/main/java/com/shuai/caffeine/config/CacheConfigs.java#L49-L55)

---

## 10.8 代码位置汇总

| 功能 | 文件位置 |
|------|----------|
| LoadingCache 使用 | [LoadingCacheDemo.java](../src/main/java/com/shuai/caffeine/demo/loading/LoadingCacheDemo.java) |
| 权重缓存 | [WeightBasedEviction.java](../src/main/java/com/shuai/caffeine/demo/eviction/impl/WeightBasedEviction.java) |
| 访问过期 | [ExpiryConfigs.java:31-34](../src/main/java/com/shuai/caffeine/config/ExpiryConfigs.java#L31-L34) |
| 淘汰监听 | [RemovalListenerDemo.java](../src/main/java/com/shuai/caffeine/demo/eviction/impl/RemovalListenerDemo.java) |
| 统计监控 | [StatsDemo.java](../src/main/java/com/shuai/caffeine/demo/stats/StatsDemo.java) |

---

## 10.9 最佳实践

### 缓存策略设计

```java
// 推荐：使用 LoadingCache 自动加载
LoadingCache<String, Response> chatCache = Caffeine.newBuilder()
    .maximumSize(10000)                    // 热门缓存
    .expireAfterWrite(Duration.ofHours(1)) // 1小时过期
    .refreshAfterWrite(Duration.ofMinutes(10)) // 10分钟后可刷新
    .recordStats()                          // 监控命中率
    .build(this::callLLM);

// 监控命中率
CacheStats stats = chatCache.stats();
if (stats.hitRate() < 0.5) {
    // 命中率过低，考虑调整策略
}
```

详见 [StatsEnableDemo.java:43-48](../src/main/java/com/shuai/caffeine/demo/stats/impl/StatsEnableDemo.java#L43-L48)

### 配置建议

| 场景 | maximumSize | expireAfterWrite | recordStats |
|------|-------------|------------------|-------------|
| API 响应缓存 | 1000-10000 | 1-24 小时 | ✅ |
| Tokenization | 5000-20000 | 30-60 分钟 | ✅ |
| Embedding | 5000-20000 | 1-7 天 | ✅ |
| 对话历史 | 500-2000 | 30-60 分钟 | ❌ |

---

## 10.10 常见问题

### Q1: 缓存命中率低

**原因**: key 设计不合理（过于具体）或数据重复率低

**解决**:
```java
// 规范化 prompt（去除时间戳等变量）
String normalizePrompt(String prompt) {
    return prompt.replaceAll("\\d{10,}", "<TIMESTAMP>")
                 .replaceAll("\\s+", " ")
                 .trim();
}
```

### Q2: 缓存过期时间设置

**建议**:
- 高频场景：30分钟-1小时
- 低频场景：4-24小时
- 静态数据：7天或更长

### Q3: 内存占用过高

**解决**: 使用权重缓存
```java
Caffeine.newBuilder()
    .maximumWeight(100 * 1024 * 1024)  // 100MB
    .weigher((key, value) -> ((String) value).length())
    .build();
```

详见 [WeightBasedEviction.java:52-60](../src/main/java/com/shuai/caffeine/demo/eviction/impl/WeightBasedEviction.java#L52-L60)

---

## 10.11 性能对比

### 预估收益

| 操作 | 无缓存 | 有缓存 | 提升 |
|------|--------|--------|------|
| API 调用成本 | $1/100次 | $0.1/100次 | 90% |
| 响应延迟 | 2-5s | <50ms | 40-100x |
| Token 消耗 | 1000 tokens | 50 tokens | 95% |

---

## 10.12 课后练习

1. 为项目的 API 调用实现响应缓存
2. 分析历史日志，计算潜在缓存命中率
3. 监控缓存命中率，优化配置参数

---

## 相关资源

- [08 - 最佳实践](08-best-practices.md) - 通用缓存最佳实践
- [05 - 淘汰策略](05-eviction.md) - 缓存淘汰策略详解
- [06 - 统计与监控](06-stats.md) - 缓存监控方案

---

## 参考链接

- [Caffeine GitHub](https://github.com/ben-manes/caffeine)
- [LangChain Cache](https://python.langchain.com/docs/modules/memory/cache)
- [OpenAI API Best Practices](https://platform.openai.com/docs/guides/rate-limits)
