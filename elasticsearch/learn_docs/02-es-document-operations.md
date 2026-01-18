# 文档操作

> 本章讲解 Elasticsearch 的 CRUD 操作、批量处理、版本控制和刷新策略，是数据操作的基础。

## 学习目标

完成本章学习后，你将能够：
- 掌握文档的增删改查操作
- 理解版本控制和乐观锁机制
- 熟练使用 Bulk API 进行批量操作
- 了解刷新策略和数据可见性

## 1. 基本 CRUD 操作

### 1.1 创建文档 (Create)

```bash
# 自动生成 ID
POST /blog/_doc
{
  "title": "Elasticsearch 教程",
  "content": "学习 Elasticsearch 的入门教程",
  "author": "张三",
  "views": 100
}

# 指定 ID
PUT /blog/_doc/1
{
  "title": "Elasticsearch 高级",
  "content": "深入理解 Elasticsearch 高级特性",
  "author": "李四",
  "views": 200
}

# 仅当文档不存在时创建 (op_type=create)
PUT /blog/_doc/2?op_type=create
{
  "title": "新文档",
  "content": "这是一篇新文档"
}
```

### 1.2 获取文档 (Read)

```bash
# 获取文档
GET /blog/_doc/1

# 响应
{
  "_index": "blog",
  "_id": "1",
  "_version": 1,
  "_seq_no": 0,
  "_primary_term": 1,
  "found": true,
  "_source": {
    "title": "Elasticsearch 高级",
    "content": "深入理解 Elasticsearch 高级特性"
  }
}

# 获取文档源数据 (不包含元数据)
GET /blog/_source/1

# 检查文档是否存在
HEAD /blog/_doc/1

# 多文档获取
GET /blog/_doc/_mget
{
  "ids": ["1", "2"]
}
```

### 1.3 更新文档 (Update)

```bash
# 全量更新 (PUT，覆盖)
PUT /blog/_doc/1
{
  "title": "更新后的标题",
  "content": "更新后的内容",
  "author": "王五",
  "views": 300
}

# 部分更新 (POST _update)
POST /blog/_update/1
{
  "doc": {
    "views": 400,
    "tags": ["更新", "ES"]
  }
}

# 使用脚本更新
POST /blog/_update/1
{
  "script": {
    "source": "ctx._source.views += params.count",
    "params": { "count": 50 }
  }
}

# 增加新字段
POST /blog/_update/1
{
  "doc": {
    "category": "技术"
  }
}
```

### 1.4 删除文档 (Delete)

```bash
# 删除文档
DELETE /blog/_doc/1

# 根据条件删除
POST /blog/_delete_by_query
{
  "query": {
    "term": {
      "status": "deleted"
    }
  }
}

# 删除索引
DELETE /blog
```

## 2. 批量操作 (Bulk API)

### 2.1 Bulk 格式

```bash
# Bulk 请求体格式
action_and_meta_data\n
optional_source\n
action_and_meta_data\n
optional_source\n
...

# 示例: 批量创建
POST /_bulk
{"index":{"_index":"blog","_id":"1"}}
{"title":"文档1","content":"内容1"}
{"index":{"_index":"blog","_id":"2"}}
{"title":"文档2","content":"内容2"}

# 混合操作
POST /_bulk
{"index":{"_index":"blog","_id":"3"}}
{"title":"文档3","content":"内容3"}
{"delete":{"_index":"blog","_id":"1"}}
{"update":{"_index":"blog","_id":"2"}}
{"doc":{"views":500}}
```

### 2.2 Bulk 操作类型

| 操作 | 格式 | 说明 |
|------|------|------|
| index | `{"index":{...}}` | 创建或替换 |
| create | `{"create":{...}}` | 仅创建，不存在则失败 |
| update | `{"update":{...}}` | 部分更新 |
| delete | `{"delete":{...}}` | 删除 |

### 2.3 Bulk 响应处理

```json
{
  "took": 5,
  "errors": false,
  "items": [
    {
      "index": {
        "_index": "blog",
        "_id": "1",
        "_version": 1,
        "result": "created",
        "status": 201
      }
    },
    {
      "delete": {
        "_index": "blog",
        "_id": "2",
        "_version": 2,
        "result": "deleted",
        "status": 200
      }
    }
  ]
}
```

## 3. 版本控制

### 3.1 内部版本控制

Elasticsearch 使用 **`_version`** 和 **`_seq_no`/`_primary_term`** 进行版本控制。

