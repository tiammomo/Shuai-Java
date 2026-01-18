# 两阶段检索

> 本章讲解两阶段检索架构（召回+排序），实现高效的检索增强系统。

## 学习目标

完成本章学习后，你将能够：
- 理解两阶段检索的设计原理
- 掌握 Bi-Encoder 和 Cross-Encoder 的区别
- 实现召回和排序的完整流程
- 配置 RRF 结果融合

## 1. 两阶段检索架构

### 1.1 为什么需要两阶段

```
┌─────────────────────────────────────────────────────────────────────┐
│                    单阶段检索的挑战                                  │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│  场景: 1000 万文档，召回 Top 10                                      │
│                                                                      │
│  问题:                                                              │
│  ├── Cross-Encoder 精度高，但计算成本 O(n)                          │
│  │   └── 1000万 × 512维矩阵乘法 = 不可接受                         │
│  │                                                                │
│  └── 解决方案: 两阶段检索                                            │
│      ├── 召回阶段 (Bi-Encoder): 快速筛选，O(1) 或 O(log n)         │
│      └── 排序阶段 (Cross-Encoder): 精排，O(k)，k << n              │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### 1.2 两阶段流程

```
┌─────────────────────────────────────────────────────────────────────┐
│                       两阶段检索流程                                  │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│                           用户查询                                   │
│                              │                                      │
│                              ▼                                      │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │                    Stage 1: 召回 (Recall)                    │   │
│  │                                                              │   │
│  │   ┌──────────────────────────────────────────────────────┐  │   │
│  │   │                  Bi-Encoder                           │  │   │
│  │   │  ┌─────────┐      ┌─────────┐      ┌─────────┐      │  │   │
│  │   │  │  Query  │  →   │  Encode │  →   │  Query  │      │  │   │
│  │   │  │  Text   │      │  Model  │      │ Vector  │      │  │   │
│  │   │  └─────────┘      └─────────┘      └─────────┘      │  │   │
│  │   └──────────────────────────────────────────────────────┘  │   │
│  │                              │                               │   │
│  │                              ▼                               │   │
│  │   ┌──────────────────────────────────────────────────────┐  │   │
│  │   │                  向量检索                             │  │   │
│  │   │    Milvus / ES (HNSW/IVF)                            │  │   │
│  │   │    快速检索 100-1000 个候选                           │  │   │
│  │   └──────────────────────────────────────────────────────┘  │   │
│  └─────────────────────────────────────────────────────────────┘   │
│                              │                                      │
│                              ▼                                      │
│                      候选文档列表 (100-1000)                         │
│                              │                                      │
│                              ▼                                      │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │                   Stage 2: 排序 (Rank)                      │   │
│  │                                                              │   │
│  │   ┌──────────────────────────────────────────────────────┐  │   │
│  │   │                 Cross-Encoder                         │  │   │
│  │   │  ┌─────────┐      ┌─────────┐      ┌─────────┐      │  │   │
│  │   │  │ Query + │  →   │  Encode │  →   │Score(0-1)│     │  │   │
│  │   │  │  Doc    │      │  Model  │      │         │      │  │   │
│  │   │  └─────────┘      └─────────┘      └─────────┘      │  │   │
│  │   └──────────────────────────────────────────────────────┘  │   │
│  │                              │                               │   │
│  │                              ▼                               │   │
│  │   ┌──────────────────────────────────────────────────────┐  │   │
│  │   │                  结果重排序                           │  │   │
│  │   │    按 Cross-Encoder 分数重新排序                     │  │   │
│  │   └──────────────────────────────────────────────────────┘  │   │
│  └─────────────────────────────────────────────────────────────┘   │
│                              │                                      │
│                              ▼                                      │
│                       最终结果 (Top 10)                              │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### 1.3 Bi-Encoder vs Cross-Encoder

