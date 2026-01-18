# Embedding 模型

> 本章讲解文本向量化的 Embedding 模型，包括主流模型介绍、API 使用和实践实现。

## 学习目标

完成本章学习后，你将能够：
- 理解 Embedding 模型的基本原理
- 掌握主流 Embedding 模型的特点
- 使用 SiliconFlow API 调用 Embedding 服务
- 实现文本向量化的完整流程

## 1. Embedding 概述

### 1.1 什么是 Embedding

Embedding 是将**离散数据**（如文本、图像）转换为**连续向量**的技术，使得语义相似的内容在向量空间中相近。

```
文本: "人工智能是未来"
         │
         ▼
┌─────────────────────────────────────────────────────────────────────┐
│                     Embedding 模型                                   │
│                                                                      │
│   Input: "人工智能是未来"                                            │
│           │                                                         │
│           ▼                                                         │
│   ┌──────────────────┐                                              │
│   │   Transformer    │  ← BERT / Sentence-BERT / BGE              │
│   │     Encoder      │                                              │
│   └──────────────────┘                                              │
│           │                                                         │
│           ▼                                                         │
│   Output: [0.023, 0.089, 0.156, 0.234, ..., 0.067]  ← 768/1024 维  │
└─────────────────────────────────────────────────────────────────────┘
         │
         ▼
┌─────────────────────────────────────────────────────────────────────┐
│                    向量空间可视化                                     │
│                                                                      │
│   "人工智能"    ────────────►  向量 (0.023, 0.089, ...)            │
│   "机器学习"    ────────────►  向量 (0.031, 0.102, ...)            │
│   "深度学习"    ────────────►  向量 (0.028, 0.095, ...)            │
│                                                                      │
│   相似文本 → 空间中距离更近                                          │
└─────────────────────────────────────────────────────────────────────┘
```

### 1.2 Embedding vs 传统特征

| 特性 | TF-IDF / BM25 | Word2Vec | Embedding (BERT/BGE) |
|------|---------------|----------|---------------------|
| 维度 | 高维稀疏 | 低维稠密 | 高维稠密 |
| 语义理解 | ❌ | 有限 | ✅ 强 |
| 上下文感知 | ❌ | ❌ | ✅ 强 |
| 多语言 | ✅ | 需单独训练 | ✅ 支持 |
| 计算成本 | 低 | 中 | 高 |

### 1.3 Embedding 应用场景

```
┌─────────────────────────────────────────────────────────────────────┐
│                        Embedding 应用场景                            │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│  1. 语义搜索                                                        │
│     "如何学习 Java" → 检索 "Java 入门教程"                          │
│                                                                      │
│  2. 文本相似度                                                      │
│     计算两段文本的语义相似度                                         │
│                                                                      │
│  3. 聚类分析                                                        │
│     将相似文档聚类在一起                                             │
│                                                                      │
│  4. 推荐系统                                                        │
│     基于用户/物品向量进行协同过滤                                     │
│                                                                      │
│  5. 分类任务                                                        │
│     使用 Embedding 作为分类器输入                                    │
│                                                                      │
│  6. RAG (检索增强生成)                                               │
│     文档向量化 → 相似度检索 → LLM 生成                              │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

## 2. 主流 Embedding 模型

### 2.1 模型对比

| 模型 | 维度 | 语言 | 特点 | 适用场景 |
|------|------|------|------|----------|
| **BGE-large-zh-v1.5** | 1024 | 中文 | 效果好 | 中文语义搜索 |
| **BGE-base-zh-v1.5** | 768 | 中文 | 平衡 | 通用场景 |
| **text-embedding-ada-002** | 1536 | 多语言 | OpenAI 官方 | 通用场景 |
| **M3E-large** | 1024 | 中英 | 多语言优化 | 中英混合 |
| **Cohere Embed** | 1024 | 多语言 | 企业级 | 商业应用 |

### 2.2 BGE 模型介绍

**BGE (BAAI General Embedding)** 是北京智源研究院开发的中文 Embedding 模型。

```bash
# BGE 模型特点
├── 多语言支持 (中英为主)
├── 多种维度 (base 768, large 1024)
├── 高性能 (MTEB 中文榜单前列)
└── 开源免费

# 模型版本
├── bge-large-zh-v1.5  ← 推荐，效果最好
├── bge-base-zh-v1.5   ← 平衡性能与速度
└── bge-small-zh-v1.5  ← 轻量级
```

### 2.3 模型选择建议

```
数据量 < 100万:
  ├── 追求效果 → BGE-large-zh-v1.5 (1024维)
  └── 平衡性能 → BGE-base-zh-v1.5 (768维)

数据量 100万-1000万:
  └── BGE-base-zh-v1.5 + 量化

数据量 > 1000万:
  └── M3E-large + IVF 索引
