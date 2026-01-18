# Elasticsearch 概述

> 本章介绍 Elasticsearch 的核心概念、特性以及应用场景，帮助你建立对搜索引擎技术的整体认知。

## 学习目标

完成本章学习后，你将能够：
- 理解 Elasticsearch 的定位和核心特性
- 了解 Elasticsearch 与传统数据库的区别
- 掌握 Elasticsearch 的典型应用场景
- 熟悉 ELK Stack 生态系统的组成

## 1. Elasticsearch 简介

### 1.1 什么是 Elasticsearch

Elasticsearch 是一个**分布式、RESTful 风格**的搜索和数据分析引擎，基于 Apache Lucene 构建。它能够快速、近实时地存储、搜索和分析海量数据。

**核心特性**：
- **分布式架构**：支持水平扩展，自动分片和副本
- **高性能搜索**：毫秒级响应，支持 PB 级数据
- **全文检索**：强大的倒排索引，支持复杂查询
- **实时分析**：聚合计算，实时数据统计
- **高可用性**：自动故障转移，数据冗余

### 1.2 版本演进

| 版本 | 发布时间 | 重要特性 |
|------|----------|----------|
| 0.4 | 2010 | 首个公开版本 |
| 1.0 | 2014 | 聚合分析功能 |
| 2.0 | 2015 | 性能优化 |
| 5.0 | 2016 | Ingest 节点、SQL 查询 |
| 6.0 | 2017 | 跨集群复制、X-Pack |
| 7.0 | 2019 | 集群协调简化、向量搜索 |
| 8.0 | 2022 | 安全增强、KNN、ES|QL |

**本项目使用版本**：Elasticsearch 8.18.0

### 1.3 与传统数据库对比

```
┌─────────────────────────────────────────────────────────────────────┐
│                    Elasticsearch vs 传统数据库                        │
├─────────────────────┬─────────────────────┬─────────────────────────┤
│       特性          │    Elasticsearch    │    传统数据库 (MySQL)    │
├─────────────────────┼─────────────────────┼─────────────────────────┤
│ 数据存储方式        │    倒排索引          │    B+Tree 索引          │
│ 查询语言            │    Query DSL        │    SQL                  │
│ 搜索能力            │    全文检索强大      │    精确匹配为主          │
│ 复杂查询            │    支持嵌套、聚合    │    需手动优化            │
│ 实时性              │    近实时 (秒级)     │    实时                  │
│ 分布式支持          │    原生支持          │    需要中间件            │
│ 数据分析            │    聚合分析强大      │    需要额外工具          │
└─────────────────────┴─────────────────────┴─────────────────────────┘
```

## 2. 核心概念

### 2.1 文档 (Document)

文档是 Elasticsearch 中的**基本数据单元**，以 JSON 格式存储。

```json
{
  "title": "Elasticsearch 教程",
  "content": "这是一篇关于 Elasticsearch 的学习教程",
  "author": "张三",
  "views": 1000,
  "tags": ["搜索", "大数据"],
  "createTime": "2024-01-17"
}
```

**文档特点**：
- 自描述：每个文档包含数据和元数据
- 模式灵活：同一索引中文档结构可以不同
- 版本控制：每次更新会生成新版本

### 2.2 索引 (Index)

索引是**存储文档的逻辑容器**，类似于关系型数据库的表。

```bash
# 创建索引
PUT /blog
{
  "settings": {
    "number_of_shards": 3,
    "number_of_replicas": 1
  },
  "mappings": {
    "properties": {
      "title": { "type": "text" },
      "content": { "type": "text" }
    }
  }
}
```

**索引命名规则**：
- 必须小写
- 不能包含 `\`, `/`, `*`, `?`, `"`, `<`, `>`, `|`
- 长度不超过 255 字节

### 2.3 映射 (Mapping)

映射定义索引中**文档的字段结构和类型**。

```json
{
  "mappings": {
    "properties": {
      "title": {
        "type": "text",
        "analyzer": "ik_max_word",
        "fields": {
          "keyword": { "type": "keyword" }
        }
      },
      "views": { "type": "integer" },
      "published": { "type": "boolean" },
      "createTime": { "type": "date" }
    }
  }
}
```

### 2.4 分片 (Shard)

分片是索引的**物理数据分区**，实现分布式存储。

