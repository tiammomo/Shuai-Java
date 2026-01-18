# RAG 概述

> 本章介绍检索增强生成 (RAG) 技术的基本原理、架构组成和应用场景。

## 学习目标

完成本章学习后，你将能够：
- 理解 RAG 技术的基本概念
- 掌握 RAG 的核心组件
- 了解 RAG 的技术演进
- 分析 RAG 的优势和挑战

## 1. RAG 简介

### 1.1 什么是 RAG

**RAG (Retrieval-Augmented Generation)** 即检索增强生成，是一种结合检索系统和生成模型的技术架构。

```
┌─────────────────────────────────────────────────────────────────────┐
│                         RAG 核心思想                                  │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   传统 LLM 问题:                                                    │
│   ├── 知识截止日期限制 (无法获取最新信息)                           │
│   ├── 可能产生幻觉 (生成不准确内容)                                 │
│   ├── 缺乏专业知识深度 (特定领域知识不足)                           │
│   └── 难以追溯来源 (无法提供答案依据)                               │
│                                                                      │
│   RAG 解决方案:                                                     │
│   ├── 检索相关文档作为上下文                                        │
│   ├── 让 LLM 基于检索结果生成答案                                   │
│   └── 提供可追溯的引用来源                                          │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### 1.2 RAG 工作流程

```
┌─────────────────────────────────────────────────────────────────────┐
│                        RAG 工作流程                                  │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   ┌─────────────────────────────────────────────────────────────┐   │
│   │                        用户问题                              │   │
│   │                   "如何配置 Elasticsearch 集群?"            │   │
│   └─────────────────────────────────────────────────────────────┘   │
│                              │                                      │
│                              ▼                                      │
│   ┌─────────────────────────────────────────────────────────────┐   │
│   │                    1. 问题理解与改写                         │   │
│   │                                                              │   │
│   │   • 问题向量化                                                │   │
│   │   • 查询改写 (可选)                                           │   │
│   │   • 意图识别                                                 │   │
│   └─────────────────────────────────────────────────────────────┘   │
│                              │                                      │
│                              ▼                                      │
│   ┌─────────────────────────────────────────────────────────────┐   │
│   │                    2. 向量检索 (Recall)                      │   │
│   │                                                              │   │
│   │   ┌──────────────────────────────────────────────────────┐  │   │
│   │   │  知识库文档 → Embedding 模型 → 向量存储               │  │   │
│   │   │     (预处理)          (向量化)      (Milvus/ES)       │  │   │
│   │   └──────────────────────────────────────────────────────┘  │   │
│   │                              │                               │   │
│   │                              ▼                               │   │
│   │              检索 Top-K 相关文档 (Bi-Encoder)               │   │
│   └─────────────────────────────────────────────────────────────┘   │
│                              │                                      │
│                              ▼                                      │
│   ┌─────────────────────────────────────────────────────────────┐   │
│   │                    3. 重排序 (Rank)                          │   │
│   │                                                              │   │
│   │              Cross-Encoder 精排 → Top-N 文档                 │   │
│   └─────────────────────────────────────────────────────────────┘   │
│                              │                                      │
│                              ▼                                      │
│   ┌─────────────────────────────────────────────────────────────┐   │
│   │                    4. 上下文构建                             │   │
│   │                                                              │   │
│   │      检索结果 + 问题 → 构建 Prompt 模板                     │   │
│   └─────────────────────────────────────────────────────────────┘   │
│                              │                                      │
│                              ▼                                      │
│   ┌─────────────────────────────────────────────────────────────┐   │
│   │                    5. LLM 生成                              │   │
│   │                                                              │   │
│   │              将 Prompt 发送给 LLM 获取答案                  │   │
│   └─────────────────────────────────────────────────────────────┘   │
│                              │                                      │
│                              ▼                                      │
│   ┌─────────────────────────────────────────────────────────────┐   │
│   │                        答案输出                              │   │
│   │      "配置 ES 集群需要以下步骤: 1. 修改配置...              │   │
│   └─────────────────────────────────────────────────────────────┘   │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

## 2. RAG 核心组件

### 2.1 数据处理层