```

## 3. SiliconFlow API 集成

### 3.1 API 配置

```java
// SiliconFlowConfig.java
package com.shuai.elasticsearch.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class SiliconFlowConfig {

    private static final String CONFIG_FILE = "application-siliconflow.properties";

    private static String apiKey;
    private static String embeddingModel;
    private static String embeddingUrl;
    private static String rerankerModel;
    private static String rerankerUrl;

    static {
        loadConfig();
    }

    private static void loadConfig() {
        Properties props = new Properties();
        try (InputStream is = new FileInputStream(CONFIG_FILE)) {
            props.load(is);
            apiKey = props.getProperty("sf.api.key");
            embeddingModel = props.getProperty("sf.embedding.model", "BAAI/bge-large-zh-v1.5");
            embeddingUrl = props.getProperty("sf.embedding.url", "https://api.siliconflow.cn/v1/embeddings");
            rerankerModel = props.getProperty("sf.reranker.model", "BAAI/bge-reranker-v2-m3");
            rerankerUrl = props.getProperty("sf.reranker.url", "https://api.siliconflow.cn/v1/rerank");
        } catch (IOException e) {
            // 使用默认值或模拟模式
            apiKey = null;
        }
    }

    public static String getApiKey() { return apiKey; }
    public static String getEmbeddingModel() { return embeddingModel; }
    public static String getEmbeddingUrl() { return embeddingUrl; }
    public static String getRerankerModel() { return rerankerModel; }
    public static String getRerankerUrl() { return rerankerUrl; }

    public static boolean isConfigured() {
        return apiKey != null && !apiKey.isEmpty();
    }
}
```

### 3.2 Embedding 服务实现

```java
// BgeEmbeddingService.java
package com.shuai.elasticsearch.embedding;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shuai.elasticsearch.config.SiliconFlowConfig;
import com.shuai.elasticsearch.util.ResponsePrinter;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.stream.Collectors;

public class BgeEmbeddingService {

    private final String apiKey;
    private final String modelName;
    private final String apiUrl;
    private final boolean available;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public BgeEmbeddingService() {
        this.apiKey = SiliconFlowConfig.getApiKey();
        this.modelName = SiliconFlowConfig.getEmbeddingModel();
        this.apiUrl = SiliconFlowConfig.getEmbeddingUrl();

        if (SiliconFlowConfig.isConfigured()) {
            this.available = true;
            ResponsePrinter.printMethodInfo("BgeEmbeddingService",
                "BGE Embedding 服务: " + modelName);
        } else {
            this.available = false;
            ResponsePrinter.printMethodInfo("BgeEmbeddingService",
                "BGE Embedding 服务 (未配置): 使用模拟模式");
        }

        this.httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .build();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * 将单条文本转换为向量
     */
    public List<Float> embed(String text) {
        return embed(Collections.singletonList(text)).get(0);
    }

    /**
     * 批量将文本转换为向量
     */
    public List<List<Float>> embed(List<String> texts) {
        if (!available) {
            return generateMockEmbeddings(texts);
        }

        try {
            return fetchEmbeddings(texts);
        } catch (Exception e) {
            System.err.println("  Embedding 失败: " + e.getMessage() + "，使用模拟结果");
            return generateMockEmbeddings(texts);
        }
    }

    /**
     * 调用 SiliconFlow API
     */
    private List<List<Float>> fetchEmbeddings(List<String> texts) throws IOException, InterruptedException {
        String requestBody = String.format("""
            {
                "model": "%s",
                "input": %s,
                "encoding_format": "float"
            }
            """, modelName, objectMapper.writeValueAsString(texts));

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(apiUrl))
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer " + apiKey)
            .POST(HttpRequest.BodyPublishers.ofString(requestBody))
            .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IOException("Embedding API 失败: " + response.statusCode() + " - " + response.body());
        }

        // 解析响应
        JsonNode root = objectMapper.readTree(response.body());
        JsonNode data = root.get("data");

        List<List<Float>> embeddings = new ArrayList<>();
        for (JsonNode item : data) {
            JsonNode embedding = item.get("embedding");
            List<Float> vector = new ArrayList<>();
            for (int i = 0; i < embedding.size(); i++) {
                vector.add((float) embedding.get(i).asDouble());
            }
            embeddings.add(vector);
        }

