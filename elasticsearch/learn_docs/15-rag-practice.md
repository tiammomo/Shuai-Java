# RAG 实践

> 本章通过实际项目代码讲解 RAG 系统的完整实现，包括知识库构建、检索优化和效果评估。

## 学习目标

完成本章学习后，你将能够：
- 实现完整的 RAG 系统架构
- 掌握文档处理和向量化流程
- 实现查询理解和结果优化
- 构建 RAG 评估和监控体系

## 1. 整体架构设计

### 1.1 系统架构

```
┌─────────────────────────────────────────────────────────────────────┐
│                        RAG 系统架构                                   │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │                     API Gateway                              │   │
│  │                  (Spring Cloud Gateway)                      │   │
│  └─────────────────────────────────────────────────────────────┘   │
│                              │                                      │
│         ┌────────────────────┼────────────────────┐                 │
│         ▼                    ▼                    ▼                 │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐            │
│  │   知识库    │    │   检索服务   │    │   问答服务   │            │
│  │   管理模块   │    │   (Recall)  │    │   (RAG API) │            │
│  └─────────────┘    └─────────────┘    └─────────────┘            │
│         │                    │                    │                 │
│         ▼                    ▼                    ▼                 │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │                     向量数据库 (Milvus)                       │   │
│  │                     + Elasticsearch                           │   │
│  └─────────────────────────────────────────────────────────────┘   │
│                              │                                      │
│                              ▼                                      │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │                     Embedding 服务                           │   │
│  │                  (SiliconFlow BGE API)                       │   │
│  └─────────────────────────────────────────────────────────────┘   │
│                              │                                      │
│                              ▼                                      │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │                     LLM 服务                                 │   │
│  │               (OpenAI / Claude / 文心一言)                   │   │
│  └─────────────────────────────────────────────────────────────┘   │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### 1.2 模块划分

```
com.shuai.elasticsearch.rag/
├── RagApplication.java           # 启动类
├── controller/
│   └── RagController.java        # REST API 控制器
├── service/
│   ├── RagService.java           # RAG 主服务
│   ├── RetrievalService.java     # 检索服务
│   ├── KnowledgeBaseService.java # 知识库管理
│   └── LlmService.java           # LLM 调用
├── embedding/
│   ├── BgeEmbeddingService.java  # Embedding 服务
│   └── RerankerService.java      # 重排序服务
├── model/
│   ├── RagRequest.java           # 请求模型
│   ├── RagResponse.java          # 响应模型
│   └── KnowledgeDoc.java         # 知识文档
└── util/
    ├── DocumentProcessor.java    # 文档处理
    └── TextChunkingUtil.java     # 文本分块
```

## 2. 核心实现代码

### 2.1 RAG 主服务

```java
// RagService.java
package com.shuai.elasticsearch.rag.service;

import com.shuai.elasticsearch.embedding.BgeEmbeddingService;
import com.shuai.elasticsearch.embedding.RerankerService;
import com.shuai.elasticsearch.embedding.RerankerService.RerankResult;
import com.shuai.elasticsearch.rag.model.RagRequest;
import com.shuai.elasticsearch.rag.model.RagResponse;
import com.shuai.elasticsearch.util.ResponsePrinter;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * RAG 核心服务
 */
@Service
public class RagService {

    private final RetrievalService retrievalService;
    private final LlmService llmService;
    private final BgeEmbeddingService embeddingService;
    private final RerankerService rerankerService;

    public RagService(RetrievalService retrievalService,
                      LlmService llmService,
                      BgeEmbeddingService embeddingService,
                      RerankerService rerankerService) {
        this.retrievalService = retrievalService;
        this.llmService = llmService;
        this.embeddingService = embeddingService;
        this.rerankerService = rerankerService;
    }