| 特性 | Bi-Encoder | Cross-Encoder |
|------|------------|---------------|
| **结构** | 两个独立编码器 | 共享编码器 |
| **输入** | Query/Doc 分别输入 | Query + Doc 拼接输入 |
| **输出** | 两个向量 | 单一分数 |
| **计算量** | 低 (编码一次) | 高 (每对都要编码) |
| **预计算** | Doc 向量可缓存 | 不可缓存 |
| **精度** | 中等 | 高 |
| **适用** | 大规模召回 | 精排阶段 |

## 2. Cross-Encoder 重排序

### 2.1 重排序服务

```java
// RerankerService.java
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

/**
 * Cross-Encoder 重排序服务
 */
public class RerankerService {

    private static final String DEFAULT_MODEL = "BAAI/bge-reranker-v2-m3";

    private final String modelName;
    private final String apiUrl;
    private String apiKey;
    private final boolean available;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public RerankerService() {
        this.modelName = SiliconFlowConfig.getRerankerModel();
        this.apiUrl = SiliconFlowConfig.getRerankerUrl();

        if (SiliconFlowConfig.isConfigured()) {
            this.apiKey = SiliconFlowConfig.getApiKey();
            this.available = true;
            ResponsePrinter.printMethodInfo("RerankerService",
                "Reranker 服务 (SiliconFlow): " + modelName);
        } else {
            this.apiKey = null;
            this.available = false;
            ResponsePrinter.printMethodInfo("RerankerService",
                "Reranker 服务 (未配置): 使用模拟模式");
        }

        this.httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .build();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * 计算单条 query-document 相关性分数
     */
    public float score(String query, String document) {
        if (!available) {
            return generateMockScore(query, document);
        }

        try {
            List<RerankResult> results = rerank(query, List.of(document), 1);
            if (!results.isEmpty()) {
                return results.get(0).getScore();
            }
        } catch (Exception e) {
            System.err.println("  Reranker 评分失败: " + e.getMessage() + "，使用模拟分数");
        }
        return generateMockScore(query, document);
    }

    /**
     * 对文档列表进行重排序
     */
    public List<RerankResult> rerank(String query, List<String> documents) {
        return rerank(query, documents, documents.size());
    }

    /**
     * 对文档列表进行重排序 (限制返回数量)
     */
    public List<RerankResult> rerank(String query, List<String> documents, int topK) {
        if (documents == null || documents.isEmpty()) {
            return new ArrayList<>();
        }

        if (!available) {
            return generateMockResults(query, documents, topK);
        }

        try {
            return fetchRerank(query, documents, topK);
        } catch (Exception e) {
            System.err.println("  Reranker 失败: " + e.getMessage() + "，使用模拟结果");
            return generateMockResults(query, documents, topK);
        }
    }

    /**
     * 调用 SiliconFlow Rerank API
     */
    private List<RerankResult> fetchRerank(String query, List<String> documents, int topK)
            throws IOException, InterruptedException {

        String requestBody = String.format("""
            {
                "model": "%s",
                "query": "%s",
                "documents": %s,
                "top_n": %d,
                "return_doc": false
            }
            """, modelName, query.replace("\"", "\\\""),
            objectMapper.writeValueAsString(documents), topK);

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(apiUrl))
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer " + apiKey)
            .POST(HttpRequest.BodyPublishers.ofString(requestBody))
            .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IOException("Rerank API 失败: " + response.statusCode() + " - " + response.body());
        }

        JsonNode root = objectMapper.readTree(response.body());
        JsonNode results = root.get("results");

        List<RerankResult> rerankResults = new ArrayList<>();
        for (JsonNode item : results) {
            int index = item.get("index").asInt();
            float score = (float) item.get("relevance_score").asDouble();
            String text = documents.get(index);
            rerankResults.add(new RerankResult(index, text, score));
        }

        return rerankResults;
    }

    private List<RerankResult> generateMockResults(String query, List<String> documents, int topK) {
        List<RerankResult> mockResults = new ArrayList<>();
        for (int i = 0; i < Math.min(documents.size(), topK); i++) {
            mockResults.add(new RerankResult(i, documents.get(i), generateMockScore(query, documents.get(i))));
        }
        mockResults.sort((a, b) -> Float.compare(b.getScore(), a.getScore()));
        return mockResults;
    }

    private float generateMockScore(String query, String document) {
        int hash = (query + ":" + document).hashCode();
        return Math.abs(hash % 10000) / 10000.0f;
    }

    public boolean isAvailable() {
        return available;
    }

    /**
     * 重排序结果
     */
    public static class RerankResult {
        private final int index;
        private final String text;
        private final float score;

        public RerankResult(int index, String text, float score) {
            this.index = index;
            this.text = text;
            this.score = score;
        }

        public int getIndex() { return index; }
        public String getText() { return text; }
        public float getScore() { return score; }

        @Override
        public String toString() {
            return String.format("[%d] score=%.4f: %s...", index, score,
                text.substring(0, Math.min(50, text.length())));
        }
    }
}
```