        return embeddings;
    }

    /**
     * 生成模拟向量 (用于测试)
     */
    private List<List<Float>> generateMockEmbeddings(List<String> texts) {
        Random random = new Random(42);  // 固定种子确保可复现
        List<List<Float>> embeddings = new ArrayList<>();

        for (String text : texts) {
            List<Float> vector = new ArrayList<>();
            int dimension = getModelDimension();
            for (int i = 0; i < dimension; i++) {
                // 基于文本内容生成确定性向量
                int hash = (text + "_" + i).hashCode();
                vector.add(((hash & 0xFFFF) / 65536.0f - 0.5f) * 2);
            }
            embeddings.add(vector);
        }

        return embeddings;
    }

    /**
     * 获取模型维度
     */
    private int getModelDimension() {
        if (modelName.contains("large")) {
            return 1024;
        } else if (modelName.contains("base")) {
            return 768;
        } else if (modelName.contains("small")) {
            return 384;
        }
        return 768;
    }

    /**
     * 计算余弦相似度
     */
    public float cosineSimilarity(List<Float> vec1, List<Float> vec2) {
        float dotProduct = 0;
        float norm1 = 0;
        float norm2 = 0;

        for (int i = 0; i < vec1.size(); i++) {
            dotProduct += vec1.get(i) * vec2.get(i);
            norm1 += vec1.get(i) * vec1.get(i);
            norm2 += vec2.get(i) * vec2.get(i);
        }

        return (float) (dotProduct / Math.sqrt(norm1 * norm2));
    }

    /**
     * 归一化向量
     */
    public List<Float> normalize(List<Float> vector) {
        float norm = 0;
        for (float v : vector) {
            norm += v * v;
        }
        norm = (float) Math.sqrt(norm);

        if (norm == 0) return vector;

        return vector.stream()
            .map(v -> v / norm)
            .collect(Collectors.toList());
    }

    public boolean isAvailable() {
        return available;
    }

    public String getModelName() {
        return modelName;
    }
}
```

### 3.3 使用示例

```java
// EmbeddingDemo.java
package com.shuai.elasticsearch.embedding;

import com.shuai.elasticsearch.util.ResponsePrinter;

import java.util.*;

public class EmbeddingDemo {

    private final BgeEmbeddingService embeddingService;

    public EmbeddingDemo() {
        this.embeddingService = new BgeEmbeddingService();
    }

    /**
     * 运行 Embedding 演示
     */
    public void runAllDemos() {
        ResponsePrinter.printMethodInfo("EmbeddingDemo", "Embedding 模型演示");

        singleTextEmbedding();
        batchTextEmbedding();
        similarityCalculation();
        normalizationDemo();
    }

    /**
     * 单文本向量化
     */
    private void singleTextEmbedding() {
        ResponsePrinter.printMethodInfo("singleTextEmbedding", "单文本向量化");

        String text = "人工智能是未来的发展方向";
        List<Float> vector = embeddingService.embed(text);

        System.out.println("  文本: " + text);
        System.out.println("  向量维度: " + vector.size());
        System.out.println("  前 5 维: " + vector.subList(0, 5));
    }

    /**
     * 批量文本向量化
     */
    private void batchTextEmbedding() {
        ResponsePrinter.printMethodInfo("batchTextEmbedding", "批量文本向量化");

        List<String> texts = Arrays.asList(
            "人工智能是未来的发展方向",
            "机器学习是人工智能的重要分支",
            "深度学习在计算机视觉中应用广泛",
            "自然语言处理研究人与计算机的交互"
        );

        List<List<Float>> vectors = embeddingService.embed(texts);

        System.out.println("  文本数量: " + texts.size());
        System.out.println("  向量维度: " + vectors.get(0).size());

        // 计算两两相似度
        System.out.println("\n  两两相似度矩阵:");
        for (int i = 0; i < texts.size(); i++) {
            System.out.print("    " + texts.get(i).substring(0, 10) + "... : ");
            for (int j = 0; j < texts.size(); j++) {
                float sim = embeddingService.cosineSimilarity(vectors.get(i), vectors.get(j));
                System.out.printf("%.3f ", sim);
            }
            System.out.println();
        }
    }

    /**
     * 相似度计算
     */
    private void similarityCalculation() {
        ResponsePrinter.printMethodInfo("similarityCalculation", "相似度计算");

        String text1 = "如何学习 Java 编程";
        String text2 = "Java 入门教程推荐";
        String text3 = "Python 机器学习";

        List<Float> vec1 = embeddingService.embed(text1);
        List<Float> vec2 = embeddingService.embed(text2);
        List<Float> vec3 = embeddingService.embed(text3);

        float sim12 = embeddingService.cosineSimilarity(vec1, vec2);
        float sim13 = embeddingService.cosineSimilarity(vec1, vec3);

        System.out.println("  \"" + text1 + "\"");
        System.out.println("    vs \"" + text2 + "\": " + String.format("%.4f", sim12));
        System.out.println("    vs \"" + text3 + "\": " + String.format("%.4f", sim13));
    }

