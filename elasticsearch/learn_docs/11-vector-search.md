# 向量检索

> 本章深入讲解向量检索技术，包括向量表示、相似度计算、检索算法和实践实现。

## 学习目标

完成本章学习后，你将能够：
- 理解向量表示和相似度计算方法
- 掌握主流向量索引算法原理
- 实现基于向量的相似度搜索
- 优化向量检索性能和精度

## 1. 向量基础

### 1.1 什么是向量

向量是**数值的有序列表**，用于表示文本、图像等数据的语义特征。

```
文本: "人工智能是未来"
         │
         ▼
    ┌───────────────────────────────┐
    │ [0.12, 0.45, 0.78, 0.33, ...] │  ← 1024 维向量
    └───────────────────────────────┘
         │
         ▼
    向量化存储在 Milvus/ES 中
```

### 1.2 向量相似度

```
┌─────────────────────────────────────────────────────────────────────┐
│                    向量相似度计算方法                                 │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│  余弦相似度 (Cosine Similarity)                                      │
│  ────────────────────────────────                                   │
│  cos(θ) = (A · B) / (|A| × |B|)                                     │
│                                                                      │
│  点积 (Dot Product)                                                  │
│  ────────────────────────────────                                   │
│  A · B = Σ(Ai × Bi)                                                 │
│                                                                      │
│  欧氏距离 (Euclidean Distance)                                       │
│  ────────────────────────────────                                   │
│  d(A, B) = √Σ(Ai - Bi)²                                             │
│                                                                      │
│  曼哈顿距离 (Manhattan Distance)                                     │
│  ────────────────────────────────                                   │
│  d(A, B) = Σ|Ai - Bi|                                               │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### 1.3 相似度对比

| 方法 | 公式 | 特点 | 适用场景 |
|------|------|------|----------|
| **余弦相似度** | cos(θ) | 关注角度，不受长度影响 | 文本相似度 |
| **点积** | A·B | 受长度影响大 | 推荐系统 |
| **欧氏距离** | √Σ(Ai-Bi)² | 直观距离 | 聚类分析 |

```bash
# Milvus 中配置相似度类型
{
  "metric_type": "COSINE",    # 余弦相似度
  "index_type": "HNSW"
}

{
  "metric_type": "L2",        # 欧氏距离
  "index_type": "IVF_FLAT"
}

{
  "metric_type": "IP",        # 内积
  "index_type":HNSW"
}
```

## 2. 向量索引算法

### 2.1 索引算法分类

```
┌─────────────────────────────────────────────────────────────────────┐
│                       向量索引算法分类                                │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│  ┌─────────────────────────────────────────────────────────────┐    │
│  │                    精确索引 (Exact)                          │    │
│  │                                                              │    │
│  │  ◉ Brute Force (暴力搜索)                                   │    │
│  │     - 优点: 100% 精度                                        │    │
│  │     - 缺点: O(n) 时间复杂度                                  │    │
│  │     - 适用: 小数据集 (< 10万)                               │    │
│  └─────────────────────────────────────────────────────────────┘    │
│                                                                      │
│  ┌─────────────────────────────────────────────────────────────┐    │
│  │                    近似索引 (Approximate)                    │    │
│  │                                                              │    │
│  │  ┌─────────────────┐  ┌─────────────────┐                   │    │
│  │  │   基于聚类       │  │   基于图        │                   │    │
│  │  │   ────────────  │  │   ────────────  │                   │    │
│  │  │  • IVF_FLAT     │  │  • HNSW         │                   │    │
│  │  │  • IVF_SQ8      │  │  • NSW          │                   │    │
│  │  │  • IVF_PQ       │  │  • FANOUT       │                   │    │
│  │  └─────────────────┘  └─────────────────┘                   │    │
│  │                                                              │    │
│  │  ┌─────────────────┐  ┌─────────────────┐                   │    │
│  │  │   基于哈希       │  │   基于树        │                   │    │
│  │  │   ────────────  │  │   ────────────  │                   │    │
│  │  │  • LSH          │  │  • KD-Tree      │                   │    │
│  │  │  • SP-LSH       │  │  • Ball Tree    │                   │    │
│  │  └─────────────────┘  └─────────────────┘                   │    │
│  └─────────────────────────────────────────────────────────────┘    │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### 2.2 IVF 索引原理