```
┌─────────────────────────────────────────────────────────────────────┐
│                        数据处理层                                     │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│  原始数据                                                            │
│  ├── PDF 文档                                                       │
│  ├── Word 文档                                                      │
│  ├── HTML 页面                                                      │
│  ├── 数据库记录                                                     │
│  └── 知识图谱                                                       │
│                                                                      │
│       │                                                             │
│       ▼                                                             │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │  文档加载 → 文本提取 → 清理格式化 → 分块 → 向量化 → 存储    │   │
│  └─────────────────────────────────────────────────────────────┘   │
│       │                                                             │
│       ▼                                                             │
│  向量数据库                                                          │
│  └── 文档 Chunk 向量 + 元数据                                       │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### 2.2 检索层

```
┌─────────────────────────────────────────────────────────────────────┐
│                          检索层                                       │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │                     查询处理                                  │   │
│  │  • 问题向量化 (Bi-Encoder)                                   │   │
│  │  • 查询改写 / 扩展                                           │   │
│  │  • 多查询融合                                                │   │
│  └─────────────────────────────────────────────────────────────┘   │
│                              │                                      │
│                              ▼                                      │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │                    向量检索 (Milvus/ES)                      │   │
│  │  • HNSW / IVF 索引                                           │   │
│  │  • 近似最近邻搜索                                            │   │
│  │  • Top-K 召回                                               │   │
│  └─────────────────────────────────────────────────────────────┘   │
│                              │                                      │
│                              ▼                                      │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │                      重排序                                  │   │
│  │  • Cross-Encoder 精排                                       │   │
│  │  • 分数校准                                                 │   │
│  │  • 去重过滤                                                 │   │
│  └─────────────────────────────────────────────────────────────┘   │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### 2.3 生成层

```
┌─────────────────────────────────────────────────────────────────────┐
│                          生成层                                       │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │                      Prompt 构建                             │   │
│  │                                                              │   │
│  │  ┌─────────────────────────────────────────────────────┐    │   │
│  │  │ System: 你是一个技术专家，基于以下参考信息回答问题。  │    │   │
│  │  ├─────────────────────────────────────────────────────┤    │   │
│  │  │ Context:                                            │    │   │
│  │  │ [检索到的文档片段 1]                                 │    │   │
│  │  │ [检索到的文档片段 2]                                 │    │   │
│  │  │ ...                                                 │    │   │
│  │  ├─────────────────────────────────────────────────────┤    │   │
│  │  │ Question: 用户问题                                   │    │   │
│  │  │ Answer:                                              │    │   │
│  │  └─────────────────────────────────────────────────────┘    │   │
│  └─────────────────────────────────────────────────────────────┘   │
│                              │                                      │
│                              ▼                                      │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │                      LLM 调用                                │   │
│  │  • OpenAI GPT-4 / Claude / 文心一言                        │   │
│  │  • 本地部署 (LLaMA / Qwen)                                 │   │
│  │  • 流式输出                                                │   │
│  └─────────────────────────────────────────────────────────────┘   │
│                              │                                      │
│                              ▼                                      │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │                    后处理                                    │   │
│  │  • 引用标注                                                 │   │
│  │  • 格式整理                                                 │   │
│  │  • 安全过滤                                                 │   │
│  └─────────────────────────────────────────────────────────────┘   │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

## 3. RAG 技术演进

### 3.1 架构演进

| 阶段 | 架构 | 特点 |
|------|------|------|
| **Naive RAG** | 检索→生成直接拼接 | 简单但效果有限 |
| **Advanced RAG** | 检索优化 + 重排序 | 效果提升，复杂度增加 |
| **Modular RAG** | 可插拔组件 | 灵活适配，生态丰富 |

### 3.2 Advanced RAG 技术

```java
// 检索优化技术

/**
 * 1. 查询改写
 */
// 原始查询: "ES 集群配置"
// 改写后: "Elasticsearch 集群配置步骤 参数说明"
String expandedQuery = query + " " + expandWithSynonyms(query);

/**
 * 2. 查询路由
 */
if (isCodeRelated(query)) {
    // 代码相关查询使用专门索引
    searchInCodeIndex(query);
} else {
    // 普通查询使用主索引
    searchInMainIndex(query);
}

/**
 * 3. 混合检索
 */
