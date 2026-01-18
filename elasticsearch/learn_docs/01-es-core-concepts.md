# ES 核心概念

> 本章深入讲解 Elasticsearch 的核心概念，包括索引、文档、映射、分片等，是理解 ES 工作原理的基础。

## 学习目标

完成本章学习后，你将能够：
- 理解索引、文档、映射的关系和作用
- 掌握 ES 的数据类型体系
- 理解分片和副本的工作机制
- 了解集群节点的角色分工

## 1. 索引 (Index)

### 1.1 索引的本质

索引是存储相关文档的**逻辑命名空间**，类似于关系型数据库中的"表"。

```bash
# 创建索引
PUT /blog
{
  "settings": {
    "number_of_shards": 3,
    "number_of_replicas": 1,
    "analysis": {
      "analyzer": {
        "ik_analyzer": {
          "type": "custom",
          "tokenizer": "ik_max_word"
        }
      }
    }
  },
  "mappings": {
    "properties": {
      "title": { "type": "text", "analyzer": "ik_analyzer" },
      "content": { "type": "text", "analyzer": "ik_analyzer" },
      "author": { "type": "keyword" },
      "views": { "type": "integer" },
      "published": { "type": "boolean" },
      "createTime": { "type": "date" }
    }
  }
}
```

### 1.2 索引设置 (Settings)

```json
{
  "settings": {
    "number_of_shards": 3,        // 主分片数量 (创建后不可修改)
    "number_of_replicas": 1,      // 副本数量 (可动态修改)
    "refresh_interval": "1s",     // 刷新间隔
    "max_result_window": 10000,   // 最大返回结果数
    "analysis": {                 // 分析器配置
      "analyzer": { },
      "tokenizer": { },
      "filter": { }
    }
  }
}
```

### 1.3 索引操作

```bash
# 查看索引信息
GET /blog

# 查看索引列表
GET /_cat/indices?v

# 删除索引
DELETE /blog

# 索引是否存在
HEAD /blog

# 打开/关闭索引
POST /blog/_close
POST /blog/_open
```

## 2. 文档 (Document)

### 2.1 文档结构

文档是 ES 中的**基本数据单元**，以 JSON 格式存储。

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
    "content": "这是一篇关于 Elasticsearch 的学习教程",
    "author": "张三",
    "views": 1000,
    "tags": ["搜索", "大数据", "分布式"],
    "category": {
      "id": 1,
      "name": "技术"
    },
    "published": true,
    "createTime": "2024-01-17T10:00:00",
    "updateTime": "2024-01-17T12:00:00"
  }
}
```

### 2.2 文档元数据

| 字段 | 说明 |
|------|------|
| `_index` | 文档所属索引 |
| `_id` | 文档唯一标识 |
| `_version` | 文档版本号 |
| `_seq_no` | 序列号 (乐观锁) |
| `_primary_term` | 主分片任期号 |

### 2.3 文档 ID 生成

```bash
# 1. 自动生成 ID
POST /blog/_doc
{
  "title": "自动生成 ID"
}

# 2. 指定 ID
PUT /blog/_doc/1
{
  "title": "指定 ID"
}