```
┌─────────────────────────────────────────────────────────────┐
│                        索引 (Index)                          │
├───────────────────┬───────────────────┬─────────────────────┤
│   分片 0          │   分片 1          │   分片 2            │
│   (Primary)       │   (Primary)       │   (Primary)         │
├───────────────────┼───────────────────┼─────────────────────┤
│   分片 0 副本     │   分片 1 副本     │   分片 2 副本       │
│   (Replica)       │   (Replica)       │   (Replica)         │
└───────────────────┴───────────────────┴─────────────────────┘
```

**分片策略**：
- 主分片数：创建索引时确定，不可修改
- 副本数：可动态调整
- 每个分片是一个独立的 Lucene 索引

### 2.5 集群 (Cluster)

集群由多个节点组成，提供**分布式存储和搜索能力**。

```
┌─────────────────────────────────────────────────────────────┐
│                      ES 集群 (Cluster)                       │
├──────────────┬──────────────┬──────────────┬───────────────┤
│   节点 1     │   节点 2     │   节点 3     │   节点 4      │
│  (Master)    │  (Data)      │  (Data)      │  (Data)       │
│              │              │              │               │
│  ◉ 协调节点   │  ◉ 数据存储  │  ◉ 数据存储  │  ◉ 数据存储   │
│  ◉ 主节点    │  ◉ 查询执行  │  ◉ 查询执行  │  ◉ 查询执行   │
└──────────────┴──────────────┴──────────────┴───────────────┘
```

## 3. 倒排索引原理

### 3.1 什么是倒排索引

倒排索引是 Elasticsearch 实现**快速全文检索**的核心数据结构。

```
文档集合:
┌─────────────────────────────────────────────────────────────┐
│ Doc1: "Elasticsearch 是 搜索 引擎"                          │
│ Doc2: "搜索 引擎 使用 倒排 索引"                             │
│ Doc3: "倒排 索引 是 核心 组件"                               │
└─────────────────────────────────────────────────────────────┘

倒排索引:
┌──────────┬─────────────────────────────────────────────────┐
│   词汇   │                   文档列表                       │
├──────────┼─────────────────────────────────────────────────┤
│ 搜索     │ [Doc1:1, Doc2:1, Doc3:0]                       │
│ 倒排     │ [Doc2:1, Doc3:1]                               │
│ 索引     │ [Doc2:2, Doc3:2]                               │
│ 核心     │ [Doc3:3]                                       │
│ 组件     │ [Doc3:4]                                       │
│ Elasticsearch │ [Doc1:0]                                │
│ 是       │ [Doc1:1, Doc3:1]                               │
│ 引擎     │ [Doc1:4, Doc2:3]                               │
│ 使用     │ [Doc2:2]                                       │
└──────────┴─────────────────────────────────────────────────┘
```

### 3.2 分词器工作流程