```
┌─────────────────────────────────────────────────────────────────────┐
│                         IVF 索引原理                                  │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│  1. 训练阶段 (Training)                                              │
│     ┌─────────────────────────────────────────────────────────┐     │
│     │                    向量集合                              │     │
│     │         ● ● ● ● ● ● ● ● ● ● ● ● ● ● ●                │     │
│     │       ● ● ● ● ● ● ● ● ● ● ● ● ● ● ● ●                │     │
│     │         ● ● ● ● ● ● ● ● ● ● ● ● ● ● ●                │     │
│     └─────────────────────────────────────────────────────────┘     │
│                              │                                       │
│                              ▼                                       │
│     ┌─────────────────────────────────────────────────────────┐     │
│     │                    K-Means 聚类                          │     │
│     │         ┌───┐    ┌───┐    ┌───┐    ┌───┐              │     │
│     │         │C1 │    │C2 │    │C3 │    │C4 │    ...       │     │
│     │         └───┘    └───┘    └───┘    └───┘              │     │
│     └─────────────────────────────────────────────────────────┘     │
│                              │                                       │
│                              ▼                                       │
│  2. 索引阶段 (Indexing)                                              │
│     ┌─────────────────────────────────────────────────────────┐     │
│     │  向量 → 计算到各中心的距离 → 分配到最近簇 → 建立倒排索引 │     │
│     └─────────────────────────────────────────────────────────┘     │
│                              │                                       │
│                              ▼                                       │
│  3. 查询阶段 (Query)                                                 │
│     ┌─────────────────────────────────────────────────────────┐     │
│     │  查询向量 → 计算到各中心距离 → 选择 nprobe 个簇 → 簇内搜索 │     │
│     └─────────────────────────────────────────────────────────┘     │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### 2.3 HNSW 索引原理

```
┌─────────────────────────────────────────────────────────────────────┐
│                         HNSW 索引原理                                 │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│  HNSW (Hierarchical Navigable Small World)                          │
│                                                                      │
│  ┌─────────────────────────────────────────────────────────────┐    │
│  │                       分层结构                               │    │
│  │                                                              │    │
│  │     Layer 2:  ▪────────▪                                    │    │
│  │                │   (高速公路层，跳数少)                      │    │
│  │     Layer 1:  ▪──▪──▪──▪──▪──▪                             │    │
│  │                │  (全局搜索层)                               │    │
│  │     Layer 0:  ▪▪▪▪▪▪▪▪▪▪▪▪▪▪▪▪▪▪▪▪▪▪▪▪▪▪                   │    │
│  │                (详细层，所有数据点)                          │    │
│  │                                                              │    │
│  └─────────────────────────────────────────────────────────────┘    │
│                              │                                       │
│                              ▼                                       │
│  搜索流程:                                                           │
│  1. 从最高层开始                                                    │
│  2. 在当前层贪心搜索最近邻居                                        │
│  3. 到达局部最优点后，进入下一层                                    │
│  4. 重复直到第 0 层                                                │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### 2.4 LSH 索引原理

```
┌─────────────────────────────────────────────────────────────────────┐
│                         LSH 索引原理                                  │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│  局部敏感哈希 (Locality Sensitive Hashing)                          │
│                                                                      │
│  核心思想: 相似的向量有更高概率映射到相同的桶                          │
│                                                                      │
│  ┌─────────────────────────────────────────────────────────────┐    │
│  │  原始向量                                                    │    │
│  │         [0.12, 0.45, 0.78, 0.33, 0.91, ...]                │    │
│  │              │                                               │    │
│  │              ▼                                               │    │
│  │  ┌─────────────────────┐                                     │    │
│  │  │   哈希函数族 (随机投影) │                                  │    │
│  │  │  h1(v), h2(v), ..., hk(v)                                │    │
│  │  └─────────────────────┘                                     │    │
│  │              │                                               │    │
│  │              ▼                                               │    │
│  │  ┌─────────────────────────────────────────────────────┐    │    │
│  │  │  Bucket 1 │ Bucket 2 │ Bucket 3 │ Bucket 4 │ ...  │    │    │
│  │  │  [v1, v5] │  [v2]    │ [v3, v7] │   [v4]   │      │    │    │
│  │  └─────────────────────────────────────────────────────┘    │    │
│  │                                                              │    │
│  │  相似向量 v1, v5 更可能在同一个桶                            │    │
│  └─────────────────────────────────────────────────────────────┘    │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

## 3. 向量检索实践

### 3.1 Elasticsearch 向量检索

```bash
# 1. 创建向量字段索引
PUT /article
{
  "mappings": {
    "properties": {
      "title": { "type": "text" },
      "content": { "type": "text" },
      "vector": {
        "type": "dense_vector",
        "dims": 1024,
        "index": true,
        "similarity": "cosine"
      }
    }
  }
}