    /**
     * 问答入口
     */
    public RagResponse ask(RagRequest request) {
        long startTime = System.currentTimeMillis();
        ResponsePrinter.printMethodInfo("RagService", "RAG 问答");

        String question = request.getQuestion();
        System.out.println("  问题: " + question);

        // 1. 查询理解
        String processedQuery = preprocessQuery(question);
        System.out.println("  处理后: " + processedQuery);

        // 2. 向量检索
        List<RetrievalService.RetrievalResult> candidates =
            retrievalService.retrieve(processedQuery, request.getTopK());
        System.out.println("  召回数量: " + candidates.size());

        // 3. 提取文档内容
        List<String> docTexts = candidates.stream()
            .map(RetrievalService.RetrievalResult::getContent)
            .collect(Collectors.toList());

        // 4. Cross-Encoder 重排序
        List<RerankResult> reranked = rerankerService.rerank(
            processedQuery, docTexts, request.getRerankTopK());

        // 5. 构建上下文
        String context = buildContext(reranked, request.getMaxContextLength());

        // 6. LLM 生成
        String answer = llmService.generate(request.getQuestion(), context);

        long elapsed = System.currentTimeMillis() - startTime;

        // 7. 构建响应
        RagResponse response = new RagResponse();
        response.setQuestion(request.getQuestion());
        response.setAnswer(answer);
        response.setRetrievedDocs(reranked.size());
        response.setElapsedMs(elapsed);

        // 添加引用来源
        List<RagResponse.Source> sources = reranked.stream()
            .limit(5)
            .map(r -> {
                RagResponse.Source source = new RagResponse.Source();
                source.setContent(r.getText().substring(0,
                    Math.min(100, r.getText().length())));
                source.setScore(r.getScore());
                return source;
            })
            .collect(Collectors.toList());
        response.setSources(sources);

        return response;
    }

    /**
     * 查询预处理
     */
    private String preprocessQuery(String query) {
        // 1. 去除多余空白
        String cleaned = query.trim().replaceAll("\\s+", " ");

        // 2. 扩展查询 (可选)
        // cleaned = queryExpansion(cleaned);

        return cleaned;
    }

    /**
     * 构建上下文
     */
    private String buildContext(List<RerankResult> results, int maxLength) {
        StringBuilder context = new StringBuilder();
        int currentLength = 0;

        for (RerankResult result : results) {
            String text = result.getText();
            if (currentLength + text.length() > maxLength) {
                break;
            }
            context.append(text).append("\n\n");
            currentLength += text.length();
        }

        return context.toString().trim();
    }

    /**
     * 批量问答
     */
    public List<RagResponse> batchAsk(List<RagRequest> requests) {
        return requests.stream()
            .map(this::ask)
            .collect(Collectors.toList());
    }
}
```

### 2.2 检索服务

```java
// RetrievalService.java
package com.shuai.elasticsearch.rag.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.shuai.elasticsearch.config.ElasticsearchConfig;
import com.shuai.elasticsearch.embedding.BgeEmbeddingService;
import com.shuai.elasticsearch.model.BlogDocument;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 检索服务 - 负责向量检索和混合检索
 */
@Service
public class RetrievalService {

    private final ElasticsearchClient esClient;
    private final BgeEmbeddingService embeddingService;
    private static final String INDEX_NAME = "blog";
    private static final String VECTOR_FIELD = "title_vector";

    public RetrievalService() {
        this.esClient = ElasticsearchConfig.getClient();
        this.embeddingService = new BgeEmbeddingService();
    }