### 2.2 重排序使用示例

```java
// TwoStageRetrievalDemo.java
package com.shuai.elasticsearch.rag;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.shuai.elasticsearch.config.ElasticsearchConfig;
import com.shuai.elasticsearch.embedding.BgeEmbeddingService;
import com.shuai.elasticsearch.embedding.RerankerService;
import com.shuai.elasticsearch.embedding.RerankerService.RerankResult;
import com.shuai.elasticsearch.model.BlogDocument;
import com.shuai.elasticsearch.util.ResponsePrinter;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class TwoStageRetrievalDemo {

    private final ElasticsearchClient esClient;
    private final BgeEmbeddingService embeddingService;
    private final RerankerService rerankerService;
    private static final String INDEX_NAME = "blog";

    public TwoStageRetrievalDemo() {
        this.esClient = ElasticsearchConfig.getClient();
        this.embeddingService = new BgeEmbeddingService();
        this.rerankerService = new RerankerService();
    }

    /**
     * 运行两阶段检索演示
     */
    public void runAllDemos() throws IOException {
        ResponsePrinter.printMethodInfo("TwoStageRetrievalDemo", "两阶段检索演示");

        System.out.println("\n  两阶段检索流程:");
        System.out.println("  1. 召回阶段: Bi-Encoder 向量检索");
        System.out.println("  2. 排序阶段: Cross-Encoder 重排序");
    }

    /**
     * Stage 1: 召回阶段
     */
    private List<BlogDocument> recallStage(String query, int topK) throws IOException {
        System.out.println("\n  [召回阶段] 查询: " + query);

        // 1. 查询文本向量化
        List<Float> queryVector = embeddingService.embed(query);
        System.out.println("    向量维度: " + queryVector.size());

        // 2. ES 向量搜索 (召回 Top 100)
        SearchResponse<BlogDocument> response = esClient.search(s -> s
            .index(INDEX_NAME)
            .knn(knn -> knn
                .field("title_vector")
                .k(100)
                .queryVector(queryVector)
            )
        , BlogDocument.class);

        // 3. 提取候选文档
        List<BlogDocument> candidates = new ArrayList<>();
        for (Hit<BlogDocument> hit : response.hits().hits()) {
            BlogDocument doc = hit.source();
            if (doc != null) {
                candidates.add(doc);
            }
        }

        System.out.println("    召回数量: " + candidates.size());
        return candidates;
    }

    /**
     * Stage 2: 排序阶段
     */
    private List<RerankResult> rankStage(String query, List<BlogDocument> candidates) {
        System.out.println("\n  [排序阶段] 候选数量: " + candidates.size());

        // 1. 提取文档文本
        List<String> docTexts = candidates.stream()
            .map(doc -> doc.getTitle() + " " + doc.getContent())
            .collect(Collectors.toList());

        // 2. Cross-Encoder 重排序 (Top 20)
        List<RerankResult> reranked = rerankerService.rerank(query, docTexts, 20);

        System.out.println("    精排数量: " + reranked.size());
        return reranked;
    }

    /**
     * 完整两阶段检索流程
     */
    public List<RerankResult> twoStageSearch(String query, int recallK, int rankK) throws IOException {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("  两阶段检索: " + query);
        System.out.println("=".repeat(60));

        // Stage 1: 召回
        List<BlogDocument> candidates = recallStage(query, recallK);

        // Stage 2: 排序
        List<RerankResult> ranked = rankStage(query, candidates);

        // 输出最终结果
        System.out.println("\n  [最终结果] Top " + Math.min(rankK, ranked.size()) + ":");
        int count = 0;
        for (RerankResult result : ranked) {
            if (count >= rankK) break;
            System.out.printf("    %d. [%.4f] %s%n", count + 1, result.getScore(),
                result.getText().substring(0, Math.min(60, result.getText().length())));
            count++;
        }

        return ranked;
    }

    /**
     * 对比实验：召回 vs 排序
     */
    public void comparisonDemo() throws IOException {
        String query = "如何学习分布式系统";

        // 纯召回结果
        System.out.println("\n  [对照组] 纯向量检索结果:");
        SearchResponse<BlogDocument> rawResponse = esClient.search(s -> s
            .index(INDEX_NAME)
            .knn(knn -> knn.field("title_vector").k(10).queryVector(embeddingService.embed(query)))
        , BlogDocument.class);

        for (Hit<BlogDocument> hit : rawResponse.hits().hits()) {
            BlogDocument doc = hit.source();
            if (doc != null) {
                System.out.printf("    ES Score: %.4f - %s%n",
                    hit.score(), doc.getTitle());
            }
        }

        // 两阶段结果
        System.out.println("\n  [实验组] 两阶段检索结果:");
        twoStageSearch(query, 100, 10);
    }
}
```