# 2. 插入向量数据
PUT /article/_doc/1
{
  "title": "机器学习入门",
  "content": "本文介绍机器学习的基础知识",
  "vector": [0.1, 0.2, 0.3, ...]
}

# 3. 向量搜索
GET /article/_search
{
  "query": {
    "knn": {
      "vector": {
        "vector": [0.1, 0.2, 0.3, ...],
        "k": 10
      }
    }
  }
}

# 4. 混合搜索 (向量 + 文本)
GET /article/_search
{
  "query": {
    "bool": {
      "must": [
        { "knn": { "vector": { "vector": [...], "k": 10 }}}
      ],
      "should": [
        { "match": { "title": "机器学习" }}
      ]
    }
  }
}
```

### 3.2 Milvus 向量检索

```java
// 完整向量检索示例
public class VectorSearchDemo {

    public void searchWithFilters() {
        // 1. 准备查询向量
        List<Float> queryVector = generateQueryVector();

        // 2. 构建搜索请求 (带过滤条件)
        SearchReq searchReq = SearchReq.builder()
            .collectionName("document_vectors")
            .data(Collections.singletonList(queryVector))
            .topK(10)
            .outputFields(Arrays.asList("id", "text", "category"))
            // 过滤条件
            .filter("category == '技术' && views > 100")
            .build();

        // 3. 执行搜索
        SearchResp response = client.search(searchReq);

        // 4. 处理结果
        for (SearchResp.SearchResult result : response.getSearchResults()) {
            for (SearchResp.Entity entity : result.getEntities()) {
                System.out.printf("ID: %s, Score: %.4f, Text: %s%n",
                    entity.get("id"),
                    entity.getScore(),
                    entity.get("text"));
            }
        }
    }

    public void batchSearch() {
        // 批量查询向量
        List<List<Float>> queryVectors = Arrays.asList(
            generateQueryVector(),
            generateQueryVector(),
            generateQueryVector()
        );

        SearchReq searchReq = SearchReq.builder()
            .collectionName("document_vectors")
            .data(queryVectors)
            .topK(5)
            .build();

        // 批量搜索结果
        List<List<SearchResp.SearchResult>> results = client.search(searchReq)
            .getSearchResults();

        for (int i = 0; i < results.size(); i++) {
            System.out.println("Query " + (i + 1) + " results:");
            for (var result : results.get(i)) {
                System.out.printf("  ID: %s, Score: %.4f%n",
                    result.getEntity().get("id"),
                    result.getScore());
            }
        }
    }
}
```

### 3.3 范围搜索

```java
// 相似度范围搜索
public void rangeSearch() {
    // 使用标量过滤实现范围搜索
    SearchReq searchReq = SearchReq.builder()
        .collectionName("document_vectors")
        .data(Collections.singletonList(queryVector))
        .topK(100)  // 先获取更多结果
        .outputFields(Arrays.asList("id", "score"))
        .filter("score > 0.8")  // 过滤低相似度结果
        .build();

    var results = client.search(searchReq);
}
```

## 4. 性能优化

### 4.1 索引参数调优

```java
// HNSW 参数调优
CreateIndexReq indexReq = CreateIndexReq.builder()
    .collectionName("my_collection")
    .fieldName("vector")
    .indexType("HNSW")
    .metricType("COSINE")
    .extraParams(Map.of(
        // 构造参数
        "M", 16,                 // 每个节点的邻居数 (4-64)
        "efConstruction", 200,   // 索引构建时的搜索深度 (100-500)

        // 查询参数
        "ef", 64                 // 查询时的搜索深度 (32-512)
    ))
    .build();