```bash
# 获取文档版本
GET /blog/_doc/1

# 响应
{
  "_version": 3,           // 版本号
  "_seq_no": 5,            // 序列号 (乐观锁)
  "_primary_term": 1,      // 主分片任期号
  "_source": { ... }
}
```

### 3.2 乐观锁控制

```java
// 使用 if_seq_no 和 if_primary_term 进行乐观锁控制
public void updateWithOptimisticLock(ElasticsearchClient client, String id, long seqNo, long primaryTerm)
        throws Exception {

    UpdateResponse<BlogDocument> response = client.update(u -> u
        .index(INDEX_NAME)
        .id(id)
        .ifSeqNo(seqNo)
        .ifPrimaryTerm(primaryTerm)
        .doc(doc -> doc.set("views", 200))
    , BlogDocument.class);

    System.out.println("更新成功，版本: " + response.version());
}
```

### 3.3 版本冲突处理

```java
/**
 * 解决版本冲突 (重试机制)
 */
public void updateWithRetry(String id, long expectedSeqNo, long expectedTerm) throws Exception {
    int maxRetries = 3;
    int retryCount = 0;

    while (retryCount < maxRetries) {
        try {
            // 尝试更新
            updateWithOptimisticLock(client, id, expectedSeqNo, expectedTerm);
            return;  // 成功，退出
        } catch (Exception e) {
            if (e.getMessage().contains("version_conflict_engine_exception")) {
                // 版本冲突，重新获取最新版本
                retryCount++;
                System.out.println("版本冲突，重试第 " + retryCount + " 次");

                // 获取最新版本信息
                var latest = client.get(g -> g.index(INDEX_NAME).id(id), BlogDocument.class);
                expectedSeqNo = latest.seqNo();
                expectedTerm = latest.primaryTerm();
            } else {
                throw e;
            }
        }
    }
}
```

## 4. 刷新策略

### 4.1 refresh 参数

```bash
# 1. 默认 refresh (1s)
PUT /blog/_doc/1
{"title":"文档1"}

# 2. 立即刷新 (refresh=true)
PUT /blog/_doc/2?refresh=true
{"title":"文档2"}

# 3. 等待刷新 (refresh=wait_for)
PUT /blog/_doc/3?refresh=wait_for
{"title":"文档3"}

# 4. 不刷新 (refresh=false)
PUT /blog/_doc/4?refresh=false
{"title":"文档4"}
```

### 4.2 refresh 策略对比

| 策略 | 可见性 | 性能 | 使用场景 |
|------|--------|------|----------|
| `true` | 立即 | 低 | 实时搜索 |
| `wait_for` | 等待刷新完成 | 中 | 需要确保可见 |
| `false` | 不立即 | 高 | 批量导入 |
| 默认 (1s) | 1秒内 | 平衡 | 一般场景 |

### 4.3 索引级别刷新设置

```bash
# 修改索引刷新间隔
PUT /blog/_settings
{
  "refresh_interval": "5s"
}

# 暂停刷新 (批量导入时)
PUT /blog/_settings
{
  "refresh_interval": "-1"
}

# 恢复刷新
PUT /blog/_settings
{
  "refresh_interval": "1s"
}
```

## 5. 路由机制

### 5.1 文档路由公式

```bash
# Elasticsearch 使用公式确定文档存储的分片
shard_num = hash(_routing) % (num_primary_shards)

# 默认使用 _id 作为路由值
# 也可以指定自定义路由字段
PUT /blog/_doc?routing=author1
{"title":"文档","author":"author1"}

# 查询时使用路由
GET /blog/_doc/1?routing=author1
```

### 5.2 自定义路由

```java
/**
 * 使用自定义路由
 */
public void indexWithRouting(ElasticsearchClient client, BlogDocument doc, String routingKey)
        throws Exception {

    String routing = routingKey != null ? routingKey : doc.getAuthor();

    IndexResponse response = client.index(i -> i
        .index(INDEX_NAME)
        .routing(routing)
        .id(doc.getId())
        .document(doc)
    );
}
```

## 6. 数据类型转换

### 6.1 动态映射规则

```
JSON 类型          →  Elasticsearch 类型
─────────────────────────────────────────
null              →  不添加字段
true/false        →  boolean
整数              →  long
小数              →  double
字符串 (日期)     →  date
字符串 (无需分词)  →  keyword
字符串 (需分词)   →  text
对象              →  object
数组              →  数组中第一个非空值的类型
```