## 3. RRF 融合算法

### 3.1 RRF 简介

**RRF (Reciprocal Rank Fusion)** 是一种简单的多路召回结果融合算法。

```bash
# RRF 公式
RRF(d) = Σ (1 / (k + rank_i(d)))

其中:
- rank_i(d): 文档 d 在第 i 路检索结果中的排名
- k: 排名折扣参数 (默认 60)
```

### 3.2 RRF 实现

```java
// RRF融合.java
import java.util.*;
import java.util.stream.Collectors;

public class RRF {

    private static final int K = 60;  // RRF 折扣参数

    /**
     * 多路召回结果 RRF 融合
     *
     * @param lists 多路检索结果 (Map<docId, rank>)
     * @return 融合后的排序结果
     */
    public static List<String> fuse(List<Map<String, Integer>> lists) {
        // 1. 收集所有文档
        Set<String> allDocs = new HashSet<>();
        for (Map<String, Integer> list : lists) {
            allDocs.addAll(list.keySet());
        }

        // 2. 计算 RRF 分数
        Map<String, Double> rrfScores = new HashMap<>();
        for (String doc : allDocs) {
            double score = 0;
            for (Map<String, Integer> list : lists) {
                Integer rank = list.get(doc);
                if (rank != null) {
                    score += 1.0 / (K + rank);
                }
            }
            rrfScores.put(doc, score);
        }

        // 3. 按 RRF 分数排序
        return rrfScores.entrySet().stream()
            .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());
    }

    /**
     * 多路检索结果融合示例
     */
    public static void main(String[] args) {
        // ES 检索结果 (docId -> rank)
        Map<String, Integer> esResults = new LinkedHashMap<>();
        esResults.put("doc1", 1);
        esResults.put("doc2", 2);
        esResults.put("doc3", 3);
        esResults.put("doc4", 4);
        esResults.put("doc5", 5);

        // Milvus 向量检索结果
        Map<String, Integer> milvusResults = new LinkedHashMap<>();
        milvusResults.put("doc3", 1);
        milvusResults.put("doc1", 2);
        esResults.put("doc6", 3);
        milvusResults.put("doc2", 4);
        milvusResults.put("doc7", 5);

        // RRF 融合
        List<Map<String, Integer>> lists = Arrays.asList(esResults, milvusResults);
        List<String> fused = RRF.fuse(lists);

        System.out.println("RRF 融合结果:");
        for (int i = 0; i < fused.size(); i++) {
            System.out.printf("  %d. %s%n", i + 1, fused.get(i));
        }
    }
}
```