```

**参数调优建议**：

| 参数 | 推荐范围 | 影响 |
|------|----------|------|
| M | 8-32 | 内存占用与精度平衡 |
| efConstruction | 100-200 | 索引构建时间与质量 |
| ef | 32-256 | 搜索精度与速度 |

### 4.2 查询优化

```java
// 1. 限制返回字段
SearchReq searchReq = SearchReq.builder()
    .collectionName("my_collection")
    .data(Collections.singletonList(queryVector))
    .topK(10)
    .outputFields(Arrays.asList("id", "text"))  // 只返回必要字段
    .build();

// 2. 使用分区加速
SearchReq searchReq = SearchReq.builder()
    .collectionName("my_collection")
    .data(Collections.singletonList(queryVector))
    .topK(10)
    .partitionNames(Arrays.asList("partition_2024"))  // 指定分区
    .build();

// 3. 异步搜索 (大数据量)
SearchReq searchReq = SearchReq.builder()
    .collectionName("my_collection")
    .data(Collections.singletonList(queryVector))
    .topK(100)
    .build();

// 异步执行
CompletableFuture<SearchResp> future = client.searchAsync(searchReq);
```

### 4.3 内存优化

```java
// 使用量化索引减少内存
CreateIndexReq indexReq = CreateIndexReq.builder()
    .collectionName("my_collection")
    .fieldName("vector")
    .indexType("IVF_SQ8")  // 标量量化
    .extraParams(Map.of(
        "nlist", 1024,
        "nprobe", 16
    ))
    .build();

// SQ8 压缩率约 4x
// Float32 (4 bytes) → Int8 (1 byte)
```

## 5. 实践部分

### 5.1 完整向量检索流程

```java
// VectorRetrievalDemo.java
package com.shuai.elasticsearch.vector;

import io.milvus.v2.client.MilvusClientV2;
import io.milvus.v2.common.DataType;
import io.milvus.v2.service.collection.request.CreateCollectionReq;
import io.milvus.v2.service.collection.request.AddFieldReq;
import io.milvus.v2.service.vector.request.InsertReq;
import io.milvus.v2.service.vector.request.SearchReq;
import io.milvus.v2.service.vector.request.CreateIndexReq;
import com.shuai.elasticsearch.util.ResponsePrinter;

import java.util.*;

public class VectorRetrievalDemo {

    private final MilvusClientV2 client;
    private static final String COLLECTION_NAME = "semantic_search";
    private static final int VECTOR_DIM = 768;

    public VectorRetrievalDemo() {
        this.client = new MilvusClientV2("http://localhost:19530");
    }

    /**
     * 运行向量检索演示
     */
    public void runAllDemos() throws Exception {
        ResponsePrinter.printMethodInfo("VectorRetrievalDemo", "向量检索演示");

        System.out.println("\n  向量检索流程:");
        System.out.println("  1. 文本向量化 → 2. 构建向量索引 → 3. 相似度搜索 → 4. 结果排序");
    }

    /**
     * 文本向量化 (使用 BGE 模型)
     */
    private void vectorizeText(String text) {
        // 实际项目中调用 Embedding API
        System.out.println("  文本: " + text);
        System.out.println("  向量维度: " + VECTOR_DIM);
        System.out.println("  向量: [" + String.join(", ",
            String.valueOf(Math.random()),
            String.valueOf(Math.random()),
            "...") + "]");
    }

    /**
     * 相似度计算
     */
    private float cosineSimilarity(List<Float> vec1, List<Float> vec2) {
        float dotProduct = 0;
        float norm1 = 0;
        float norm2 = 0;

        for (int i = 0; i < vec1.size(); i++) {
            dotProduct += vec1.get(i) * vec2.get(i);
            norm1 += vec1.get(i) * vec1.get(i);
            norm2 += vec2.get(i) * vec2.get(i);
        }

        return dotProduct / (float) (Math.sqrt(norm1) * Math.sqrt(norm2));
    }

