# Milvus 入门

> 本章介绍 Milvus 向量数据库的基本概念、架构和使用方法，为向量检索打下基础。

## 学习目标

完成本章学习后，你将能够：
- 理解向量数据库的基本概念
- 掌握 Milvus 的核心架构
- 熟悉 Milvus 的基本操作
- 理解向量索引类型

## 1. 向量数据库概述

### 1.1 什么是向量数据库

向量数据库是专门用于**存储、索引和检索高维向量**的数据库系统。

```
┌─────────────────────────────────────────────────────────────────────┐
│                    传统数据库 vs 向量数据库                           │
├─────────────────────┬─────────────────────┬─────────────────────────┤
│       特性          │    传统数据库       │     向量数据库          │
├─────────────────────┼─────────────────────┼─────────────────────────┤
│ 数据类型            │  结构化/半结构化     │     高维向量            │
│ 查询方式            │  精确匹配/范围查询   │     相似度检索          │
│ 索引结构            │  B+Tree / Hash      │     倒排索引 / HNSW     │
│ 检索速度 (大数据量) │   秒级              │     毫秒级              │
│ 适用场景            │  事务处理           │     AI / 推荐 / 搜索    │
└─────────────────────┴─────────────────────┴─────────────────────────┘
```

### 1.2 向量检索的应用场景

| 场景 | 说明 | 示例 |
|------|------|------|
| **语义搜索** | 基于语义的相似度搜索 | "苹果" 匹配水果而非手机 |
| **推荐系统** | 相似内容推荐 | 商品推荐、内容推荐 |
| **异常检测** | 异常向量识别 | 欺诈检测、异常行为 |
| **多模态搜索** | 跨模态检索 | 以文搜图、以图搜图 |
| **RAG** | 检索增强生成 | 知识库问答 |

## 2. Milvus 架构

### 2.1 系统架构

```
┌─────────────────────────────────────────────────────────────────────┐
│                         Milvus 架构图                                │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │                      访问层 (Access Layer)                   │   │
│  │  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐       │   │
│  │  │   SDK    │ │  Restful │ │  Grpc    │ │   GUI    │       │   │
│  │  └──────────┘ └──────────┘ └──────────┘ └──────────┘       │   │
│  └─────────────────────────────────────────────────────────────┘   │
│                              │                                      │
│                              ▼                                      │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │                     协调服务 (Coordination)                  │   │
│  │  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐       │   │
│  │  │  Proxy   │ │  Root    │ │  Query   │ │  Index   │       │   │
│  │  │          │ │  Coord   │ │  Coord   │ │  Coord   │       │   │
│  │  └──────────┘ └──────────┘ └──────────┘ └──────────┘       │   │
│  └─────────────────────────────────────────────────────────────┘   │
│                              │                                      │
│          ┌───────────────────┼───────────────────┐                 │
│          ▼                   ▼                   ▼                 │
│  ┌──────────────┐   ┌──────────────┐   ┌──────────────┐          │
│  │  数据节点    │   │  索引节点    │   │  查询节点    │          │
│  │  (Data Node) │   │ (Index Node) │   │(Query Node)  │          │
│  └──────────────┘   └──────────────┘   └──────────────┘          │
│                              │                                      │
│                              ▼                                      │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │                      存储层 (Storage)                        │   │
│  │  ┌──────────────┐   ┌──────────────┐   ┌──────────────┐     │   │
│  │  │  etcd        │   │   MinIO      │   │  Pulsar/     │     │   │
│  │  │ (元数据)     │   │ (对象存储)   │   │  Kafka       │     │   │
│  │  └──────────────┘   └──────────────┘   └──────────────┘     │   │
│  └─────────────────────────────────────────────────────────────┘   │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### 2.2 核心概念

| 概念 | 说明 | 类比 (ES) |
|------|------|-----------|
| **Collection** | 向量集合 | Index |
| **Schema** | 集合结构 | Mapping |
| **Field** | 字段 | Field |
| **Partition** | 数据分区 | Routing/Shard |
| **Index** | 索引结构 | Index |
| **Search** | 向量搜索 | _search |

### 2.3 数据模型

```
Collection (集合)
├── Schema (模式)
│   ├── Field: id (INT64, 主键)
│   ├── Field: vector (FLOAT_VECTOR, 向量字段)
│   └── Field: metadata (VARCHAR, 标量字段)
├── Partitions (分区)
│   ├── Partition 1
│   │   └── Segments → Indexes
│   └── Partition 2
└── Indexes (索引)
    └── Vector Index (HNSW/IVF)