## 4. 实践部分

### 4.1 完整两阶段检索服务

```java
// HybridSearchDemo.java
package com.shuai.elasticsearch.rag;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.shuai.elasticsearch.config.ElasticsearchConfig;
import com.shuai.elasticsearch.embedding.BgeEmbeddingService;
import com.shuai.elasticsearch.embedding.RerankerService;
import com.shuai.elasticsearch.model.BlogDocument;
import com.shuai.elasticsearch.util.ResponsePrinter;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class HybridSearchDemo {

    private final ElasticsearchClient esClient;
    private final BgeEmbeddingService embeddingService;
    private final RerankerService rerankerService;
    private static final String INDEX_NAME = "blog";

    private static final int RECALL_SIZE = 100;  // 召回数量
    private static final int RERANK_SIZE = 20;   // 重排数量

    public HybridSearchDemo() {
        this.esClient = ElasticsearchConfig.getClient();
        this.embeddingService = new BgeEmbeddingService();
        this.rerankerService = new RerankerService();
    }

    /**
     * 运行混合检索演示
     */
    public void runAllDemos() throws IOException {
        ResponsePrinter.printMethodInfo("HybridSearchDemo", "混合检索演示");

        parallelSearch();
        singleStageSearch();
    }

    /**
     * 并行多路召回 + 重排序
     */
    private void parallelSearch() throws IOException {
        ResponsePrinter.printMethodInfo("parallelSearch", "并行多路检索");

        String query = "Elasticsearch 分布式搜索";

        System.out.println("\n  查询: " + query);

        // 并行执行多路召回
        List<BlogDocument> candidates = new ArrayList<>();

        // 1. ES 文本检索
        SearchResponse<BlogDocument> esResponse = esClient.search(s -> s
            .index(INDEX_NAME)
            .query(q -> q
                .multiMatch(mm -> mm
                    .query(query)
                    .fields(List.of("title^3", "content"))
                )
            )
            .size(RECALL_SIZE)
        , BlogDocument.class);

        Set<String> seenIds = new HashSet<>();
        for (Hit<BlogDocument> hit : esResponse.hits().hits()) {
            BlogDocument doc = hit.source();
            if (doc != null && !seenIds.contains(doc.getId())) {
                candidates.add(doc);
                seenIds.add(doc.getId());
            }
        }
        System.out.println("  ES 召回: " + candidates.size() + " 条");

        // 2. ES 向量检索
        try {
            SearchResponse<BlogDocument> knnResponse = esClient.search(s -> s
                .index(INDEX_NAME)
                .knn(knn -> knn
                    .field("title_vector")
                    .k(RECALL_SIZE)
                    .queryVector(embeddingService.embed(query))
                )
            , BlogDocument.class);

            for (Hit<BlogDocument> hit : knnResponse.hits().hits()) {
                BlogDocument doc = hit.source();
                if (doc != null && !seenIds.contains(doc.getId())) {
                    candidates.add(doc);
                    seenIds.add(doc.getId());
                }
            }
            System.out.println("  向量召回: " + (candidates.size() - seenIds.size()) + " 条");
        } catch (Exception e) {
            System.out.println("  向量检索跳过: " + e.getMessage());
        }

        System.out.println("  总候选: " + candidates.size() + " 条");

        // 3. Cross-Encoder 重排序
        List<RerankerService.RerankResult> reranked = rerankerService.rerank(
            query,
            candidates.stream()
                .map(doc -> doc.getTitle() + " " + doc.getContent())
                .collect(Collectors.toList()),
            RERANK_SIZE
        );

        // 4. 输出结果
        System.out.println("\n  Top 10 结果:");
        for (int i = 0; i < Math.min(10, reranked.size()); i++) {
            RerankerService.RerankResult result = reranked.get(i);
            System.out.printf("    %d. [%.4f] %s%n", i + 1, result.getScore(),
                result.getText().substring(0, Math.min(50, result.getText().length())));
        }
    }

    /**
     * 单阶段对比
     */
    private void singleStageSearch() throws IOException {
        ResponsePrinter.printMethodInfo("singleStageSearch", "单阶段检索对比");

        String query = "Elasticsearch 教程";

        System.out.println("\n  查询: " + query);
        System.out.println("\n  [单阶段] ES 文本检索:");

        SearchResponse<BlogDocument> response = esClient.search(s -> s
            .index(INDEX_NAME)
            .query(q -> q
                .multiMatch(mm -> mm
                    .query(query)
                    .fields(List.of("title^3", "content"))
                )
            )
            .size(10)
        , BlogDocument.class);

        for (Hit<BlogDocument> hit : response.hits().hits()) {
            BlogDocument doc = hit.source();
            if (doc != null) {
                System.out.printf("    [%.4f] %s%n", hit.score(), doc.getTitle());
            }
        }
    }
}
```