// 向量检索 + 关键词检索
List<String> vectorResults = vectorSearch(query, 50);
List<String> keywordResults = keywordSearch(query, 50);
List<String> fusedResults = fuseWithRRF(vectorResults, keywordResults);

/**
 * 4. 重排序
 */
List<Doc> rerankedDocs = crossEncoderRerank(query, candidates, 20);
```

### 3.3 Modular RAG 架构

```
┌─────────────────────────────────────────────────────────────────────┐
│                        Modular RAG 架构                               │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│  ┌─────────┐  ┌─────────┐  ┌─────────┐  ┌─────────┐               │
│  │ Indexer │  │ Retriever│  │  Ranker │  │ Generator│              │
│  └────┬────┘  └────┬────┘  └────┬────┘  └────┬────┘               │
│       │            │            │            │                      │
│       └────────────┴─────┬──────┴────────────┘                      │
│                          │                                           │
│                    ┌─────┴─────┐                                     │
│                    │  Orchestrator │  ← 可编排调度                   │
│                    │   (编排器)   │                                   │
│                    └──────┬──────┘                                     │
│                           │                                           │
│                    ┌──────┴──────┐                                     │
│                    │   Router    │  ← 智能路由                        │
│                    │   (路由)    │                                     │
│                    └─────────────┘                                     │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

## 4. RAG 应用场景

### 4.1 典型应用

| 场景 | 说明 | 示例 |
|------|------|------|
| **知识库问答** | 企业内部知识问答 | 技术文档、FAQ |
| **客服系统** | 智能客服 | 电商客服、银行客服 |
| **文档摘要** | 长文档摘要生成 | 合同分析、报告生成 |
| **代码助手** | 代码问答 | API 文档、编程问题 |
| **教育培训** | 个性化学习 | 题库解析、知识点问答 |

### 4.2 技术选型参考

```
┌─────────────────────────────────────────────────────────────────────┐
│                      RAG 技术选型指南                                 │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│  数据规模:                                                           │
│  ├── < 1万文档 → 简单 RAG + ES                                      │
│  ├── 1万-100万 → Advanced RAG + Milvus                              │
│  └── > 100万 → Modular RAG + 分布式向量库                           │
│                                                                      │
│  响应速度:                                                           │
│  ├── < 1秒 → 缓存 + 轻量模型                                        │
│  ├── 1-3秒 → 标准配置                                               │
│  └── > 3秒 → 异步 + 流式输出                                        │
│                                                                      │
│  精度要求:                                                           │
│  ├── 通用场景 → BGE-base + GPT-3.5                                  │
│  ├── 高精度 → BGE-large + GPT-4 / Claude-3                          │
│  └── 垂直领域 → 领域微调模型                                        │
│                                                                      │
│  成本考虑:                                                           │
│  ├── 低成本 → 本地模型 + 开源向量库                                  │
│  ├── 中等 → 云 API + 自建向量库                                     │
│  └── 高成本 → 全云服务                                              │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

## 5. RAG 优势与挑战

### 5.1 RAG 优势

| 优势 | 说明 |
|------|------|
| **知识时效性** | 可随时更新知识库，无需重新训练 |
| **可解释性** | 答案可追溯到源文档 |
| **可控性** | 减少幻觉，提高准确性 |
| **灵活性** | 可适配不同领域和应用场景 |
| **成本效益** | 无需大模型微调即可注入知识 |

### 5.2 RAG 挑战

| 挑战 | 解决方案 |
|------|----------|
| **检索质量** | 优化分块、查询改写、重排序 |
| **上下文长度** | 关键信息提取、压缩 |
| **多轮对话** | 引入对话历史管理 |
| **领域适配** | 领域特定 Embedding 模型 |
| **评估困难** | 建立评估指标体系 |

## 6. LLM 集成

### 6.1 OpenAI 客户端

**代码位置**: [OpenAIClient.java](src/main/java/com/shuai/elasticsearch/llm/OpenAIClient.java)

RAG 系统需要 LLM 生成最终答案，OpenAI 客户端提供三种调用方式：

| 调用方式 | 特点 | 适用场景 |
|----------|------|----------|
| 同步调用 | 简单阻塞，等待响应 | 批量处理、低并发 |
| 异步调用 | 非阻塞，CompletableFuture | 高并发、批量请求 |
| 流式输出 | 实时显示生成内容 | 长文本、对话交互 |

### 6.2 RAG 中的 LLM 调用

```java
// 集成 LLM 到 RAG 系统
public class RagWithLLMDemo {

