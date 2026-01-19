# Elasticsearch 搜索引擎模块

> Elasticsearch 全文检索与数据分析模块

## 学习文档

本模块配套学习文档位于 [learn_docs](learn_docs/README.md) 目录：

### Elasticsearch

| 主题 | 文档链接 |
|------|----------|
| [Elasticsearch 基础](#1-elasticsearch-概述) | [learn_docs/01-basic/](learn_docs/01-basic/README.md) |
| [Elasticsearch 进阶](#6-聚合分析) | [learn_docs/02-advanced/](learn_docs/02-advanced/README.md) |
| [高级主题](#8-地理查询) | [learn_docs/03-advanced-topics/](learn_docs/03-advanced-topics/README.md) |
| [向量检索与 RAG](#11-milvus-向量检索) | [learn_docs/04-vector-rag/](learn_docs/04-vector-rag/README.md) |

> **提示**: 点击主题名称可跳转到下方对应章节。

## 目录

- [简介](#简介)
- [项目结构](#项目结构)
- [包说明](#包说明)
- [知识点索引](#知识点索引)
- [运行示例](#运行示例)
- [单元测试](#单元测试)
- [skills](#skills)
- [依赖](#依赖)

## 简介

本模块涵盖 Elasticsearch 全文检索、聚合分析、地理查询的完整实践。

Elasticsearch 是一个分布式、RESTful 风格的搜索和数据分析引擎，能够解决不断涌现出的各种用例。作为 Elastic Stack 的核心，它集中存储您的数据，帮助您发现意料之中以及意料之外的情况。

## 项目结构

```
elasticsearch/
├── pom.xml                                      # Maven 配置
├── README.md                                    # 项目文档
├── deploy/
│   └── docker-compose.yml                       # Docker Compose 部署配置
└── src/main/java/com/shuai/elasticsearch/
│   ├── ElasticsearchDemo.java                  # 模块入口，主类
│   ├── config/
│   │   ├── ElasticsearchConfig.java            # ES 客户端配置（单例）
│   │   ├── IndexConfig.java                    # 索引管理配置
│   │   ├── MilvusConfig.java                   # Milvus 客户端配置
│   │   └── SiliconFlowConfig.java              # SiliconFlow API 配置
│   ├── model/
│   │   ├── BlogDocument.java                   # 博客文档实体
│   │   └── LocationDocument.java               # 地理位置文档实体
│   ├── util/
│   │   ├── ResponsePrinter.java                # 响应格式化输出工具
│   │   ├── DataInitUtil.java                   # 测试数据初始化工具
│   │   └── VectorEmbeddingUtil.java            # 向量嵌入工具类
│   ├── overview/
│   │   └── ElasticsearchOverviewDemo.java      # ES 概述：概念、架构、REST API
│   ├── document/
│   │   └── DocumentOperationDemo.java          # 文档操作：CRUD、批量
│   ├── query/
│   │   ├── QueryDemo.java                      # 查询：全文检索、精确匹配、复合查询
│   │   ├── NestedQueryDemo.java                # 嵌套查询：nested 类型与查询
│   │   └── FunctionScoreDemo.java              # 评分函数：function_score
│   ├── aggregation/
│   │   └── AggregationDemo.java                # 聚合：指标聚合、桶聚合、管道聚合
│   ├── fulltext/
│   │   └── FullTextSearchDemo.java             # 全文检索：分词器、高亮、相关性
│   ├── geo/
│   │   └── GeoQueryDemo.java                   # 地理查询：geo_point、geo_shape
│   ├── async/
│   │   └── AsyncClientDemo.java                # 异步客户端：CompletableFuture
│   ├── index/
│   │   └── IndexManagementDemo.java            # 索引管理：创建、删除、别名
│   ├── embedding/                              # 向量嵌入模块
│   │   ├── EmbeddingService.java               # Embedding 服务接口
│   │   ├── EmbeddingServiceFactory.java        # Embedding 服务工厂
│   │   ├── BgeEmbeddingService.java            # BGE 向量嵌入服务
│   │   ├── RerankerService.java                # Cross-Encoder 重排序服务
│   │   ├── MockEmbeddingService.java           # 模拟向量服务（测试用）
│   │   └── OpenAIEmbeddingService.java         # OpenAI Embedding 服务
│   ├── data/
│   │   └── BlogDataGenerator.java              # 测试数据生成器（13篇高质量文档）
│   ├── vector/
│   │   └── MilvusQueryDemo.java                # Milvus 向量检索：相似度搜索
│   └── rag/
│       ├── RAGDemo.java                        # RAG 检索增强生成
│       └── HybridSearchDemo.java               # 混合检索：ES + Milvus
│   ├── llm/
│   │   └── OpenAIClient.java                   # OpenAI API 客户端：聊天/流式/异步
│   └── monitoring/
│       ├── MetricNames.java                    # Prometheus 指标名称常量
│       └── PrometheusConfig.java               # 监控配置与指标记录
```

## 包说明

| 包名 | 职责 | 关键类 |
|------|------|--------|
| `elasticsearch` | 根包，主入口 | ElasticsearchDemo |
| `elasticsearch.config` | 配置管理 | ElasticsearchConfig, IndexConfig, MilvusConfig, SiliconFlowConfig |
| `elasticsearch.model` | 数据模型 | BlogDocument, LocationDocument |
| `elasticsearch.util` | 工具类 | ResponsePrinter, DataInitUtil, VectorEmbeddingUtil |
| `elasticsearch.overview` | ES 核心概念和架构 | ElasticsearchOverviewDemo |
| `elasticsearch.document` | 文档 CRUD 操作 | DocumentOperationDemo |
| `elasticsearch.query` | 查询类型实现 | QueryDemo, NestedQueryDemo, FunctionScoreDemo |
| `elasticsearch.aggregation` | 聚合分析功能 | AggregationDemo |
| `elasticsearch.fulltext` | 全文检索高级功能 | FullTextSearchDemo |
| `elasticsearch.geo` | 地理位置查询 | GeoQueryDemo |
| `elasticsearch.async` | 异步客户端 | AsyncClientDemo |
| `elasticsearch.index` | 索引管理 | IndexManagementDemo |
| `elasticsearch.embedding` | 向量嵌入服务 | EmbeddingService, BgeEmbeddingService, RerankerService |
| `elasticsearch.vector` | Milvus 向量检索 | MilvusQueryDemo |
| `elasticsearch.rag` | RAG 检索增强 | RAGDemo, HybridSearchDemo |
| `elasticsearch.data` | 测试数据生成 | BlogDataGenerator |
| `elasticsearch.llm` | LLM 集成 | OpenAIClient |
| `elasticsearch.monitoring` | 监控配置 | MetricNames, PrometheusConfig |

## 知识点索引

### 1. Elasticsearch 概述

**核心概念**: Index、Document、Mapping、Shard、Replica

- Index (索引) - 相当于数据库
- Document (文档) - 相当于数据行
- Field (字段) - 相当于数据列
- Mapping (映射) - 相当于表结构
- Shard (分片) - 数据分片
- Replica (副本) - 数据副本

**代码位置**: [ElasticsearchOverviewDemo.java](src/main/java/com/shuai/elasticsearch/overview/ElasticsearchOverviewDemo.java)

### 2. 文档操作

**功能**: 插入、更新、删除、批量操作

- 单条/批量插入文档
- 全量/部分更新
- 按 ID/条件删除
- Bulk API 批量操作

**代码位置**: [DocumentOperationDemo.java](src/main/java/com/shuai/elasticsearch/document/DocumentOperationDemo.java)

### 3. 查询类型

**全文检索查询**:
- `match` - 标准全文检索
- `match_phrase` - 短语匹配
- `multi_match` - 多字段匹配
- `query_string` - 查询字符串

**精确匹配查询**:
- `term` - 单词精确匹配
- `terms` - 多值精确匹配
- `range` - 范围查询
- `exists` - 字段存在查询
- `prefix` / `wildcard` / `regexp` - 前缀/通配符/正则匹配

**复合查询**:
- `bool` - 布尔查询 (must/filter/should/must_not)
- `boosting` - 降级查询
- `constant_score` - 常量评分

**代码位置**: [QueryDemo.java](src/main/java/com/shuai/elasticsearch/query/QueryDemo.java)

### 4. 嵌套查询

**功能**: 处理数组中的嵌套对象

- `nested` 数据类型
- `nested` 查询
- 嵌套聚合 (nested aggregation)
- 反嵌套聚合 (reverse_nested)

**代码位置**: [NestedQueryDemo.java](src/main/java/com/shuai/elasticsearch/query/NestedQueryDemo.java)

### 5. 评分函数

**功能**: 自定义相关性评分

- `script_score` - 脚本评分
- `random_score` - 随机评分
- `field_value_factor` - 字段值因子
- 衰减函数 (gauss, linear, exp)
- score_mode / boost_mode 配置

**代码位置**: [FunctionScoreDemo.java](src/main/java/com/shuai/elasticsearch/query/FunctionScoreDemo.java)

### 6. 聚合分析

**指标聚合**:
- `avg` / `sum` / `max` / `min` - 基本统计
- `stats` / `extended_stats` - 综合统计
- `cardinality` - 去重计数
- `percentiles` - 百分位

**桶聚合**:
- `terms` - 术语聚合
- `range` / `histogram` / `date_histogram` - 范围聚合

**管道聚合**:
- `avg_bucket` / `max_bucket` / `min_bucket` / `sum_bucket`
- `cumulative_sum` - 累计求和

**代码位置**: [AggregationDemo.java](src/main/java/com/shuai/elasticsearch/aggregation/AggregationDemo.java)

### 7. 全文检索

**分词器配置**:
- 内置分词器 (standard, keyword, whitespace 等)
- IK 分词器 (ik_max_word, ik_smart)
- 自定义分词器

**高亮显示**:
- 基本高亮配置
- 自定义高亮标签
- 多字段高亮

**相关性排序**:
- BM25 算法
- `function_score` 自定义评分
- 字段权重配置

**代码位置**: [FullTextSearchDemo.java](src/main/java/com/shuai/elasticsearch/fulltext/FullTextSearchDemo.java)

### 8. 地理查询

**地理位置类型**:
- `geo_point` - 经纬度点
- `geo_shape` - 几何图形

**地理查询**:
- `geo_distance` - 距离查询
- `geo_bounding_box` - 矩形范围
- `geo_polygon` - 多边形范围
- `geo_radius` - 圆形范围

**地理位置聚合**:
- `geotile_grid` - 地理网格
- `geohash_grid` - Geohash 网格
- `geo_centroid` - 地理中心

**代码位置**: [GeoQueryDemo.java](src/main/java/com/shuai/elasticsearch/geo/GeoQueryDemo.java)

### 9. 异步客户端

**功能**: 非阻塞异步操作

- `ElasticsearchAsyncClient` - 异步客户端
- `CompletableFuture` - Java 并发编程
- 并行请求处理
- 超时控制

**代码位置**: [AsyncClientDemo.java](src/main/java/com/shuai/elasticsearch/async/AsyncClientDemo.java)

### 10. 索引管理

**功能**: 索引生命周期管理

- 创建索引 (自定义 mapping/settings)
- 查询索引信息 (mapping/settings/stats)
- 更新索引设置
- 索引别名管理
- 删除索引

**代码位置**: [IndexManagementDemo.java](src/main/java/com/shuai/elasticsearch/index/IndexManagementDemo.java)

### 11. Milvus 向量检索

**功能**: 基于 Milvus 的向量相似度搜索

- 向量插入: 存储文档向量
- 相似度搜索: 基于余弦相似度的检索
- 批量操作: 批量插入和搜索
- 过滤搜索: 带标量条件的向量搜索

**索引类型**:
- IVF_FLAT: 精确召回，速度快
- IVF_SQ8: 量化压缩，节省内存
- HNSW: 高速召回，精度高

**代码位置**: [MilvusQueryDemo.java](src/main/java/com/shuai/elasticsearch/vector/MilvusQueryDemo.java)

### 12. RAG 检索增强

**功能**: Retrieval-Augmented Generation 检索增强生成

- 文档分块: 文本分块处理
- 向量化: 文本转向量
- 上下文构建: 检索结果合并
- 提示词工程: RAG 提示词模板

**RAG 流程**:
1. 文档处理 -> 分块、向量化
2. 向量存储 -> Milvus
3. 用户查询 -> 向量化
4. 向量检索 -> 相似文档
5. 上下文构建 -> 拼接检索结果
6. LLM 生成 -> 基于上下文生成回答

**代码位置**: [RAGDemo.java](src/main/java/com/shuai/elasticsearch/rag/RAGDemo.java)

### 13. 混合检索 (ES + Milvus)

**功能**: 结合 Elasticsearch 和 Milvus 的混合检索

- 并行检索: ES 和 Milvus 同时搜索
- 结果融合: RRF 加权平均、交叉编码重排
- 查询路由: 根据查询类型选择策略

**结果融合策略**:
- RRF (Reciprocal Rank Fusion): 倒数排名融合
- 加权平均: α * ES_score + (1-α) * Vector_score
- 交叉编码重排: 使用交叉编码器精排

**代码位置**: [HybridSearchDemo.java](src/main/java/com/shuai/elasticsearch/rag/HybridSearchDemo.java)

### 14. 向量检索基础

**核心概念**:
- **文本向量化 (Embedding)**: 将文本转换为高维向量表示，使语义相似的文本在向量空间中接近
- **向量相似度**: 基于余弦相似度 (Cosine Similarity) 衡量向量间的相似程度
- **Embedding 模型**: BGE, OpenAI text-embedding, M3E 等预训练模型

**向量检索流程**:
```
用户查询 → 向量化 → 在向量空间中搜索 → 返回相似文档
```

**代码位置**: [VectorEmbeddingUtil.java](src/main/java/com/shuai/elasticsearch/util/VectorEmbeddingUtil.java)

### 15. 两阶段检索 (召回 + 排序)

**检索架构**:
```
┌─────────────────────────────────────────────────────────┐
│                      用户查询                            │
└─────────────────────────┬───────────────────────────────┘
                          │
          ┌───────────────┼───────────────┐
          ▼               ▼               ▼
    ┌──────────┐   ┌──────────┐   ┌──────────┐
    │ 召回阶段 │   │ 排序阶段  │   │  生成阶段 │
    │ (Recall) │   │  (Rank)  │   │ (Generate)│
    └────┬─────┘   └────┬─────┘   └────┬─────┘
         │              │              │
    Embedding模型    Cross-Encoder    LLM
    (Bi-Encoder)      重排序
    召回 Top 100     精排 Top 10
```

**召回阶段 (Recall)**:
- 使用 Bi-Encoder (Embedding 模型) 快速处理查询和文档
- 返回相似度最高的 N 个候选文档 (如 Top 100)
- 特点：速度快、精度较低

**排序阶段 (Rank)**:
- 使用 Cross-Encoder 对候选文档重新评分
- 返回最终排序结果 (如 Top 10)
- 特点：速度慢、精度高

**代码位置**:
- [BgeEmbeddingService.java](src/main/java/com/shuai/elasticsearch/embedding/BgeEmbeddingService.java) - 召回服务
- [RerankerService.java](src/main/java/com/shuai/elasticsearch/embedding/RerankerService.java) - 重排序服务

### 16. SiliconFlow API 集成

**SiliconFlow** 是一家国产 AI 推理平台，提供高性价比的 Embedding 和 Reranker 服务。

**支持的模型**:

| 模型类型 | 模型名称 | 维度 | 特点 |
|----------|----------|------|------|
| Embedding | BAAI/bge-large-zh-v1.5 | 1024 | 中文最优，高质量 |
| Embedding | BAAI/bge-base-zh-v1.5 | 768 | 平衡性能和精度 |
| Embedding | BAAI/bge-small-zh | 384 | 轻量快速 |
| Reranker | BAAI/bge-reranker-v2-m3 | - | 中英文兼优 |

**配置文件**: [application-siliconflow.properties](src/main/resources/application-siliconflow.properties)

```properties
# SiliconFlow API 配置
sf.api.key=sk-xxxxx  # 从 SiliconFlow 平台获取
sf.embedding.model=BAAI/bge-large-zh-v1.5
sf.reranker.model=BAAI/bge-reranker-v2-m3
sf.api.url=https://api.siliconflow.cn/v1
```

**代码位置**: [SiliconFlowConfig.java](src/main/java/com/shuai/elasticsearch/config/SiliconFlowConfig.java)
**配置类**: [BgeEmbeddingService.java](src/main/java/com/shuai/elasticsearch/embedding/BgeEmbeddingService.java) - 真实调用 SiliconFlow API
**重排序**: [RerankerService.java](src/main/java/com/shuai/elasticsearch/embedding/RerankerService.java) - Cross-Encoder 重排序

### 17. OpenAI LLM 集成

**功能**: 集成 OpenAI API 实现 LLM 生成功能

- 聊天补全: chatCompletion() 同步调用
- 流式输出: streamingChat() 实时响应
- 异步调用: asyncChatCompletion() 非阻塞
- Token 统计: 统计输入输出 token 数

**配置参数**:
- `OPENAI_API_KEY`: OpenAI API Key
- `OPENAI_MODEL`: 模型名称 (默认 gpt-3.5-turbo)
- `OPENAI_BASE_URL`: API 基础 URL (可选)
- `OPENAI_TIMEOUT`: 超时时间 (默认 60 秒)

**代码位置**: [OpenAIClient.java](src/main/java/com/shuai/elasticsearch/llm/OpenAIClient.java)

**使用示例**:
```java
// 初始化客户端
OpenAIClient client = new OpenAIClient("sk-your-api-key");

// 简单聊天
String response = client.chat("你好，请介绍一下自己");

// 带系统提示
List<Message> messages = List.of(Message.user("北京有哪些好玩的地方？"));
String response = client.chatCompletion(messages, "你是一个专业的导游");

// 流式输出
client.streamingChat(messages, (chunk, progress) -> {
    System.out.print(chunk);  // 实时输出
});

// 异步调用
CompletableFuture<String> future = client.asyncChatCompletion(messages);
future.thenAccept(response -> System.out.println(response));
```

### 18. 监控告警

**功能**: Prometheus 监控指标与告警配置

**指标类型**:
- ES 指标: 集群健康、查询延迟、查询速率
- Milvus 指标: 向量数量、查询延迟、插入速率
- RAG 指标: Embedding 延迟、LLM 延迟、查询总数
- JVM 指标: 堆内存使用、GC 次数、线程数

**监控配置**: [PrometheusConfig.java](src/main/java/com/shuai/elasticsearch/monitoring/PrometheusConfig.java)

**指标名称**: [MetricNames.java](src/main/java/com/shuai/elasticsearch/monitoring/MetricNames.java)

**Prometheus 配置**: [prometheus.yml](deploy/prometheus/prometheus.yml)

**告警规则**: [elasticsearch-alerts.yml](deploy/prometheus/rules/elasticsearch-alerts.yml)

**Kibana 仪表盘**: [dashboard.json](deploy/kibana/dashboard.json)

### 19. 设计模式应用

本项目使用了多种经典设计模式：

| 模式 | 示例类 | 说明 |
|------|--------|------|
| **单例模式** | `ElasticsearchConfig` | 确保全局只有一个 ES 客户端实例，避免重复连接 |
| **工厂模式** | `EmbeddingServiceFactory` | 根据配置动态创建不同的 Embedding 服务实现 |
| **Builder 模式** | `BlogDocument.builder()` | 对象，提高链式构建复杂代码可读性 |
| **策略模式** | 不同的查询实现 | 封装可互换的查询算法，如不同类型的评分函数 |
| **模板方法** | 各 Demo 类的 `runAllDemos()` | 定义算法骨架，具体步骤由子类实现 |

**代码示例 - 单例模式**:
```java
public class ElasticsearchConfig {
    private static ElasticsearchClient client;

    public static synchronized ElasticsearchClient getClient() {
        if (client == null) {
            client = new ElasticsearchClient(config);
        }
        return client;
    }
}
```

**代码示例 - Builder 模式**:
```java
BlogDocument doc = BlogDocument.builder()
    .id("1")
    .title("Elasticsearch 教程")
    .author("张三")
    .views(100)
    .build();
```

### 18. 故障排查指南

#### 常见问题及解决方案

| 问题 | 可能原因 | 解决方案 |
|------|----------|----------|
| 连接超时 | ES 服务未启动 | 检查 Docker 容器: `docker ps \| grep elasticsearch` |
| 索引不存在 | 未创建索引或名称错误 | 运行 `IndexManagementDemo` 创建索引 |
| 分词效果差 | 未安装 IK 分词器 | 安装 IK 分词器插件 |
| 向量检索无结果 | Milvus 服务未启动 | 检查 Milvus 容器状态 |
| API Key 无效 | Key 配置错误或过期 | 检查 `application-siliconflow.properties` 配置 |

#### 调试命令

```bash
# 检查 ES 健康状态
curl -X GET "localhost:9200/_cluster/health?pretty"

# 查看所有索引
curl -X GET "localhost:9200/_cat/indices?v"

# 测试分词效果
curl -X POST "localhost:9200/blog/_analyze" -H 'Content-Type: application/json' -d'
{
  "analyzer": "ik_max_word",
  "text": "Elasticsearch 教程"
}'

# 检查 Milvus 健康状态
curl -X GET "http://localhost:9091/healthz"

# 查看服务端口占用
netstat -tlnp | grep -E "9200|19530"
```

### 19. 推荐学习路径

按照以下顺序学习，可以循序渐进掌握本项目：

#### 阶段 1: 基础入门 (1-2天)
1. [x] ES 核心概念 → `ElasticsearchOverviewDemo`
2. [x] 文档 CRUD 操作 → `DocumentOperationDemo`
3. [x] 索引管理 → `IndexManagementDemo`

#### 阶段 2: 查询进阶 (2-3天)
1. [x] 基础查询类型 → `QueryDemo`
2. [x] 全文检索 → `FullTextSearchDemo`
3. [x] 嵌套查询 → `NestedQueryDemo`
4. [x] 评分函数 → `FunctionScoreDemo`

#### 阶段 3: 高级功能 (2-3天)
1. [x] 聚合分析 → `AggregationDemo`
2. [x] 地理查询 → `GeoQueryDemo`
3. [x] 异步操作 → `AsyncClientDemo`

#### 阶段 4: 向量检索与 RAG (2-3天)
1. [x] 向量检索基础 → `VectorEmbeddingUtil`
2. [x] Milvus 向量检索 → `MilvusQueryDemo`
3. [x] 两阶段检索 → `BgeEmbeddingService` + `RerankerService`
4. [x] RAG 检索增强 → `RAGDemo`
5. [x] 混合检索 → `HybridSearchDemo`

### 21. API 参考链接

| 主题 | 官方文档 |
|------|----------|
| Elasticsearch Java Client | https://www.elastic.co/guide/en/elasticsearch/client/java-api-client/current/index.html |
| Elasticsearch Query DSL | https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl.html |
| Elasticsearch Aggregations | https://www.elastic.co/guide/en/elasticsearch/reference/current/aggregations.html |
| IK 分词器 | https://github.com/medcl/elasticsearch-analysis-ik |
| Milvus Java SDK | https://milvus.io/docs/v2.5.x/java.md |
| BGE Embedding 模型 | https://github.com/FlagOpen/FlagEmbedding |
| SiliconFlow API | https://cloud.siliconflow.cn/docs |
| OpenAI Chat API | https://platform.openai.com/docs/api-reference/chat |
| Prometheus | https://prometheus.io/docs/introduction/overview/ |

## 运行示例

### 前置条件

确保所有依赖服务已启动：

```bash
# 方式1: Docker Compose 一键启动 (推荐)
docker-compose -f deploy/docker-compose.yml up -d

# 方式2: 手动启动各服务
# Elasticsearch
docker run -d --name es \
  -p 9200:9200 -p 9300:9300 \
  -e "discovery.type=single-node" \
  docker.elastic.co/elasticsearch/elasticsearch:8.18.0

# Milvus (可选，向量检索功能需要)
docker run -d --name milvus \
  -p 19530:19530 \
  -p 9091:9091 \
  milvusdb/milvus:v2.5.10
```

### 运行命令

```bash
# 编译项目
mvn compile -q -pl elasticsearch

# 运行主类
mvn -pl elasticsearch exec:java \
  -Dexec.mainClass=com.shuai.elasticsearch.ElasticsearchDemo
```

### 配置说明

支持通过环境变量和配置文件两种方式配置：

**方式1: 环境变量**
```bash
# Elasticsearch
export ES_HOST=localhost
export ES_PORT=9200
export ES_SCHEME=http

# Milvus (可选)
export MILVUS_HOST=localhost
export MILVUS_PORT=19530

# SiliconFlow API (可选，向量检索需要)
export SF_API_KEY=sk-your-api-key
```

**方式2: 配置文件**
```properties
# 配置文件模板位于 src/main/resources/*.example
# 使用前请复制并填写实际配置：

# 1. 复制 SiliconFlow 配置模板
cp src/main/resources/application-siliconflow.properties.example \
   src/main/resources/application-siliconflow.properties

# 2. 编辑配置文件，填入您的 API Key
# sf.api.key=sk-your-api-key
# sf.embedding.model=BAAI/bge-large-zh-v1.5
# sf.reranker.model=BAAI/bge-reranker-v2-m3
```

> **注意**: `*.properties` 文件已加入 `.gitignore`，不会提交到版本控制。

## 单元测试

### 运行测试

```bash
# 运行所有测试
mvn test -pl elasticsearch

# 运行特定测试类
mvn test -pl elasticsearch -Dtest=ElasticsearchConfigTest

# 运行需要 ES 连接的配置环境变量
ES_HOST=localhost mvn test -pl elasticsearch
```

### 测试类说明

| 测试类 | 测试内容 | 文件位置 |
|--------|----------|----------|
| `ElasticsearchConfigTest` | 客户端初始化、单例模式、连接测试 | [ElasticsearchConfigTest.java](src/test/java/com/shuai/elasticsearch/ElasticsearchConfigTest.java) |
| `DocumentModelTest` | 文档模型创建、属性设置、Builder 模式 | [DocumentModelTest.java](src/test/java/com/shuai/elasticsearch/DocumentModelTest.java) |
| `DocumentOperationDemoTest` | 文档 CRUD、批量操作 | [DocumentOperationDemoTest.java](src/test/java/com/shuai/elasticsearch/document/DocumentOperationDemoTest.java) |
| `QueryDemoTest` | 查询类型测试 | [QueryDemoTest.java](src/test/java/com/shuai/elasticsearch/query/QueryDemoTest.java) |
| `AggregationDemoTest` | 聚合分析测试 | [AggregationDemoTest.java](src/test/java/com/shuai/elasticsearch/aggregation/AggregationDemoTest.java) |
| `FullTextSearchTest` | 全文检索测试 | [FullTextSearchTest.java](src/test/java/com/shuai/elasticsearch/fulltext/FullTextSearchTest.java) |
| `GeoQueryTest` | 地理查询测试 | [GeoQueryTest.java](src/test/java/com/shuai/elasticsearch/geo/GeoQueryTest.java) |
| `VectorEmbeddingUtilTest` | 向量嵌入测试 | [VectorEmbeddingUtilTest.java](src/test/java/com/shuai/elasticsearch/util/VectorEmbeddingUtilTest.java) |
| `RAGDemoTest` | RAG 系统测试 | [RAGDemoTest.java](src/test/java/com/shuai/elasticsearch/rag/RAGDemoTest.java) |
| `OpenAIClientTest` | OpenAI 客户端测试 | [OpenAIClientTest.java](src/test/java/com/shuai/elasticsearch/llm/OpenAIClientTest.java) |

## skills

```yaml
elasticsearch:
  # 核心概念
  core_concepts:
    - Index
    - Document
    - Mapping
    - Shard
    - Replica
    - Inverted Index

  # 文档操作
  document_operations:
    - _doc
    - _bulk
    - _update
    - _refresh
    - _flush
    - _delete_by_query

  # 查询类型
  queries:
    - match
    - match_phrase
    - multi_match
    - term
    - terms
    - range
    - bool
    - filter
    - must
    - should
    - must_not
    - nested
    - function_score
    - script_score
    - random_score
    - field_value_factor
    - constant_score
    - boosting
    - exists
    - prefix
    - wildcard
    - regexp

  # 聚合分析
  aggregations:
    - avg
    - sum
    - max
    - min
    - cardinality
    - stats
    - extended_stats
    - percentiles
    - terms
    - range
    - histogram
    - date_histogram
    - geotile_grid
    - geohash_grid
    - nested
    - reverse_nested
    - avg_bucket
    - sum_bucket
    - cumulative_sum

  # 全文检索
  full_text_search:
    - analyzer
    - ik_max_word
    - ik_smart
    - highlight
    - _score
    - sort
    - BM25
    - minimum_should_match
    - fuzziness

  # 地理查询
  geo_queries:
    - geo_point
    - geo_shape
    - geo_distance
    - geo_bounding_box
    - geo_polygon
    - geo_radius
    - geo_centroid

  # 数据类型
  mappings:
    - keyword
    - text
    - date
    - long
    - double
    - boolean
    - object
    - nested
    - ip
    - flattenned
    - alias

  # 异步操作
  async_operations:
    - ElasticsearchAsyncClient
    - CompletableFuture
    - parallel_requests
    - timeout_handling

  # 索引管理
  index_management:
    - create_index
    - get_mapping
    - get_settings
    - put_settings
    - aliases
    - rollover
    - shrink
    - clone

  # 向量检索
  vector_search:
    - Milvus
    - embedding
    - cosine_similarity
    - dot_product
    - Euclidean
    - IVF_FLAT
    - IVF_SQ8
    - HNSW
    - vector_index
    - two_stage_retrieval
    - siliconflow
    - bge_embedding
    - bge_reranker

  # Embedding 与 RAG
  embedding_rag:
    - BGE (bge-large-zh-v1.5)
    - BGE-reranker-v2-m3
    - SiliconFlow API
    - OpenAI embedding
    - text_vectorization
    - document_chunking
    - context_window
    - Retrieval-Augmented Generation
    - hybrid_search
    - RRF
    - cross_encoder
    - bi_encoder
    - recall
    - rank
    - cosine_similarity
    - vector_normalization

  # LLM 集成
  llm_integration:
    - OpenAI API
    - chat_completion
    - streaming_chat
    - async_chat
    - message_roles
    - token_usage

  # 监控告警
  monitoring:
    - Prometheus
    - metrics
    - alerting
    - Grafana
    - Kibana
    - JVM metrics
    - ES cluster health
    - Milvus monitoring

  # 性能优化
  optimization:
    - index refresh
    - merge segments
    - cache
    - circuit breaker
    - bulk_request
    - search_request_cache
    - request_timeout
```

## 依赖

| 依赖 | 版本 | 用途 |
|------|------|------|
| elasticsearch-java | 8.15.0 | ES Java 客户端 |
| spring-boot-starter-data-elasticsearch | 3.5.3 | Spring Data ES 集成 |
| milvus-sdk-java | 2.3.7 | Milvus 向量数据库客户端 |
| jackson-databind | 2.17.0 | JSON 序列化 |
| lombok | - | 简化代码 |
| junit-jupiter | 5.11.0 | 单元测试 |

---

## Docker Compose 一键启动

使用 Docker Compose 可以一键启动所有依赖服务：

```yaml
# deploy/docker-compose.yml
version: '3.8'

services:
  elasticsearch:
    image: docker.elastic.co/elasticsearch/elasticsearch:8.18.0
    container_name: es01
    environment:
      - discovery.type=single-node
      - xpack.security.enabled=false
      - "ES_JAVA_OPTS=-Xms512m -Xmx512m"
    ports:
      - "9200:9200"
      - "9300:9300"
    volumes:
      - es-data:/usr/share/elasticsearch/data
    networks:
      - es-network

  milvus:
    image: milvusdb/milvus:v2.5.10
    container_name: milvus
    command: ["milvus", "run", "standalone"]
    ports:
      - "19530:19530"
      - "9091:9091"
    volumes:
      - milvus-data:/var/lib/milvus
    networks:
      - es-network

  minio:
    image: minio/minio:latest
    container_name: minio
    command: server /data --console-address ":9001"
    ports:
      - "9000:9000"
      - "9001:9001"
    volumes:
      - minio-data:/data
    environment:
      MINIO_ROOT_USER: admin
      MINIO_ROOT_PASSWORD: admin123
    networks:
      - es-network

volumes:
  es-data:
  milvus-data:
  minio-data:

networks:
  es-network:
    driver: bridge
```

**启动命令**:
```bash
# 启动所有服务
docker-compose -f deploy/docker-compose.yml up -d

# 查看服务状态
docker-compose -f deploy/docker-compose.yml ps

# 查看日志
docker-compose -f deploy/docker-compose.yml logs -f

# 停止所有服务
docker-compose -f deploy/docker-compose.yml down
```

---

**作者**: Shuai
**创建时间**: 2026-01-15
**更新时间**: 2026-01-18