# 3. 使用 op_type 防止覆盖
PUT /blog/_doc/1?op_type=create
{
  "title": "仅创建"
}
```

## 3. 映射 (Mapping)

### 3.1 映射定义

映射定义索引中**文档的字段结构**和**数据类型**。

```json
{
  "mappings": {
    "properties": {
      "title": {
        "type": "text",
        "analyzer": "ik_max_word",
        "search_analyzer": "ik_smart",
        "fields": {
          "keyword": { "type": "keyword" },
          "pinyin": { "type": "text", "analyzer": "pinyin" }
        }
      },
      "content": { "type": "text" },
      "author": { "type": "keyword" },
      "views": { "type": "integer" },
      "price": { "type": "scaled_float", "scaling_factor": 100 },
      "isPublished": { "type": "boolean" },
      "createTime": { "type": "date", "format": "yyyy-MM-dd||epoch_millis" },
      "location": { "type": "geo_point" },
      "tags": { "type": "keyword" }
    }
  }
}
```

### 3.2 核心数据类型

#### 字符串类型

| 类型 | 说明 | 场景 |
|------|------|------|
| `text` | 全文检索 | 需分词的文本 |
| `keyword` | 精确匹配 | 不需分词的文本 |

#### 数值类型

| 类型 | 说明 | 范围 |
|------|------|------|
| `integer` | 32位整数 | ±2^31 |
| `long` | 64位整数 | ±2^63 |
| `float` | 单精度浮点 | IEEE 754 |
| `double` | 双精度浮点 | IEEE 754 |
| `scaled_float` | 缩放浮点 | 高精度小数 |

#### 日期类型

```json
{
  "createTime": {
    "type": "date",
    "format": "yyyy-MM-dd HH:mm:ss||yyyy-MM-dd||epoch_millis"
  }
}
```

#### 地理类型

```json
{
  "location": {
    "type": "geo_point"
  }
}
```

### 3.3 多字段映射 (Multi-Fields)

```json
{
  "properties": {
    "title": {
      "type": "text",
      "analyzer": "ik_max_word",
      "fields": {
        "keyword": { "type": "keyword" },      // 精确匹配
        "pinyin": { "type": "text", "analyzer": "pinyin" }  // 拼音搜索
      }
    }
  }
}
```

### 3.4 动态映射

ES 支持**自动识别字段类型**：

```json
// 动态映射规则
{
  "dynamic_templates": [
    {
      "strings_as_keywords": {
        "match_mapping_type": "string",
        "mapping": { "type": "keyword" }
      }
    },
    {
      "longs_as_long": {
        "match_mapping_type": "long",
        "mapping": { "type": "long" }
      }
    }
  ]
}
```

## 4. 分片 (Shard)

### 4.1 分片架构

```
┌─────────────────────────────────────────────────────────────────────┐
│                         索引 (Index)                                 │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│  ┌─────────┐   ┌─────────┐   ┌─────────┐                           │
│  │ Shard 0 │   │ Shard 1 │   │ Shard 2 │      主分片 (Primary)      │
│  │   ┌───┐ │   │   ┌───┐ │   │   ┌───┐ │                           │
│  │   │Doc│ │   │   │Doc│ │   │   │Doc│ │                           │
│  │   │ 0 │ │   │   │ 1 │ │   │   │ 2 │ │                           │
│  │   └───┘ │   │   └───┘ │   │   └───┘ │                           │
│  └────┬────┘   └────┬────┘   └────┬────┘                           │
│       │            │            │                                  │
│       ▼            ▼            ▼                                  │
│  ┌─────────┐   ┌─────────┐   ┌─────────┐                           │
│  │Replica 0│   │Replica 1│   │Replica 2│      副本分片 (Replica)    │
│  └─────────┘   └─────────┘   └─────────┘                           │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### 4.2 分片数量设置

```bash
# 创建索引时设置分片数
PUT /my_index
{
  "settings": {
    "number_of_shards": 5,    // 主分片数 (重要!)
    "number_of_replicas": 1   // 副本数
  }
}
```

**分片数原则**：
- 分片数**不可修改**（创建时确定）
- 每个分片大小建议 **10GB-50GB**
- 分片数不宜过多或过少
- 分片数 = 数据量 / (单分片大小建议)

### 4.3 副本机制

```json
{
  "settings": {
    "number_of_replicas": 2   // 每个主分片有2个副本
  }
}
```

**副本作用**：
- 数据冗余：防止数据丢失
- 高可用：节点故障时自动切换
- 读取扩展：分担查询压力

### 4.4 分片分配策略

```bash
# 查看分片分配情况
GET _cat/shards?v

# 手动移动分片
POST _cluster/reroute
{
  "commands": [{
    "move": {
      "index": "blog",
      "shard": 0,
      "from_node": "node1",
      "to_node": "node2"
    }
  }]
}
```

## 5. 集群与节点

### 5.1 节点角色