### 6.2 强制类型转换

```bash
# 创建索引时指定类型
PUT /blog
{
  "mappings": {
    "properties": {
      "title": { "type": "text" },
      "views": { "type": "integer" },
      "publishDate": { "type": "date" }
    }
  }
}
```

## 7. 实践部分

### 7.1 完整 CRUD 示例

```java
// DocumentOperationDemo.java
package com.shuai.elasticsearch.document;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.bulk.BulkResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.shuai.elasticsearch.config.ElasticsearchConfig;
import com.shuai.elasticsearch.model.BlogDocument;
import com.shuai.elasticsearch.util.ResponsePrinter;

import java.io.IOException;
import java.util.List;

public class DocumentOperationDemo {

    private final ElasticsearchClient client;
    private static final String INDEX_NAME = "blog";

    public DocumentOperationDemo() {
        this.client = ElasticsearchConfig.getClient();
    }

    /**
     * 运行所有文档操作演示
     */
    public void runAllDemos() throws IOException {
        ResponsePrinter.printMethodInfo("DocumentOperationDemo", "文档操作演示");

        createDocument();
        getDocument();
        updateDocument();
        bulkOperation();
        deleteDocument();
    }

    /**
     * 创建文档
     */
    public void createDocument() throws IOException {
        ResponsePrinter.printMethodInfo("createDocument", "创建文档");

        BlogDocument doc = new BlogDocument();
        doc.setId("1");
        doc.setTitle("Elasticsearch 入门教程");
        doc.setContent("这是一篇关于 Elasticsearch 基础知识的教程");
        doc.setAuthor("张三");
        doc.setViews(100);
        doc.setTags(List.of("ES", "搜索", "入门"));

        IndexResponse response = client.index(i -> i
            .index(INDEX_NAME)
            .id(doc.getId())
            .document(doc)
        );

        System.out.println("  结果: " + response.result());
        System.out.println("  版本: " + response.version());
    }

    /**
     * 获取文档
     */
    public void getDocument() throws IOException {
        ResponsePrinter.printMethodInfo("getDocument", "获取文档");

        GetResponse<BlogDocument> response = client.get(g -> g
            .index(INDEX_NAME)
            .id("1")
        , BlogDocument.class);

        if (response.found()) {
            BlogDocument doc = response.source();
            System.out.println("  标题: " + doc.getTitle());
            System.out.println("  作者: " + doc.getAuthor());
            System.out.println("  浏览: " + doc.getViews());
        } else {
            System.out.println("  文档不存在");
        }
    }

    /**
     * 更新文档
     */
    public void updateDocument() throws IOException {
        ResponsePrinter.printMethodInfo("updateDocument", "更新文档");

        // 部分更新
        UpdateResponse<BlogDocument> response = client.update(u -> u
            .index(INDEX_NAME)
            .id("1")
            .doc(doc -> doc
                .set("views", 200)
                .set("updateTime", java.time.LocalDateTime.now().toString())
            )
        , BlogDocument.class);

        System.out.println("  结果: " + response.result());
        System.out.println("  版本: " + response.version());
    }

    /**
     * 批量操作
     */
    public void bulkOperation() throws IOException {
        ResponsePrinter.printMethodInfo("bulkOperation", "批量操作");

        // 准备批量数据
        String bulkBody = """
            {"index":{"_index":"blog","_id":"2"}}
            {"title":"批量文档1","content":"内容1","author":"李四","views":50}
            {"index":{"_index":"blog","_id":"3"}}
            {"title":"批量文档2","content":"内容2","author":"王五","views":60}
            {"index":{"_index":"blog","_id":"4"}}
            {"title":"批量文档3","content":"内容3","author":"赵六","views":70}
            """;

        BulkResponse response = client.bulk(b -> b
            .withPlainString(bulkBody)
        );

        System.out.println("  耗时: " + response.took());
        System.out.println("  错误: " + response.errors());

        for (var item : response.items()) {
            if (item.error() != null) {
                System.out.println("  失败: " + item.error().reason());
            }
        }
    }

    /**
     * 删除文档
     */
    public void deleteDocument() throws IOException {
        ResponsePrinter.printMethodInfo("deleteDocument", "删除文档");

        DeleteResponse response = client.delete(d -> d
            .index(INDEX_NAME)
            .id("4")
        );

        System.out.println("  结果: " + response.result());
    }

    /**
     * 批量查询
     */
    public void mgetOperation() throws IOException {
        ResponsePrinter.printMethodInfo("mgetOperation", "批量查询");

        MgetResponse<BlogDocument> response = client.mget(m -> m
            .index(INDEX_NAME)
            .ids("1", "2", "3")
        , BlogDocument.class);

        for (Hit<BlogDocument> hit : response.docs()) {
            if (hit.source() != null) {
                System.out.println("  " + hit.source().getTitle());
            }
        }
    }
}
```