    /**
     * 向量归一化
     */
    private void normalizationDemo() {
        ResponsePrinter.printMethodInfo("normalizationDemo", "向量归一化");

        String text = "测试文本";
        List<Float> original = embeddingService.embed(text);
        List<Float> normalized = embeddingService.normalize(original);

        float originalNorm = (float) Math.sqrt(original.stream()
            .mapToDouble(v -> v * v).sum());
        float normalizedNorm = (float) Math.sqrt(normalized.stream()
            .mapToDouble(v -> v * v).sum());

        System.out.println("  原始向量 L2 范数: " + String.format("%.6f", originalNorm));
        System.out.println("  归一化后 L2 范数: " + String.format("%.6f", normalizedNorm));
    }
}
```

### 3.4 配置文件

```properties
# application-siliconflow.properties
# SiliconFlow API 配置

# API Key (必填)
sf.api.key=sk-your-api-key-here

# Embedding 模型配置
sf.embedding.model=BAAI/bge-large-zh-v1.5
sf.embedding.url=https://api.siliconflow.cn/v1/embeddings

# Reranker 模型配置
sf.reranker.model=BAAI/bge-reranker-v2-m3
sf.reranker.url=https://api.siliconflow.cn/v1/rerank

# 其他配置
sf.request.timeout=30
sf.request.max-retries=3
```

## 4. 文本分块策略

### 4.1 分块原因

```
长文档直接 Embedding 的问题:
├── 超出模型最大长度限制 (通常 512-2048 tokens)
├── 稀释重要信息的语义信号
└── 降低检索精度

解决方案: 文本分块 (Text Chunking)
```

### 4.2 分块策略

```java
// TextChunkingUtil.java
public class TextChunkingUtil {

    /**
     * 固定长度分块
     */
    public static List<String> fixedSizeChunk(String text, int chunkSize, int overlap) {
        List<String> chunks = new ArrayList<>();
        int start = 0;

        while (start < text.length()) {
            int end = Math.min(start + chunkSize, text.length());
            chunks.add(text.substring(start, end));
            start = end - overlap;
        }

        return chunks;
    }

    /**
     * 句子级分块
     */
    public static List<String> sentenceChunk(String text) {
        // 使用正则分割句子
        String[] sentences = text.split("[。！？.!?]+");
        List<String> chunks = new ArrayList<>();
        StringBuilder currentChunk = new StringBuilder();

        for (String sentence : sentences) {
            if (currentChunk.length() + sentence.length() > 500) {
                if (currentChunk.length() > 0) {
                    chunks.add(currentChunk.toString().trim());
                }
                currentChunk = new StringBuilder(sentence);
            } else {
                if (currentChunk.length() > 0) {
                    currentChunk.append("。");
                }
                currentChunk.append(sentence);
            }
        }

        if (currentChunk.length() > 0) {
            chunks.add(currentChunk.toString().trim());
        }

        return chunks;
    }

    /**
     * 段落级分块 (带标题)
     */
    public static List<String> paragraphChunk(String text) {
        String[] paragraphs = text.split("\n\n");
        List<String> chunks = new ArrayList<>();

        for (String paragraph : paragraphs) {
            if (!paragraph.trim().isEmpty()) {
                chunks.add(paragraph.trim());
            }
        }

        return chunks;
    }
}
```

## 5. 常见问题

| 问题 | 原因 | 解决方案 |
|------|------|----------|
| API 调用超时 | 网络问题 | 增加超时时间，重试 |
| 维度不匹配 | 模型选择错误 | 确认模型维度配置 |
| 相似度异常 | 向量未归一化 | 使用归一化向量 |
| 分块效果差 | 块大小不合适 | 调整 chunk_size 和 overlap |

## 6. 扩展阅读

**代码位置**:
- [BgeEmbeddingService.java](src/main/java/com/shuai/elasticsearch/embedding/BgeEmbeddingService.java) - BGE Embedding 服务（真实调用 SiliconFlow API）
- [SiliconFlowConfig.java](src/main/java/com/shuai/elasticsearch/config/SiliconFlowConfig.java) - SiliconFlow API 配置
- [RerankerService.java](src/main/java/com/shuai/elasticsearch/embedding/RerankerService.java) - Cross-Encoder 重排序服务
- [VectorEmbeddingUtil.java](src/main/java/com/shuai/elasticsearch/util/VectorEmbeddingUtil.java) - 向量工具类
- [BlogDataGenerator.java](src/main/java/com/shuai/elasticsearch/data/BlogDataGenerator.java) - 测试数据生成器（13篇高质量文档）

- [BGE Model](https://github.com/FlagOpen/FlagEmbedding)
- [SiliconFlow API](https://cloud.siliconflow.cn/docs)
- [Sentence-BERT](https://www.sbert.net/)
- [HuggingFace Transformers](https://huggingface.co/docs/transformers)
- [上一章: 向量检索](11-vector-search.md) | [下一章: 两阶段检索](13-two-stage-retrieval.md)

---

**学习进度**: ⬜⬜⬜⬜⬜⬜⬜⬜ 0/18 文档完成

**最后更新**: 2026-01-18