```
┌─────────────────────────────────────────────────────────────────────┐
│                         节点角色                                     │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│  ┌─────────────────┐                                                │
│  │   Master Node   │  主节点                                        │
│  │   ◉ 集群管理    │  - 管理集群状态                                 │
│  │   ◉ 分片分配    │  - 创建/删除索引                                 │
│  │   ◉ 元数据      │  - 分片分配决策                                  │
│  └─────────────────┘                                                │
│                                                                      │
│  ┌─────────────────┐                                                │
│  │   Data Node     │  数据节点                                      │
│  │   ◉ 数据存储    │  - 存储索引数据                                  │
│  │   ◉ CRUD 操作   │  - 执行 CRUD 和搜索                             │
│  │   ◉ 聚合计算    │  - 聚合计算                                      │
│  └─────────────────┘                                                │
│                                                                      │
│  ┌─────────────────┐                                                │
│  │   Ingest Node   │  预处理节点                                    │
│  │   ◉ 数据处理    │  - 文档预处理                                    │
│  │   ◉ 管道执行    │  - Ingestion Pipeline                          │
│  └─────────────────┘                                                │
│                                                                      │
│  ┌─────────────────┐                                                │
│  │   Coordinating  │  协调节点                                      │
│  │     Node        │  - 请求转发                                      │
│  │   ◉ 请求聚合    │  - 结果合并                                      │
│  │   ◉ 负载均衡    │  - 负载均衡                                      │
│  └─────────────────┘                                                │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### 5.2 节点配置

```yaml
# elasticsearch.yml
node.name: es-node-1
node.master: true      # 参与主节点选举
node.data: true        # 存储数据
node.ingest: false     # 不做预处理
node.voting_only: false

# 集群名称
cluster.name: my-cluster

# 节点发现
discovery.seed_hosts:
  - 192.168.1.1
  - 192.168.1.2
```

### 5.3 集群健康

```bash
# 查看集群健康
GET _cluster/health

# 响应示例
{
  "cluster_name": "elasticsearch",
  "status": "green",
  "timed_out": false,
  "number_of_nodes": 3,
  "data_nodes": 2,
  "active_primary_shards": 10,
  "active_shards": 20,
  "relocating_shards": 0,
  "initializing_shards": 0,
  "unassigned_shards": 0
}
```

| 状态 | 说明 |
|------|------|
| `green` | 所有分片正常 |
| `yellow` | 副本未分配 |
| `red` | 主分片故障 |

## 6. 倒排索引详解

### 6.1 倒排索引结构

```
文档内容:
Doc1: "Elasticsearch 是 搜索 引擎"
Doc2: "搜索 引擎 使用 倒排 索引"

倒排索引:
┌──────────┬──────────────────────────────────────────┐
│   词     │  文档ID:词频(位置)                        │
├──────────┼──────────────────────────────────────────┤
│搜索      │  Doc1:1(2), Doc2:1(0)                   │
│倒排      │  Doc2:1(2)                              │
│索引      │  Doc2:1(3)                              │
│引擎      │  Doc1:1(3), Doc2:1(2)                   │
│Elasticsearch │ Doc1:1(0)                          │
│是        │  Doc1:1(1)                              │
│使用      │  Doc2:1(1)                              │
└──────────┴──────────────────────────────────────────┘
```

### 6.2 相关性评分 (TF-IDF / BM25)

```java
// Elasticsearch 使用 BM25 算法计算相关性
// BM25 公式:
// score(D, Q) = Σ IDF(qi) * (f(qi, D) * (k1 + 1)) / (f(qi, D) + k1 * (1 - b + b * |D|/avgdl))

public class BM25Demo {
    public static void main(String[] args) {
        // BM25 特点:
        // 1. TF (词频): 非线性饱和
        // 2. IDF (逆文档频率): 对罕见词加权
        // 3. 文档长度归一化
    }
}
```

## 7. 实践部分

### 7.1 创建索引映射

```java
// IndexConfig.java
public class IndexConfig {

    public static final String BLOG_INDEX = "blog";
    public static final String PRODUCT_INDEX = "product";

    /**
     * 创建博客索引
     */
    public static void createBlogIndex(ElasticsearchClient client) throws Exception {
        // 检查索引是否存在
        boolean exists = client.indices().exists(e -> e.index(BLOG_INDEX)).value();

        if (!exists) {
            CreateIndexResponse response = client.indices().create(c -> c
                .index(BLOG_INDEX)
                .settings(s -> s
                    .numberOfShards(3)
                    .numberOfReplicas(1)
                    .refreshInterval(t -> t.time("1s"))
                )
                .mappings(m -> m
                    .properties("title", p -> p.text(t -> t
                        .analyzer("ik_max_word")
                        .searchAnalyzer("ik_smart")
                        .fields("keyword", f -> f.keyword(k -> k))
                    ))
                    .properties("content", p -> p.text(t -> t.analyzer("ik_max_word")))
                    .properties("author", p -> p.keyword(k -> k))
                    .properties("views", p -> p.integer(i -> i))
                    .properties("tags", p -> p.keyword(k -> k))
                    .properties("createTime", p -> p.date(d -> d.format("yyyy-MM-dd||epoch_millis")))
                )
            );
            System.out.println("索引创建: " + response.acknowledged());
        }
    }
}
```

### 7.2 CRUD 操作示例

```java
// DocumentOperationDemo.java
public class DocumentOperationDemo {