```

## 3. 基本操作

### 3.1 连接 Milvus

```java
// Milvus Java Client
import io.milvus.v2.client.ConnectConfig;
import io.milvus.v2.client.MilvusClientV2;

public class MilvusConfig {

    private static final String HOST = "localhost";
    private static final int PORT = 19530;

    private static MilvusClientV2 client;

    public static synchronized MilvusClientV2 getClient() {
        if (client == null) {
            ConnectConfig config = ConnectConfig.builder()
                .uri("http://" + HOST + ":" + PORT)
                .build();
            client = new MilvusClientV2(config);
        }
        return client;
    }
}
```

### 3.2 创建集合

```java
// 创建集合示例
import io.milvus.v2.service.collection.request.CreateCollectionReq;
import io.milvus.v2.service.collection.request.AddFieldReq;
import io.milvus.v2.common.DataType;

public class MilvusDemo {

    public void createCollection() {
        // 1. 定义字段
        AddFieldReq field1 = AddFieldReq.builder()
            .fieldName("id")
            .dataType(DataType.Int64)
            .isPrimaryKey(true)
            .build();

        AddFieldReq field2 = AddFieldReq.builder()
            .fieldName("vector")
            .dataType(DataType.FloatVector)
            .dimension(1024)  // 向量维度
            .build();

        AddFieldReq field3 = AddFieldReq.builder()
            .fieldName("text")
            .dataType(DataType.VarChar)
            .maxLength(65535)
            .build();

        // 2. 创建集合请求
        CreateCollectionReq request = CreateCollectionReq.builder()
            .collectionName("my_collection")
            .addFieldList(field1, field2, field3)
            .build();

        // 3. 执行创建
        client.createCollection(request);
        System.out.println("集合创建成功");
    }
}
```

### 3.3 插入数据

```java
import io.milvus.v2.service.vector.request.InsertReq;
import io.milvus.v2.service.vector.request.DataType;

public class MilvusDemo {

    public void insertVectors() {
        // 准备向量数据
        List<List<Float>> vectors = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            vectors.add(generateRandomVector(1024));
        }

        // 准备标量数据
        List<Long> ids = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            ids.add((long) i);
        }

        List<String> texts = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            texts.add("文档 " + i + " 的内容");
        }

        InsertReq insertReq = InsertReq.builder()
            .collectionName("my_collection")
            .data(Map.of(
                "id", ids,
                "vector", vectors,
                "text", texts
            ))
            .build();

        InsertResp response = client.insert(insertReq);
        System.out.println("插入数量: " + response.getInsertCnt());
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

### 3.4 向量搜索

```java
import io.milvus.v2.service.vector.request.SearchReq;
import io.milvus.v2.service.vector.request.DataType;
import io.milvus.v2.service.vector.response.SearchResp;

public class MilvusDemo {

    public void searchVectors() {
        // 准备查询向量
        List<Float> queryVector = generateRandomVector(1024);

        // 构建搜索请求
        SearchReq searchReq = SearchReq.builder()
            .collectionName("my_collection")
            .data(Collections.singletonList(queryVector))
            .topK(10)  // 返回 Top-K 结果
            .outputFields(Arrays.asList("id", "text"))
            .build();

        // 执行搜索
        SearchResp response = client.search(searchReq);

        // 处理结果
        for (SearchResp.SearchResult result : response.getSearchResults()) {
            System.out.println("查询结果:");
            for (io.milvus.v2.service.vector.response.SearchResp.Entity entity : result.getEntities()) {
                System.out.println("  ID: " + entity.get("id"));
                System.out.println("  文本: " + entity.get("text"));
                System.out.println("  距离: " + entity.getScore());
            }
        }
    }
}
```

## 4. 索引类型

### 4.1 索引类型对比

| 索引类型 | 原理 | 适用场景 | 精度 | 内存 |
|----------|------|----------|------|------|
| **FLAT** | 暴力搜索 | 小数据量 | 100% | 高 |
| **IVF_FLAT** | 聚类索引 | 中等数据量 | 高 | 中 |
| **IVF_SQ8** | 量化压缩 | 大数据量 | 中 | 低 |
| **HNSW** | 图索引 | 大数据量、高精度 | 很高 | 高 |
| **PQ** | 乘积量化 | 超大规模 | 中 | 低 |
| **SCANN** | 向量压缩 | 超大规模 | 中 | 低 |

### 4.2 创建索引