### 7.2 数据初始化工具

```java
// DataInitUtil.java
public class DataInitUtil {

    /**
     * 初始化博客索引测试数据
     */
    public static void initBlogData(ElasticsearchClient client, String indexName) throws Exception {
        // 检查索引是否存在，不存在则创建
        boolean exists = client.indices().exists(e -> e.index(indexName)).value();
        if (!exists) {
            createBlogIndex(client, indexName);
        }

        // 批量插入测试数据
        StringBuilder bulkBody = new StringBuilder();
        String[][] testData = {
            {"1", "Elasticsearch 入门教程", "从零开始学习 Elasticsearch", "张三", "100"},
            {"2", "Elasticsearch 高级特性", "深入理解 Elasticsearch 高级功能", "李四", "200"},
            {"3", "Elasticsearch 集群部署", "如何在生产环境部署 ES 集群", "王五", "150"},
            {"4", "Elasticsearch 性能优化", "优化 ES 查询性能和索引策略", "赵六", "300"},
            {"5", "Elasticsearch 与 Spring Boot", "Spring Boot 集成 ES 实战", "张三", "250"}
        };

        for (String[] row : testData) {
            bulkBody.append(String.format("""
                {"index":{"_index":"%s","_id":"%s"}}
                {"id":"%s","title":"%s","content":"%s","author":"%s","views":%s,"status":"published"}
                """, indexName, row[0], row[0], row[1], row[2], row[3], row[4]));
        }

        client.bulk(b -> b.withPlainString(bulkBody.toString()));

        // 刷新索引使数据可见
        client.indices().refresh(r -> r.index(indexName));
    }

    private static void createBlogIndex(ElasticsearchClient client, String indexName) throws Exception {
        client.indices().create(c -> c
            .index(indexName)
            .settings(s -> s
                .numberOfShards(1)
                .numberOfReplicas(0)
            )
            .mappings(m -> m
                .properties("id", p -> p.keyword(k -> k))
                .properties("title", p -> p.text(t -> t.analyzer("ik_max_word")))
                .properties("content", p -> p.text(t -> t.analyzer("ik_max_word")))
                .properties("author", p -> p.keyword(k -> k))
                .properties("views", p -> p.integer(i -> i))
                .properties("status", p -> p.keyword(k -> k))
            )
        );
    }
}
```

## 8. 常见问题

| 问题 | 原因 | 解决方案 |
|------|------|----------|
| 文档创建失败 | 索引不存在或映射冲突 | 先创建索引 |
| 版本冲突异常 | 并发更新同一文档 | 使用 if_seq_no 乐观锁 |
| Bulk 部分失败 | 单个文档格式错误 | 检查 errors 响应 |
| 数据不立即可见 | refresh 策略 | 使用 refresh=true |
| ID 重复 | 使用相同 ID 覆盖 | 使用 op_type=create |

## 9. 扩展阅读

- [Document APIs](https://www.elastic.co/guide/en/elasticsearch/reference/current/docs.html)
- [Bulk API](https://www.elastic.co/guide/en/elasticsearch/reference/current/docs-bulk.html)
- [Optimistic Concurrency Control](https://www.elastic.co/guide/en/elasticsearch/reference/current/optimum-elasticsearch.html)

**代码位置**:
- [DocumentOperationDemo.java](src/main/java/com/shuai/elasticsearch/document/DocumentOperationDemo.java) - CRUD 操作演示
- [BlogDataGenerator.java](src/main/java/com/shuai/elasticsearch/data/BlogDataGenerator.java) - 测试数据生成器（13篇高质量文档）
- [DocumentOperationDemoTest.java](src/test/java/com/shuai/elasticsearch/document/DocumentOperationDemoTest.java) - CRUD 测试

- [上一章: 核心概念](01-es-core-concepts.md) | [下一章: 查询类型](03-es-query-types.md)

---

**学习进度**: ⬜⬜⬜⬜⬜⬜⬜⬜ 0/18 文档完成

**最后更新**: 2026-01-17