    private final ElasticsearchClient client;
    private static final String INDEX_NAME = "blog";

    public DocumentOperationDemo() {
        this.client = ElasticsearchConfig.getClient();
    }

    /**
     * 创建文档
     */
    public void createDocument() throws Exception {
        // 1. 自动生成 ID
        BlogDocument doc = new BlogDocument();
        doc.setTitle("Elasticsearch 教程");
        doc.setContent("学习 Elasticsearch 的基础教程");
        doc.setAuthor("张三");
        doc.setViews(100);
        doc.setTags(List.of("ES", "搜索"));

        IndexResponse response = client.index(i -> i
            .index(INDEX_NAME)
            .id(doc.getId())  // 或不指定，使用自动生成
            .document(doc)
        );

        System.out.println("创建结果: " + response.result());
    }

    /**
     * 获取文档
     */
    public void getDocument(String id) throws Exception {
        GetResponse<BlogDocument> response = client.get(g -> g
            .index(INDEX_NAME)
            .id(id)
        , BlogDocument.class);

        if (response.found()) {
            System.out.println("文档: " + response.source());
        } else {
            System.out.println("文档不存在");
        }
    }

    /**
     * 更新文档
     */
    public void updateDocument(String id) throws Exception {
        UpdateResponse<BlogDocument> response = client.update(u -> u
            .index(INDEX_NAME)
            .id(id)
            .doc(patch -> patch
                .set("views", 200)
                .set("updateTime", LocalDateTime.now())
            )
        , BlogDocument.class);

        System.out.println("更新版本: " + response.version());
    }

    /**
     * 删除文档
     */
    public void deleteDocument(String id) throws Exception {
        DeleteResponse response = client.delete(d -> d
            .index(INDEX_NAME)
            .id(id)
        );

        System.out.println("删除结果: " + response.result());
    }
}
```

### 7.3 查看索引信息

```java
/**
 * 查看索引信息
 */
public void getIndexInfo() throws Exception {
    // 查看索引设置
    GetIndexResponse response = client.indices().get(g -> g
        .index(INDEX_NAME)
    );

    System.out.println("索引名称: " + response.result().keySet());
    response.index().values().forEach(indexMetadata -> {
        System.out.println("分片数: " + indexMetadata.settings().numberOfShards());
        System.out.println("副本数: " + indexMetadata.settings().numberOfReplicas());
    });
}
```

## 8. 常见问题

| 问题 | 原因 | 解决方案 |
|------|------|----------|
| 索引字段类型错误 | 动态映射导致类型推断错误 | 使用显式映射 |
| 分片分配不均 | 热点数据 | 使用分片路由 |
| 集群状态 yellow | 副本未分配 | 增加节点或减少副本 |
| 搜索结果不完整 | 分页超过 max_result_window | 增加设置或使用 search_after |
| 内存溢出 | 分片过大 | 增加分片数或扩容 |

## 9. 扩展阅读

- [Index Modules](https://www.elastic.co/guide/en/elasticsearch/reference/current/index-modules.html)
- [Mapping](https://www.elastic.co/guide/en/elasticsearch/reference/current/mapping.html)
- [Shards and Replicas](https://www.elastic.co/guide/en/elasticsearch/reference/current/scalability.html)

**代码位置**:
- [IndexConfig.java](src/main/java/com/shuai/elasticsearch/config/IndexConfig.java) - 索引配置
- [BlogDocument.java](src/main/java/com/shuai/elasticsearch/model/BlogDocument.java) - 文档映射定义
- [DataInitUtil.java](src/main/java/com/shuai/elasticsearch/util/DataInitUtil.java) - 索引初始化

- [上一章: ES 概述](00-elasticsearch-overview.md) | [下一章: 文档操作](02-es-document-operations.md)

---

**学习进度**: ⬜⬜⬜⬜⬜⬜⬜⬜ 0/18 文档完成

**最后更新**: 2026-01-17