```java
import io.milvus.v2.service.index.request.CreateIndexReq;

public void createIndex() {
    // 创建 HNSW 索引
    CreateIndexReq indexReq = CreateIndexReq.builder()
        .collectionName("my_collection")
        .fieldName("vector")
        .indexType("HNSW")
        .metricType("COSINE")  // 余弦相似度
        .extraParams(Map.of(
            "M", 16,          // 邻居数
            "efConstruction", 200  // 构造时搜索深度
        ))
        .build();

    client.createIndex(indexReq);
    System.out.println("索引创建成功");
}
```

### 4.3 索引参数

| 参数 | HNSW | IVF_FLAT | 说明 |
|------|------|----------|------|
| M | ✅ | ❌ | 每个节点的邻居数 |
| efConstruction | ✅ | ❌ | 索引构建时的搜索深度 |
| ef | ✅ | ❌ | 查询时的搜索深度 |
| nlist | ❌ | ✅ | 聚类数量 |
| nprobe | ❌ | ✅ | 查询时探测的聚类数 |

## 5. 数据分区

### 5.1 分区操作

```java
import io.milvus.v2.service.partition.request.CreatePartitionReq;
import io.milvus.v2.service.partition.request.LoadPartitionReq;

// 创建分区
CreatePartitionReq partitionReq = CreatePartitionReq.builder()
    .collectionName("my_collection")
    .partitionName("partition_2024")
    .build();
client.createPartition(partitionReq);

// 查看分区
ListPartitionsReq listReq = ListPartitionsReq.builder()
    .collectionName("my_collection")
    .build();
```

### 5.2 分区搜索

```java
// 指定分区搜索
SearchReq searchReq = SearchReq.builder()
    .collectionName("my_collection")
    .data(Collections.singletonList(queryVector))
    .partitionNames(Arrays.asList("partition_2024"))
    .topK(10)
    .build();
```

## 6. 实践部分

### 6.1 完整示例

```java
// MilvusQueryDemo.java
package com.shuai.elasticsearch.vector;

import io.milvus.v2.client.MilvusClientV2;
import io.milvus.v2.common.DataType;
import io.milvus.v2.service.collection.request.CreateCollectionReq;
import io.milvus.v2.service.collection.request.AddFieldReq;
import io.milvus.v2.service.collection.request.LoadCollectionReq;
import io.milvus.v2.service.vector.request.InsertReq;
import io.milvus.v2.service.vector.request.SearchReq;
import io.milvus.v2.service.vector.request.DataType;
import io.milvus.v2.service.vector.request.CreateIndexReq;
import com.shuai.elasticsearch.util.ResponsePrinter;

import java.util.*;

public class MilvusQueryDemo {

    private final MilvusClientV2 client;
    private static final String COLLECTION_NAME = "document_vectors";

    public MilvusQueryDemo() {
        // Milvus 配置
        this.client = new MilvusClientV2("http://localhost:19530");
    }

    /**
     * 运行所有 Milvus 演示
     */
    public void runAllDemos() throws Exception {
        ResponsePrinter.printMethodInfo("MilvusQueryDemo", "Milvus 向量检索演示");

        createCollection();
        insertVectors();
        createIndex();
        searchVectors();
    }

    /**
     * 创建集合
     */
    private void createCollection() throws Exception {
        ResponsePrinter.printMethodInfo("createCollection", "创建集合");

        // 定义字段
        AddFieldReq idField = AddFieldReq.builder()
            .fieldName("id")
            .dataType(DataType.Int64)
            .isPrimaryKey(true)
            .build();

        AddFieldReq vectorField = AddFieldReq.builder()
            .fieldName("vector")
            .dataType(DataType.FloatVector)
            .dimension(1024)
            .build();

        AddFieldReq textField = AddFieldReq.builder()
            .fieldName("text")
            .dataType(DataType.VarChar)
            .maxLength(65535)
            .build();

        // 创建集合
        CreateCollectionReq request = CreateCollectionReq.builder()
            .collectionName(COLLECTION_NAME)
            .addFieldList(idField, vectorField, textField)
            .build();

        client.createCollection(request);
        System.out.println("  集合创建成功: " + COLLECTION_NAME);
    }

    /**
     * 插入向量
     */
    private void insertVectors() throws Exception {
        ResponsePrinter.printMethodInfo("insertVectors", "插入向量数据");

        // 生成测试数据
        List<Long> ids = new ArrayList<>();
        List<List<Float>> vectors = new ArrayList<>();
        List<String> texts = new ArrayList<>();

        for (int i = 0; i < 100; i++) {
            ids.add((long) i);
            vectors.add(generateRandomVector(1024));
            texts.add("这是文档 " + i + " 的内容，包含关于人工智能和机器学习的信息");
        }

        InsertReq insertReq = InsertReq.builder()
            .collectionName(COLLECTION_NAME)
            .data(Map.of(
                "id", ids,
                "vector", vectors,
                "text", texts
            ))
            .build();

        client.insert(insertReq);
        System.out.println("  插入 100 条向量数据");
    }

    /**
     * 创建索引
     */
    private void createIndex() throws Exception {
        ResponsePrinter.printMethodInfo("createIndex", "创建向量索引");

        CreateIndexReq indexReq = CreateIndexReq.builder()
            .collectionName(COLLECTION_NAME)
            .fieldName("vector")
            .indexType("HNSW")
            .metricType("COSINE")
            .extraParams(Map.of("M", 16, "efConstruction", 200))
            .build();

        client.createIndex(indexReq);
        System.out.println("  HNSW 索引创建成功");
    }

    /**
     * 向量搜索
     */
    private void searchVectors() throws Exception {
        ResponsePrinter.printMethodInfo("searchVectors", "向量相似度搜索");

        // 查询向量
        List<Float> queryVector = generateRandomVector(1024);

        SearchReq searchReq = SearchReq.builder()
            .collectionName(COLLECTION_NAME)
            .data(Collections.singletonList(queryVector))
            .topK(5)
            .outputFields(Arrays.asList("id", "text"))
            .build();

        var results = client.search(searchReq).getSearchResults();

        System.out.println("  搜索结果 (Top 5):");
        for (var result : results) {
            for (var entity : result.getEntities()) {
                System.out.println("    ID: " + entity.get("id") +
                    ", 相似度: " + String.format("%.4f", entity.getScore()));
            }
        }
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

### 6.2 Docker 启动 Milvus

```yaml
# docker-compose.yml
version: '3.8'