```
┌─────────────────────────────────────────────────────────────────┐
│                    分词器 (Analyzer) 工作流程                    │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│   输入: "Elasticsearch 是强大的搜索引擎"                        │
│         │                                                       │
│         ▼                                                       │
│   ┌───────────┐                                                 │
│   │ Character │  字符过滤: 转小写、去除标点                       │
│   │  Filters  │  "elasticsearch 是 强大 的 搜索 引擎"            │
│   └───────────┘                                                 │
│         │                                                       │
│         ▼                                                       │
│   ┌───────────┐                                                 │
│   │ Tokenizer │  分词: 按规则切分为词元                          │
│   │           │  ["elasticsearch", "是", "强大", "的", "搜索", "引擎"] │
│   └───────────┘                                                 │
│         │                                                       │
│         ▼                                                       │
│   ┌───────────┐                                                 │
│   │   Token   │  词元过滤: 同义词、词形还原、停用词                │
│   │  Filters  │  ["elasticsearch", "强大", "搜索", "引擎"]        │
│   └───────────┘                                                 │
│         │                                                       │
│         ▼                                                       │
│   输出: 词元列表                                                │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

## 4. ELK Stack 生态系统

### 4.1 组件介绍

```
┌─────────────────────────────────────────────────────────────────────┐
│                         ELK Stack                                    │
├──────────┬──────────┬──────────┬──────────┬─────────────────────────┤
│  Beats    │  Logstash │ Elasticsearch │   Kibana   │   APM/CI      │
│          │           │              │            │               │
│  ◉ 数据   │  ◉ ETL   │  ◉ 存储搜索 │  ◉ 可视化 │  ◉ 性能监控   │
│    采集   │    处理   │  ◉ 数据分析 │  ◉ 仪表盘  │  ◉ 告警       │
└──────────┴──────────┴──────────┴──────────┴─────────────────────────┘
```

| 组件 | 用途 | 特点 |
|------|------|------|
| Beats | 数据采集 | 轻量级、低资源占用 |
| Logstash | 数据处理 | 强大的 ETL 能力 |
| Elasticsearch | 存储分析 | 核心搜索分析引擎 |
| Kibana | 可视化 | 丰富的图表和仪表盘 |

### 4.2 数据流转

```
┌─────────────────────────────────────────────────────────────────────────┐
│                         数据流转图                                       │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│  ┌──────────┐    ┌──────────┐    ┌───────────┐    ┌──────────┐        │
│  │  数据源   │───▶│  Beats   │───▶│ Logstash  │───▶│    ES    │        │
│  │          │    │          │    │           │    │          │        │
│  │ ◉ 日志   │    │ ◉ Filebeat│    │ ◉ 解析   │    │ ◉ 存储   │        │
│  │ ◉ 指标   │    │ ◉ Metricbeat│   │ ◉ 转换   │    │ ◉ 索引   │        │
│  │ ◉ APM   │    │ ◉ Heartbeat│   │ ◉ 过滤   │    │ ◉ 搜索   │        │
│  └──────────┘    └──────────┘    └───────────┘    └────┬─────┘        │
│                                                        │               │
│                                                        ▼               │
│                                               ┌──────────────┐         │
│                                               │   Kibana     │         │
│                                               │              │         │
│                                               │ ◉ 可视化    │         │
│                                               │ ◉ 分析      │         │
│                                               │ ◉ 告警      │         │
│                                               └──────────────┘         │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
```

## 5. 应用场景

### 5.1 典型应用场景

| 场景 | 说明 | 示例 |
|------|------|------|
| **全文搜索** | 网站、文档搜索 | 电商商品搜索 |
| **日志分析** | ELK 日志分析 | 应用日志、访问日志 |
| **指标监控** | 时序数据监控 | 服务器指标、业务指标 |
| **应用搜索** | 内搜、文档检索 | 知识库搜索 |
| **地理查询** | 位置服务 | 附近的人、门店查询 |
| **推荐系统** | 向量检索 | 相似内容推荐 |

### 5.2 搜索场景示例

```
┌─────────────────────────────────────────────────────────────────────┐
│                    电商搜索场景                                        │
├─────────────────────────────────────────────────────────────────────┤
│                                                                     │
│  用户输入: "2024 新款 iPhone 15 Pro Max"                           │
│                                                                     │
│  Elasticsearch 处理:                                                │
│    1. 分词: ["2024", "新款", "iphone", "15", "pro", "max"]         │
│    2. 查询: match 查询 + 过滤条件                                    │
│    3. 排序: 相关性评分 + 价格 + 销量                                  │
│    4. 高亮: 匹配关键词高亮显示                                        │
│                                                                     │
│  返回结果:                                                          │
│    ┌─────────────────────────────────────────────────────────┐     │
│    │ [iPhone 15 Pro Max 256GB]                              │     │
│    │ <em>2024</em>新款 <em>iPhone</em> 官方正品            │     │
│    │ ¥9,999 | 销量: 10万+ | 评分: 4.9                        │     │
│    └─────────────────────────────────────────────────────────┘     │
│                                                                     │
└─────────────────────────────────────────────────────────────────────┘
```

## 6. REST API 基础

### 6.1 API 格式

Elasticsearch 提供完整的 RESTful API：

```bash
# 基础格式
curl -X METHOD "HOST:PORT/INDEX/_ACTION?QUERY_PARAMS" -d 'BODY'

# 示例
curl -X GET "localhost:9200/blog/_search?pretty" \
  -H 'Content-Type: application/json' \
  -d '{"query": {"match": {"title": "Elasticsearch"}}}'
```

### 6.2 常用端点

| 方法 | 端点 | 说明 |
|------|------|------|
| GET | `/_cluster/health` | 集群健康状态 |
| GET | `/<index>/_search` | 搜索文档 |
| PUT | `/<index>` | 创建索引 |
| POST | `/<index>/_doc` | 创建文档 |
| GET | `/<index>/_doc/<id>` | 获取文档 |
| PUT | `/<index>/_doc/<id>` | 更新文档 |
| DELETE | `/<index>/_doc/<id>` | 删除文档 |
| POST | `/<index>/_update/<id>` | 部分更新 |

### 6.3 响应格式

```json
{
  "_index": "blog",
  "_id": "1",
  "_version": 1,
  "_seq_no": 0,
  "_primary_term": 1,
  "found": true,
  "_source": {
    "title": "Elasticsearch 教程",
    "content": "内容..."
  }
}
```

## 7. 实践部分

### 7.1 验证 Elasticsearch 服务

```bash
# 1. 检查集群健康状态
curl -X GET "localhost:9200/_cluster/health?pretty"