    private final OpenAIClient llmClient;

    public RagWithLLMDemo() {
        this.llmClient = new OpenAIClient(System.getenv("OPENAI_API_KEY"));
    }

    /**
     * RAG 问答流程
     */
    public String ask(String question) throws IOException {
        // 1. 检索相关文档 (省略)
        String context = retrieveDocuments(question);

        // 2. 构建提示词
        List<Message> messages = RagPromptTemplate.build(context, question);

        // 3. 调用 LLM 生成答案
        String answer = llmClient.chatCompletion(messages);

        return answer;
    }
}
```

### 6.3 提示词模板

**代码位置**: [RagPromptTemplate.java](src/main/java/com/shuai/elasticsearch/rag/RagPromptTemplate.java)

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

    public static List<Message> build(String context, String question) {
        String userContent = String.format("""
            ## 参考信息

            %s

            ---

            ## 用户问题

            %s

            ## 回答

            """, context, question);

        return List.of(
            Message.system(SYSTEM_PROMPT),
            Message.user(userContent)
        );
    }
}
```

## 7. 实践部分

### 6.1 简单 RAG 实现

```java
// SimpleRagDemo.java
package com.shuai.elasticsearch.rag;

import com.shuai.elasticsearch.embedding.BgeEmbeddingService;
import com.shuai.elasticsearch.embedding.RerankerService;
import com.shuai.elasticsearch.util.ResponsePrinter;

import java.util.*;

/**
 * 简化版 RAG 实现
 */
public class SimpleRagDemo {

    private final BgeEmbeddingService embeddingService;
    private final RerankerService rerankerService;

    // 模拟知识库 (实际项目中应从数据库加载)
    private final Map<String, String> knowledgeBase = new LinkedHashMap<>();

    public SimpleRagDemo() {
        this.embeddingService = new BgeEmbeddingService();
        this.rerankerService = new RerankerService();

        // 初始化知识库
        initKnowledgeBase();

        ResponsePrinter.printMethodInfo("SimpleRagDemo", "简化版 RAG 实现");
    }

    private void initKnowledgeBase() {
        knowledgeBase.put("1", "Elasticsearch 是一个分布式搜索和分析引擎，基于 Apache Lucene 构建。");
        knowledgeBase.put("2", "安装 Elasticsearch: 1. 下载压缩包 2. 解压 3. 修改配置 4. 运行 bin/elasticsearch");
        knowledgeBase.put("3", "ES 集群配置关键参数: cluster.name, node.name, network.host, http.port");
        knowledgeBase.put("4", "Kibana 是 ES 的可视化工具，用于数据分析和仪表盘创建。");
        knowledgeBase.put("5", "Logstash 用于数据收集和处理，常与 ES 和 Kibana 组成 ELK 栈。");
        knowledgeBase.put("6", "ES 查询类型: match, term, range, bool, nested 等。");
        knowledgeBase.put("7", "向量检索使用 dense_vector 字段类型，配合 knn 查询使用。");
        knowledgeBase.put("8", "Milvus 是开源向量数据库，支持大规模向量检索和相似度搜索。");
    }

    /**
     * 简化版 RAG 流程
     */
    public String ask(String question) {
        System.out.println("\n  用户问题: " + question);

        // 1. 问题向量化
        List<Float> questionVector = embeddingService.embed(question);

        // 2. 简单检索 (模拟)
        List<String> candidateIds = simpleRetrieve(questionVector);

        // 3. 重排序
        List<String> candidateTexts = new ArrayList<>();
        for (String id : candidateIds) {
            candidateTexts.add(knowledgeBase.get(id));
        }

        List<RerankerService.RerankResult> reranked = rerankerService.rerank(
            question, candidateTexts, 3);

        // 4. 构建上下文
        StringBuilder context = new StringBuilder();
        for (RerankerService.RerankResult result : reranked) {
            context.append("- ").append(result.getText()).append("\n");
        }

        // 5. 生成回答 (模拟)
        String answer = generateAnswer(question, context.toString());

        return answer;
    }

    /**
     * 简单检索 (基于关键词匹配)
     */
    private List<String> simpleRetrieve(List<Float> questionVector) {
        List<String> results = new ArrayList<>();

        for (String id : knowledgeBase.keySet()) {
            String text = knowledgeBase.get(id);
            List<Float> docVector = embeddingService.embed(text);
            float similarity = embeddingService.cosineSimilarity(questionVector, docVector);

            if (similarity > 0.5) {
                results.add(id);
            }
        }

        // 按相似度排序
        results.sort((a, b) -> {
            float simA = embeddingService.cosineSimilarity(
                questionVector, embeddingService.embed(knowledgeBase.get(a)));
            float simB = embeddingService.cosineSimilarity(
                questionVector, embeddingService.embed(knowledgeBase.get(b)));
            return Float.compare(simB, simA);
        });

        return results.subList(0, Math.min(5, results.size()));
    }

    /**
     * 生成回答 (实际项目中调用 LLM)
     */
    private String generateAnswer(String question, String context) {
        // 模拟 LLM 生成
        return String.format("""
            基于检索到的信息，回答您的问题: "%s"

            相关参考:
            %s

            建议:
            如需了解更多细节，请参考官方文档或提供更具体的问题。

            (注: 此为简化版 RAG 演示，实际系统需接入真实 LLM)
            """, question, context.trim());
    }

    /**
     * RAG 流程演示
     */
    public void demonstrate() {
        System.out.println("\n  RAG 流程演示:");
        System.out.println("  " + "=".repeat(50));

        String[] questions = {
            "如何安装 Elasticsearch?",
            "ES 集群需要配置哪些参数?",
            "ES 和 Kibana 有什么关系?"
        };

        for (String question : questions) {
            System.out.println("\n  " + "-".repeat(50));
            String answer = ask(question);
            System.out.println("\n  " + answer);
        }
    }
}
```