    /**
     * 检索入口
     */
    public List<RetrievalResult> retrieve(String query, int topK) {
        try {
            // 1. 问题向量化
            List<Float> queryVector = embeddingService.embed(query);

            // 2. 向量检索 (Milvus 风格调用 ES)
            return vectorSearch(queryVector, topK);

        } catch (Exception e) {
            System.err.println("  检索失败: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * 向量检索
     */
    private List<RetrievalResult> vectorSearch(List<Float> queryVector, int topK) {
        try {
            SearchResponse<BlogDocument> response = esClient.search(s -> s
                .index(INDEX_NAME)
                .knn(knn -> knn
                    .field(VECTOR_FIELD)
                    .k(topK)
                    .queryVector(queryVector)
                )
                .size(topK)
            , BlogDocument.class);

            List<RetrievalResult> results = new ArrayList<>();
            for (Hit<BlogDocument> hit : response.hits().hits()) {
                BlogDocument doc = hit.source();
                if (doc != null) {
                    RetrievalResult result = new RetrievalResult();
                    result.setId(hit.id());
                    result.setContent(doc.getTitle() + " " + doc.getContent());
                    result.setScore(hit.score() != null ? hit.score() : 0.0f);
                    results.add(result);
                }
            }

            return results;

        } catch (IOException e) {
            System.err.println("  ES 查询失败: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * 混合检索 (向量 + 关键词)
     */
    public List<RetrievalResult> hybridSearch(String query, int topK) {
        // 1. 并行执行两种检索
        List<RetrievalResult> vectorResults = vectorSearch(
            embeddingService.embed(query), topK);
        List<RetrievalResult> keywordResults = keywordSearch(query, topK);

        // 2. 结果融合 (RRF)
        return fuseResults(vectorResults, keywordResults, topK);
    }

    /**
     * 关键词检索
     */
    private List<RetrievalResult> keywordSearch(String query, int topK) {
        try {
            SearchResponse<BlogDocument> response = esClient.search(s -> s
                .index(INDEX_NAME)
                .query(q -> q
                    .multiMatch(mm -> mm
                        .query(query)
                        .fields(List.of("title^3", "content"))
                    )
                )
                .size(topK)
            , BlogDocument.class);

            List<RetrievalResult> results = new ArrayList<>();
            for (Hit<BlogDocument> hit : response.hits().hits()) {
                BlogDocument doc = hit.source();
                if (doc != null) {
                    RetrievalResult result = new RetrievalResult();
                    result.setId(hit.id());
                    result.setContent(doc.getTitle() + " " + doc.getContent());
                    result.setScore(hit.score() != null ? hit.score() : 0.0f);
                    results.add(result);
                }
            }

            return results;

        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    /**
     * RRF 融合
     */
    private List<RetrievalResult> fuseResults(
            List<RetrievalResult> vectorResults,
            List<RetrievalResult> keywordResults,
            int topK) {

        // 构建排名映射
        Map<String, Integer> vectorRanks = new HashMap<>();
        for (int i = 0; i < vectorResults.size(); i++) {
            vectorRanks.put(vectorResults.get(i).getId(), i + 1);
        }

        Map<String, Integer> keywordRanks = new HashMap<>();
        for (int i = 0; i < keywordResults.size(); i++) {
            keywordRanks.put(keywordResults.get(i).getId(), i + 1);
        }

        // 计算 RRF 分数
        Map<String, Double> rrfScores = new HashMap<>();
        Set<String> allIds = new HashSet<>();
        allIds.addAll(vectorRanks.keySet());
        allIds.addAll(keywordRanks.keySet());

        for (String id : allIds) {
            double score = 0;
            if (vectorRanks.containsKey(id)) {
                score += 1.0 / (60 + vectorRanks.get(id));
            }
            if (keywordRanks.containsKey(id)) {
                score += 1.0 / (60 + keywordRanks.get(id));
            }
            rrfScores.put(id, score);
        }

        // 排序并返回
        return rrfScores.entrySet().stream()
            .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
            .limit(topK)
            .map(e -> {
                RetrievalResult r = new RetrievalResult();
                r.setId(e.getKey());
                r.setScore(e.getValue().floatValue());
                return r;
            })
            .collect(Collectors.toList());
    }

    /**
     * 检索结果
     */
    public static class RetrievalResult {
        private String id;
        private String content;
        private float score;

        // getters and setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public float getScore() { return score; }
        public void setScore(float score) { this.score = score; }
    }
}
```

### 2.3 LLM 服务

```java
// LlmService.java
package com.shuai.elasticsearch.rag.service;

import com.shuai.elasticsearch.config.SiliconFlowConfig;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shuai.elasticsearch.util.ResponsePrinter;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * LLM 调用服务
 */
@Service
public class LlmService {

    private static final String DEFAULT_MODEL = "deepseek-ai/DeepSeek-V2.5";

    private final String apiUrl;
    private final String apiKey;
    private final boolean available;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public LlmService() {
        this.apiUrl = SiliconFlowConfig.getLlmUrl();
        this.apiKey = SiliconFlowConfig.getApiKey();

        if (SiliconFlowConfig.isConfigured()) {
            this.available = true;
            ResponsePrinter.printMethodInfo("LlmService", "LLM 服务: " + DEFAULT_MODEL);
        } else {
            this.available = false;
            ResponsePrinter.printMethodInfo("LlmService", "LLM 服务 (未配置): 使用模拟模式");
        }

        this.httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .build();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * 生成回答
     */
    public String generate(String question, String context) {
        if (!available) {
            return generateMockAnswer(question, context);
        }

        try {
            return callLlm(question, context);
        } catch (Exception e) {
            System.err.println("  LLM 调用失败: " + e.getMessage() + "，使用模拟回答");
            return generateMockAnswer(question, context);
        }
    }

    /**
     * 调用 LLM API
     */
    private String callLlm(String question, String context) throws IOException, InterruptedException {
        String prompt = buildPrompt(question, context);

        String requestBody = String.format("""
            {
                "model": "%s",
                "messages": [
                    {"role": "system", "content": "你是一个helpful助手，基于提供的上下文回答问题。如果上下文中没有相关信息，请明确告知。"},
                    {"role": "user", "content": %s}
                ],
                "temperature": 0.7,
                "max_tokens": 2000
            }
            """, DEFAULT_MODEL, objectMapper.writeValueAsString(prompt));

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(apiUrl))
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer " + apiKey)
            .POST(HttpRequest.BodyPublishers.ofString(requestBody))
            .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IOException("LLM API 失败: " + response.body());
        }

        JsonNode root = objectMapper.readTree(response.body());
        return root.get("choices").get(0).get("message").get("content").asText();
    }

    /**
     * 构建 Prompt
     */
    private String buildPrompt(String question, String context) {
        return String.format("""
            基于以下参考信息回答用户问题。如果参考信息中没有相关内容，请直接说明。

            ## 参考信息:
            %s

            ## 用户问题:
            %s

            ## 回答要求:
            1. 直接基于参考信息回答，不要添加未提及的内容
            2. 如果参考信息不完整，说明局限性
            3. 回答要简洁明了
            """, context, question);
    }

    /**
     * 模拟回答
     */
    private String generateMockAnswer(String question, String context) {
        return String.format("""
            基于检索到的信息，我来回答您的问题: "%s"

            ## 参考内容摘要:
            %s

            ## 回答:
            (注: 此为模拟回答，实际系统会调用 LLM 生成更准确、流畅的回答)

            如需获取完整答案，请配置 LLM API (如 OpenAI、Claude 或 SiliconFlow)。

            提示: 当前配置 SiliconFlow 后可使用 DeepSeek 等模型生成高质量回答。
            """, question, context.length() > 200 ? context.substring(0, 200) + "..." : context);
    }
}
```

## 3. 知识库管理

### 3.1 知识文档管理

```java
// KnowledgeBaseService.java
package com.shuai.elasticsearch.rag.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.shuai.elasticsearch.config.ElasticsearchConfig;
import com.shuai.elasticsearch.embedding.BgeEmbeddingService;
import com.shuai.elasticsearch.rag.model.KnowledgeDoc;
import com.shuai.elasticsearch.util.DocumentProcessor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 知识库管理服务
 */
@Service
public class KnowledgeBaseService {

    private final ElasticsearchClient esClient;
    private final BgeEmbeddingService embeddingService;
    private final DocumentProcessor documentProcessor;
    private static final String INDEX_NAME = "knowledge_base";

    public KnowledgeBaseService() {
        this.esClient = ElasticsearchConfig.getClient();
        this.embeddingService = new BgeEmbeddingService();
        this.documentProcessor = new DocumentProcessor();
    }

    /**
     * 添加文档到知识库
     */
    public String addDocument(KnowledgeDoc doc) throws IOException {
        // 1. 文本分块
        List<String> chunks = documentProcessor.chunk(doc.getContent());

        // 2. 为每个 chunk 生成向量
        List<List<Float>> vectors = embeddingService.embed(chunks);

        // 3. 存储到 ES
        String docId = UUID.randomUUID().toString();

        for (int i = 0; i < chunks.size(); i++) {
            Map<String, Object> document = new HashMap<>();
            document.put("title", doc.getTitle());
            document.put("content", chunks.get(i));
            document.put("vector", vectors.get(i));
            document.put("source", doc.getSource());
            document.put("metadata", doc.getMetadata());

            IndexResponse response = esClient.index(i -> i
                .index(INDEX_NAME)
                .id(docId + "_" + i)
                .document(document)
            );
        }

        return docId;
    }

    /**
     * 批量添加文档
     */
    public void addDocuments(List<KnowledgeDoc> docs) throws IOException {
        for (KnowledgeDoc doc : docs) {
            addDocument(doc);
        }
    }

    /**
     * 搜索知识库
     */
    public List<KnowledgeDoc> search(String query, int topK) throws IOException {
        List<Float> queryVector = embeddingService.embed(query);

        SearchResponse<Map> response = esClient.search(s -> s
            .index(INDEX_NAME)
            .knn(knn -> knn
                .field("vector")
                .k(topK)
                .queryVector(queryVector)
            )
            .size(topK)
        , Map.class);

        return response.hits().hits().stream()
            .map(hit -> {
                Map<String, Object> source = hit.source();
                KnowledgeDoc doc = new KnowledgeDoc();
                doc.setTitle((String) source.get("title"));
                doc.setContent((String) source.get("content"));
                doc.setScore(hit.score());
                return doc;
            })
            .collect(Collectors.toList());
    }

    /**
     * 删除文档
     */
    public void deleteDocument(String docId) throws IOException {
        // 删除关联的所有 chunk
        esClient.deleteByQuery(d -> d
            .index(INDEX_NAME)
            .query(q -> q
                .prefix(p -> p.field("_id").value(docId))
            )
        );
    }
}
```

### 3.2 文档处理工具

```java
// DocumentProcessor.java
package com.shuai.elasticsearch.rag.util;

import java.util.*;
import java.util.regex.Pattern;

/**
 * 文档处理工具 - 文本清理、分块
 */
public class DocumentProcessor {

    private static final int DEFAULT_CHUNK_SIZE = 500;
    private static final int DEFAULT_CHUNK_OVERLAP = 50;

    /**
     * 文本分块
     */
    public List<String> chunk(String text) {
        return chunk(text, DEFAULT_CHUNK_SIZE, DEFAULT_CHUNK_OVERLAP);
    }

    /**
     * 文本分块 (可配置参数)
     */
    public List<String> chunk(String text, int chunkSize, int overlap) {
        // 1. 清理文本
        String cleaned = cleanText(text);

        // 2. 按段落分割
        List<String> paragraphs = Arrays.asList(cleaned.split("\n\n+"));

        List<String> chunks = new ArrayList<>();
        StringBuilder currentChunk = new StringBuilder();

        for (String para : paragraphs) {
            if (currentChunk.length() + para.length() > chunkSize) {
                if (currentChunk.length() > 0) {
                    chunks.add(currentChunk.toString().trim());
                }
                // 保留 overlap 部分的尾部
                int overlapStart = Math.max(0, currentChunk.length() - overlap);
                String overlapText = currentChunk.substring(overlapStart);
                currentChunk = new StringBuilder(overlapText);
            }
            currentChunk.append(para).append("\n\n");
        }

        if (currentChunk.length() > 0) {
            chunks.add(currentChunk.toString().trim());
        }

        return chunks;
    }

    /**
     * 清理文本
     */
    public String cleanText(String text) {
        if (text == null) return "";

        return text
            // 移除多余空白
            .replaceAll("\\s+", " ")
            // 移除特殊字符 (保留中文、英文、数字、常用标点)
            .replaceAll("[\\x00-\\x1F\\x7F]", "")
            // 规范化换行
            .replaceAll("\r\n", "\n")
            .trim();
    }

    /**
     * 提取关键句
     */
    public List<String> extractKeySentences(String text, int maxSentences) {
        // 简单实现: 按句号分割，取前 N 句
        String[] sentences = text.split("[。！？.!?]+");
        List<String> keySentences = new ArrayList<>();

        for (String sentence : sentences) {
            if (sentence.trim().length() < 10) continue;
            if (keySentences.size() >= maxSentences) break;
            keySentences.add(sentence.trim());
        }

        return keySentences;
    }
}
```

## 4. API 接口

### 4.1 REST API

```java
// RagController.java
package com.shuai.elasticsearch.rag.controller;

import com.shuai.elasticsearch.rag.model.RagRequest;
import com.shuai.elasticsearch.rag.model.RagResponse;
import com.shuai.elasticsearch.rag.service.RagService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * RAG API 控制器
 */
@RestController
@RequestMapping("/api/rag")
public class RagController {

    private final RagService ragService;

    public RagController(RagService ragService) {
        this.ragService = ragService;
    }

    /**
     * 问答接口
     */
    @PostMapping("/ask")
    public ResponseEntity<RagResponse> ask(@RequestBody RagRequest request) {
        if (request.getQuestion() == null || request.getQuestion().isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        RagResponse response = ragService.ask(request);
        return ResponseEntity.ok(response);
    }

    /**
     * 批量问答接口
     */
    @PostMapping("/batch-ask")
    public ResponseEntity<List<RagResponse>> batchAsk(@RequestBody List<RagRequest> requests) {
        List<RagResponse> responses = ragService.batchAsk(requests);
        return ResponseEntity.ok(responses);
    }

    /**
     * 健康检查
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("RAG 服务正常运行");
    }
}
```

### 4.2 请求/响应模型

```java
// RagRequest.java
package com.shuai.elasticsearch.rag.model;

public class RagRequest {
    private String question;
    private int topK = 10;           // 召回数量
    private int rerankTopK = 5;      // 重排序数量
    private int maxContextLength = 3000;  // 最大上下文长度

    // getters and setters
    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }
    public int getTopK() { return topK; }
    public void setTopK(int topK) { this.topK = topK; }
    public int getRerankTopK() { return rerankTopK; }
    public void setRerankTopK(int rerankTopK) { this.rerankTopK = rerankTopK; }
    public int getMaxContextLength() { return maxContextLength; }
    public void setMaxContextLength(int maxContextLength) { this.maxContextLength = maxContextLength; }
}

// RagResponse.java
package com.shuai.elasticsearch.rag.model;

import java.util.List;

public class RagResponse {
    private String question;
    private String answer;
    private int retrievedDocs;
    private long elapsedMs;
    private List<Source> sources;

    // getters and setters
    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }
    public String getAnswer() { return answer; }
    public void setAnswer(String answer) { this.answer = answer; }
    public int getRetrievedDocs() { return retrievedDocs; }
    public void setRetrievedDocs(int retrievedDocs) { this.retrievedDocs = retrievedDocs; }
    public long getElapsedMs() { return elapsedMs; }
    public void setElapsedMs(long elapsedMs) { this.elapsedMs = elapsedMs; }
    public List<Source> getSources() { return sources; }
    public void setSources(List<Source> sources) { this.sources = sources; }

    public static class Source {
        private String content;
        private float score;

        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public float getScore() { return score; }
        public void setScore(float score) { this.score = score; }
    }
}
```

## 5. 实践部分

### 5.1 RAG 系统测试

```java
// RagSystemDemo.java
package com.shuai.elasticsearch.rag;

import com.shuai.elasticsearch.rag.model.RagRequest;
import com.shuai.elasticsearch.rag.model.RagResponse;
import com.shuai.elasticsearch.rag.service.RagService;
import com.shuai.elasticsearch.util.ResponsePrinter;

import java.util.Arrays;
import java.util.List;

/**
 * RAG 系统测试演示
 */
public class RagSystemDemo {

    private final RagService ragService;

    public RagSystemDemo(RagService ragService) {
        this.ragService = ragService;
    }

    /**
     * 运行系统测试
     */
    public void runTests() {
        ResponsePrinter.printMethodInfo("RagSystemDemo", "RAG 系统测试");

        // 1. 单问题测试
        singleQuestionTest();

        // 2. 批量测试
        batchQuestionTest();

        // 3. 性能测试
        performanceTest();
    }

    /**
     * 单问题测试
     */
    private void singleQuestionTest() {
        System.out.println("\n  [单问题测试]");

        RagRequest request = new RagRequest();
        request.setQuestion("Elasticsearch 集群如何配置?");
        request.setTopK(10);
        request.setRerankTopK(5);

        RagResponse response = ragService.ask(request);

        printResponse(response);
    }

    /**
     * 批量测试
     */
    private void batchQuestionTest() {
        System.out.println("\n  [批量测试]");

        List<String> questions = Arrays.asList(
            "如何安装 Elasticsearch?",
            "ES 和 Kibana 有什么关系?",
            "向量检索如何使用?"
        );

        RagRequest template = new RagRequest();
        template.setTopK(10);
        template.setRerankTopK(5);

        long totalStart = System.currentTimeMillis();

        for (int i = 0; i < questions.size(); i++) {
            System.out.println("\n  问题 " + (i + 1) + ": " + questions.get(i));
            template.setQuestion(questions.get(i));
            RagResponse response = ragService.ask(template);
            System.out.println("  回答: " + response.getAnswer().substring(0, 100) + "...");
            System.out.println("  耗时: " + response.getElapsedMs() + "ms");
        }

        long totalElapsed = System.currentTimeMillis() - totalStart;
        System.out.println("\n  批量测试总耗时: " + totalElapsed + "ms");
    }

    /**
     * 性能测试
     */
    private void performanceTest() {
        System.out.println("\n  [性能测试]");

        RagRequest request = new RagRequest();
        request.setQuestion("Elasticsearch 集群配置");
        request.setTopK(100);
        request.setRerankTopK(20);

        // 执行 10 次
        int iterations = 10;
        long[] times = new long[iterations];

        for (int i = 0; i < iterations; i++) {
            long start = System.currentTimeMillis();
            ragService.ask(request);
            times[i] = System.currentTimeMillis() - start;
        }

        double avg = Arrays.stream(times).average().orElse(0);
        double p50 = times[(int) (iterations * 0.5)];
        double p95 = times[(int) (iterations * 0.95)];
        double p99 = times[(int) (iterations * 0.99)];

        System.out.println("  执行次数: " + iterations);
        System.out.printf("  平均耗时: %.2f ms%n", avg);
        System.out.printf("  P50: %.2f ms%n", p50);
        System.out.printf("  P95: %.2f ms%n", p95);
        System.out.printf("  P99: %.2f ms%n", p99);
    }

    private void printResponse(RagResponse response) {
        System.out.println("\n  问题: " + response.getQuestion());
        System.out.println("  回答: " + response.getAnswer());
        System.out.println("  召回: " + response.getRetrievedDocs() + " 篇文档");
        System.out.println("  耗时: " + response.getElapsedMs() + "ms");

        if (response.getSources() != null && !response.getSources().isEmpty()) {
            System.out.println("\n  参考来源:");
            for (int i = 0; i < Math.min(3, response.getSources().size()); i++) {
                RagResponse.Source source = response.getSources().get(i);
                System.out.printf("    %d. [%.4f] %s...%n", i + 1, source.getScore(), source.getContent());
            }
        }
    }
}
```

## 6. 常见问题

| 问题 | 原因 | 解决方案 |
|------|------|----------|
| 检索不到结果 | 知识库为空 | 先导入文档 |
| 回答不准确 | 召回文档不相关 | 优化 Embedding 模型 |
| 响应慢 | Embedding/LLM 调用慢 | 缓存 + 异步 |
| 上下文超长 | 文档 chunk 过大 | 减小 chunk_size |

## 7. 扩展阅读

**代码位置**:
- [RagPromptTemplate.java](src/main/java/com/shuai/elasticsearch/rag/RagPromptTemplate.java) - 提示词模板
- [HybridSearchDemo.java](src/main/java/com/shuai/elasticsearch/rag/HybridSearchDemo.java) - 混合检索实现

- [LangChain RAG](https://python.langchain.com/docs/use_cases/question_answering/)
- [LlamaIndex](https://docs.llamaindex.ai/)
- [RAG 评估框架 RAGAS](https://github.com/explodinggradients/ragas)
- [上一章: RAG 概述](14-rag-overview.md) | [下一章: 性能优化](16-performance-optimization.md)

---

**学习进度**: ⬜⬜⬜⬜⬜⬜⬜⬜ 0/18 文档完成

**最后更新**: 2026-01-17