    /**
     * 批量向量搜索
     */
    public void batchVectorSearch() throws Exception {
        ResponsePrinter.printMethodInfo("batchVectorSearch", "批量向量搜索");

        // 生成多个查询向量
        List<List<Float>> queryVectors = Arrays.asList(
            generateRandomVector(VECTOR_DIM),
            generateRandomVector(VECTOR_DIM)
        );

        SearchReq searchReq = SearchReq.builder()
            .collectionName(COLLECTION_NAME)
            .data(queryVectors)
            .topK(5)
            .outputFields(Arrays.asList("id", "text", "category"))
            .build();

        var results = client.search(searchReq).getSearchResults();

        for (int i = 0; i < results.size(); i++) {
            System.out.println("  查询 " + (i + 1) + " 结果:");
            for (var result : results.get(i)) {
                System.out.println("    ID: " + result.getEntity().get("id") +
                    ", 相似度: " + String.format("%.4f", result.getScore()));
            }
        }
    }

    /**
     * 带过滤的向量搜索
     */
    public void filteredVectorSearch() throws Exception {
        ResponsePrinter.printMethodInfo("filteredVectorSearch", "带过滤的向量搜索");

        SearchReq searchReq = SearchReq.builder()
            .collectionName(COLLECTION_NAME)
            .data(Collections.singletonList(generateRandomVector(VECTOR_DIM)))
            .topK(10)
            .outputFields(Arrays.asList("id", "text", "category"))
            .filter("category == 'AI' && views >= 100")
            .build();

        System.out.println("  过滤条件: category == 'AI' && views >= 100");
        // 处理结果...
    }

    private List<Float> generateRandomVector(int dimension) {
        Random random = new Random();
        List<Float> vector = new ArrayList<>();
        for (int i = 0; i < dimension; i++) {
            vector.add(random.nextFloat());
        }
        return vector;
    }
}
```

### 5.2 向量检索最佳实践

```java
public class VectorSearchBestPractices {

    /**
     * 最佳实践 1: 预加载索引
     */
    public void preloadIndex() {
        // 搜索前预加载索引到内存
        client.loadCollection(LoadCollectionReq.builder()
            .collectionName("my_collection")
            .build());
    }

    /**
     * 最佳实践 2: 使用批量插入
     */
    public void batchInsert() {
        int batchSize = 1000;
        int totalRecords = 100000;

        for (int i = 0; i < totalRecords; i += batchSize) {
            List<List<Float>> batch = generateBatch(batchSize);
            InsertReq insertReq = InsertReq.builder()
                .collectionName("my_collection")
                .data(Map.of("vector", batch))
                .build();
            client.insert(insertReq);
        }
    }

    /**
     * 最佳实践 3: 监控索引状态
     */
    public void monitorIndex() {
        GetIndexBuildProgressReq progressReq = GetIndexBuildProgressReq.builder()
            .collectionName("my_collection")
            .fieldName("vector")
            .build();

        var progress = client.getIndexBuildProgress(progressReq);
        System.out.println("索引构建进度: " + progress.getProgress() + "%");
    }
}
```

## 6. 常见问题

| 问题 | 原因 | 解决方案 |
|------|------|----------|
| 搜索慢 | ef 参数太小 | 增加 ef 值 |
| 精度低 | 索引类型不匹配 | 根据数据量选择索引 |
| 内存不足 | 向量维度太高 | 使用 PQ/SQ 量化 |
| 结果不稳定 | HNSW 随机性 | 设置固定种子 |

## 7. 扩展阅读

**代码位置**:
- [MilvusQueryDemo.java](src/main/java/com/shuai/elasticsearch/vector/MilvusQueryDemo.java) - 向量搜索实现
- [VectorEmbeddingUtilTest.java](src/test/java/com/shuai/elasticsearch/util/VectorEmbeddingUtilTest.java) - 向量工具测试

- [Vector Search in Elasticsearch](https://www.elastic.co/guide/en/elasticsearch/reference/current/knn-search.html)
- [Milvus Vector Index](https://milvus.io/docs/index.md)
- [HNSW Paper](https://arxiv.org/abs/1603.09320)
- [Ann-Benchmarks](https://github.com/erikbern/ann-benchmarks)
- [上一章: Milvus 入门](10-milvus-introduction.md) | [下一章: Embedding 模型](12-embedding-models.md)

---

**学习进度**: ⬜⬜⬜⬜⬜⬜⬜⬜ 0/18 文档完成

**最后更新**: 2026-01-17