### 6.2 RAG 评估指标

```java
/**
 * RAG 评估指标
 */
public class RagEvaluator {

    /**
     * 检索评估指标
     */
    public static class RetrievalMetrics {
        private final double hitRate;       // 命中率
        private final double mrr;           // MRR (Mean Reciprocal Rank)
        private final double ndcg;          // NDCG (Normalized DCG)

        // 命中: 检索结果中是否包含正确答案
        // MRR: 第一个正确答案的排名的倒数平均值
        // NDCG: 归一化折损累积增益
    }

    /**
     * 生成评估指标
     */
    public static class GenerationMetrics {
        private final double relevance;     // 相关性 (1-5)
        private final double faithfulness;  // 忠实度 (是否基于检索结果)
        private final double accuracy;      // 准确性
        private final String answer;        // 生成答案
    }

    /**
     * 综合评估
     */
    public static class EvaluationResult {
        private final RetrievalMetrics retrieval;
        private final GenerationMetrics generation;
        private final double overallScore;
    }
}
```

## 7. 扩展阅读

**代码位置**:
- [RAGDemo.java](src/main/java/com/shuai/elasticsearch/rag/RAGDemo.java) - RAG 系统演示（完整流程）
- [HybridSearchDemo.java](src/main/java/com/shuai/elasticsearch/rag/HybridSearchDemo.java) - 混合检索演示
- [BgeEmbeddingService.java](src/main/java/com/shuai/elasticsearch/embedding/BgeEmbeddingService.java) - Embedding 向量化服务
- [BlogDataGenerator.java](src/main/java/com/shuai/elasticsearch/data/BlogDataGenerator.java) - 测试数据生成器（13篇高质量文档）
- [RagPromptTemplate.java](src/main/java/com/shuai/elasticsearch/rag/RagPromptTemplate.java) - 提示词模板

- [RAG Survey Paper](https://arxiv.org/abs/2312.10997)
- [LangChain RAG](https://python.langchain.com/docs/use_cases/question_answering/)
- [LlamaIndex](https://docs.llamaindex.ai/)
- [BGE Embedding](https://github.com/FlagOpen/FlagEmbedding)
- [上一章: 两阶段检索](13-two-stage-retrieval.md) | [下一章: LLM 集成](18-llm-integration.md)

---

**学习进度**: ⬜⬜⬜⬜⬜⬜⬜⬜⬜⬜ 0/18 文档完成

**最后更新**: 2026-01-18