# 响应示例:
# {
#   "cluster_name" : "elasticsearch",
#   "status" : "green",
#   "timed_out" : false,
#   "number_of_nodes" : 1,
#   "number_of_data_nodes" : 1,
#   "active_primary_shards" : 5,
#   "active_shards" : 5,
#   "relocating_shards" : 0,
#   "initializing_shards" : 0,
#   "unassigned_shards" : 0,
#   "delayed_unassigned_shards" : 0,
#   "number_of_pending_tasks" : 0,
#   "number_of_in_flight_fetch" : 0
# }

# 2. 查看节点信息
curl -X GET "localhost:9200/_nodes?pretty"

# 3. 查看所有索引
curl -X GET "localhost:9200/_cat/indices?v"
```

### 7.2 Java 客户端连接

```java
// ElasticsearchConfig.java
package com.shuai.elasticsearch.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;

public class ElasticsearchConfig {

    private static final String HOST = "localhost";
    private static final int PORT = 9200;

    private static ElasticsearchClient client;

    public static synchronized ElasticsearchClient getClient() {
        if (client == null) {
            RestClient restClient = RestClient.builder(
                new HttpHost(HOST, PORT, "http")
            ).build();

            ElasticsearchTransport transport = new RestClientTransport(
                restClient, new JacksonJsonpMapper()
            );

            client = new ElasticsearchClient(transport);
        }
        return client;
    }
}
```

### 7.3 快速测试代码

```java
// ElasticsearchOverviewDemo.java
package com.shuai.elasticsearch.overview;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.cluster.HealthResponse;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.shuai.elasticsearch.config.ElasticsearchConfig;
import com.shuai.elasticsearch.model.BlogDocument;

public class ElasticsearchOverviewDemo {

    public static void main(String[] args) throws Exception {
        ElasticsearchClient client = ElasticsearchConfig.getClient();

        // 1. 检查集群健康状态
        System.out.println("=== 1. 集群健康状态 ===");
        HealthResponse health = client.cluster().health();
        System.out.println("集群名称: " + health.clusterName());
        System.out.println("状态: " + health.status());
        System.out.println("节点数: " + health.numberOfNodes());

        // 2. 查看节点信息
        System.out.println("\n=== 2. 节点信息 ===");
        var nodesResponse = client.cluster().info(c -> c);
        System.out.println("集群节点数: " + nodesResponse.nodes().size());

        // 3. 列出所有索引
        System.out.println("\n=== 3. 索引列表 ===");
        var indicesResponse = client.indices().i -> i.index("_all"));
        indicesResponse.index().keySet().forEach(name ->
            System.out.println("索引: " + name)
        );
    }
}
```

## 8. 常见问题

| 问题 | 解决方案 |
|------|----------|
| 集群状态为 yellow | 副本分片未分配，增加节点或减少副本数 |
| 无法连接 ES | 检查 ES 服务是否启动，端口是否正确 |
| 查询超时 | 增加 timeout 参数，优化查询语句 |
| 内存不足 | 调整 ES_JAVA_OPTS，增加堆内存 |
| 分片不均衡 | 使用集群重分配策略 |

## 9. 扩展阅读

- [Elasticsearch 官方文档](https://www.elastic.co/guide/en/elasticsearch/reference/current/index.html)
- [Elasticsearch Java Client](https://www.elastic.co/guide/en/elasticsearch/client/java-api-client/current/index.html)
- [ELK Stack 官方](https://www.elastic.co/cn/what-is/elk-stack)

**代码位置**:
- [ElasticsearchOverviewDemo.java](src/main/java/com/shuai/elasticsearch/overview/ElasticsearchOverviewDemo.java) - ES 概述演示
- [ElasticsearchConfig.java](src/main/java/com/shuai/elasticsearch/config/ElasticsearchConfig.java) - 客户端配置
- [BlogDocument.java](src/main/java/com/shuai/elasticsearch/model/BlogDocument.java) - 文档模型

- [下一章: ES 核心概念](01-es-core-concepts.md)

---

**学习进度**: ⬜⬜⬜⬜⬜⬜⬜⬜ 0/18 文档完成

**最后更新**: 2026-01-17