## 5. 实践部分

### 5.1 测试类说明

**代码位置**: [QueryDemoTest.java](src/test/java/com/shuai/elasticsearch/query/QueryDemoTest.java)

本测试类验证查询相关功能，包括：

| 测试方法 | 测试内容 |
|----------|----------|
| `testBlogDocumentCreation()` | 测试文档模型创建 |
| `testBlogDocumentTags()` | 测试标签管理 |
| `testBlogDocumentBuilderChain()` | 测试 Builder 链式调用 |
| `testBlogDocumentEquality()` | 测试对象相等性 |

### 5.2 单元测试运行

```bash
# 运行所有测试
mvn test -pl elasticsearch

# 运行查询相关测试
mvn test -pl elasticsearch -Dtest=QueryDemoTest

# 运行测试并生成报告
mvn test -pl elasticsearch -DargLine="-Xmx2g" -Dsurefire.useFile=false
```

### 5.3 测试结果示例

```
QueryDemoTest
├── testBlogDocumentCreation() ✅
├── testBlogDocumentTags() ✅
├── testBlogDocumentBuilderChain() ✅
├── testBlogDocumentEquality() ✅
└── testBlogDocumentToString() ✅

5 tests passed
```

## 6. 常见问题

| 问题 | 原因 | 解决方案 |
|------|------|----------|
| 重排序慢 | Cross-Encoder 调用延迟 | 批量调用，缓存热点 |
| 召回不足 | Bi-Encoder 精度不够 | 增加召回数量 |
| 结果重复 | 多路召回重复 | 去重处理 |
| RRF 不稳定 | 排名变化大 | 增加 k 值 |

## 7. 扩展阅读

**代码位置**:
- [HybridSearchDemo.java](src/main/java/com/shuai/elasticsearch/rag/HybridSearchDemo.java) - 混合检索演示（ES + Milvus）
- [BgeEmbeddingService.java](src/main/java/com/shuai/elasticsearch/embedding/BgeEmbeddingService.java) - Bi-Encoder 召回服务
- [RerankerService.java](src/main/java/com/shuai/elasticsearch/embedding/RerankerService.java) - Cross-Encoder 重排序
- [MilvusQueryDemo.java](src/main/java/com/shuai/elasticsearch/vector/MilvusQueryDemo.java) - Milvus 向量检索

- [RRF Paper](https://plg.uwaterloo.ca/~gvcormac/cormacksigir09-rrf.pdf)
- [Cross-Encoder vs Bi-Encoder](https://www.sbert.net/docs/pretrained_cross-encoders.html)
- [BGE Reranker](https://github.com/FlagOpen/FlagEmbedding)
- [上一章: Embedding 模型](12-embedding-models.md) | [下一章: RAG 概述](14-rag-overview.md)

---

**学习进度**: ⬜⬜⬜⬜⬜⬜⬜⬜⬜⬜ 0/18 文档完成

**最后更新**: 2026-01-18