services:
  etcd:
    image: quay.io/coreos/etcd:v3.5.16
    container_name: etcd
    command: etcd -advertise-client-urls http://0.0.0.0:2379 -listen-client-urls http://0.0.0.0:2379
    ports:
      - "2379:2379"
    networks:
      - milvus

  minio:
    image: minio/minio:latest
    container_name: minio
    command: server /data --console-address :9001
    ports:
      - "9000:9000"
      - "9001:9001"
    environment:
      MINIO_ROOT_USER: admin
      MINIO_ROOT_PASSWORD: admin123
    networks:
      - milvus

  milvus:
    image: milvusdb/milvus:v2.5.10
    container_name: milvus
    command: ["milvus", "run", "standalone"]
    ports:
      - "19530:19530"
      - "9091:9091"
    volumes:
      - milvus-data:/var/lib/milvus
    depends_on:
      - etcd
      - minio
    networks:
      - milvus

networks:
  milvus:
    driver: bridge

volumes:
  milvus-data:
```

## 7. 常见问题

| 问题 | 原因 | 解决方案 |
|------|------|----------|
| 连接超时 | 服务未启动 | 检查 Docker 容器状态 |
| 索引加载慢 | 数据量大 | 预加载索引 |
| 搜索慢 | nprobe 太小 | 增加 nprobe |
| 精度低 | 索引类型不匹配 | 根据场景选择索引 |

## 8. 扩展阅读

**代码位置**:
- [MilvusQueryDemo.java](src/main/java/com/shuai/elasticsearch/vector/MilvusQueryDemo.java) - Milvus 向量检索演示
- [MilvusConfig.java](src/main/java/com/shuai/elasticsearch/config/MilvusConfig.java) - Milvus 客户端配置
- [VectorEmbeddingUtil.java](src/main/java/com/shuai/elasticsearch/util/VectorEmbeddingUtil.java) - 向量工具类

- [Milvus 官方文档](https://milvus.io/docs/)
- [Milvus Java SDK](https://milvus.io/docs/java-sdk.md)
- [向量索引类型](https://milvus.io/docs/index.md)
- [HNSW 原理](https://arxiv.org/abs/1603.09320)
- [上一章: 索引管理](09-es-index-management.md) | [下一章: 向量检索](11-vector-search.md)

---

**学习进度**: ⬜⬜⬜⬜⬜⬜⬜⬜ 0/18 文档完成

**最后更新**: 2026-01-17
