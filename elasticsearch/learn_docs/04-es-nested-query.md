# 嵌套查询

> 本章深入讲解 Elasticsearch 的嵌套数据类型和嵌套查询，解决复杂数据结构的检索问题。

## 学习目标

完成本章学习后，你将能够：
- 理解 nested 数据类型与 object 类型的区别
- 掌握 nested 查询的语法和使用场景
- 实现复杂的嵌套数据检索
- 优化嵌套查询性能

## 1. nested 数据类型

### 1.1 什么是 nested 类型

nested 类型是一种特殊的**对象数组**索引方式，使得数组中的每个对象都可以独立索引和查询。

```json
{
  "mappings": {
    "properties": {
      "title": { "type": "text" },
      "comments": {
        "type": "nested",
        "properties": {
          "user": { "type": "keyword" },
          "content": { "type": "text" },
          "date": { "type": "date" },
          "rating": { "type": "integer" }
        }
      }
    }
  }
}
```

### 1.2 nested vs object

```
┌─────────────────────────────────────────────────────────────────────┐
│                    nested vs object 对比                             │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│  Object 类型 (默认)                                                  │
│  ┌─────────────────────────────────────────────────────────────┐    │
│  │  文档: {                                                      │    │
│  │    "title": "文章标题",                                       │    │
│  │    "comments": [                                             │    │
│  │      { "user": "张三", "content": "评论1", "rating": 5 },     │    │
│  │      { "user": "李四", "content": "评论2", "rating": 4 }      │    │
│  │    ]                                                         │    │
│  │  }                                                           │    │
│  │                                                              │    │
│  │  内部存储 (扁平化):                                           │    │
│  │  ┌──────────────────────────────────────────────────────┐    │    │
│  │  │ comments.user:     ["张三", "李四"]                   │    │    │
│  │  │ comments.content:  ["评论1", "评论2"]                 │    │    │
│  │  │ comments.rating:   [5, 4]                             │    │    │
│  │  └──────────────────────────────────────────────────────┘    │    │
│  │                                                              │    │
│  │  ⚠️ 问题: 无法精确查询 "张三的5星评论"                       │    │
│  │     因为 user 和 rating 被分开存储                          │    │
│  └─────────────────────────────────────────────────────────────┘    │
│                                                                      │
│  Nested 类型                                                         │
│  ┌─────────────────────────────────────────────────────────────┐    │
│  │  文档: {                                                      │    │
│  │    "title": "文章标题",                                       │    │
│  │    "comments": [                                             │    │
│  │      { "user": "张三", "content": "评论1", "rating": 5 },     │    │
│  │      { "user": "李四", "content": "评论2", "rating": 4 }      │    │
│  │    ]                                                         │    │
│  │  }                                                           │    │
│  │                                                              │    │
│  │  内部存储 (独立索引):                                         │    │
│  │  ┌──────────────────────────────────────────────────────┐    │    │
│  │  │ Doc 1: {user: "张三", content: "评论1", rating: 5}   │    │    │
│  │  │ Doc 2: {user: "李四", content: "评论2", rating: 4}   │    │    │
│  │  └──────────────────────────────────────────────────────┘    │    │
│  │                                                              │    │
│  │  ✓ 可以精确查询 "张三的5星评论"                               │    │
│  │     因为每个评论对象独立存储                                  │    │
│  └─────────────────────────────────────────────────────────────┘    │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### 1.3 使用场景

| 场景 | 示例 | 是否用 nested |
|------|------|---------------|
| 评论/回复 | 文章下的多条评论 | ✅ |
| 产品属性 | 规格参数列表 | ✅ |
| 订单明细 | 订单中的多个商品 | ✅ |
| 简历经历 | 工作经历列表 | ✅ |
| 标签列表 | 文章标签 (keyword) | ❌ |

## 2. nested 查询

### 2.1 基本 nested 查询

```bash
# 嵌套查询语法
GET /blog/_search
{
  "query": {
    "nested": {
      "path": "comments",
      "query": {
        "bool": {
          "must": [
            { "term": { "comments.user": "张三" }},
            { "range": { "comments.rating": { "gte": 4 }}}
          ]
        }
      }
    }
  }
}
```

### 2.2 nested 查询参数

```bash
# 完整参数
GET /blog/_search
{
  "query": {
    "nested": {
      "path": "comments",
      "score_mode": "avg",        # 评分模式
      "ignore_unmapped": false,   # 忽略未映射字段
      "query": { ... }
    }
  }
}
```

**score_mode 选项**：

| 模式 | 说明 |
|------|------|
| `avg` | 平均值 (默认) |
| `sum` | 总和 |
| `min` | 最小值 |
| `max` | 最大值 |
| `none` | 不计算评分 |

## 3. 实践部分

### 3.1 创建 nested 索引

```bash
# 创建产品索引 (包含规格参数)
PUT /product
{
  "mappings": {
    "properties": {
      "name": { "type": "text" },
      "category": { "type": "keyword" },
      "specs": {
        "type": "nested",
        "properties": {
          "name": { "type": "keyword" },
          "value": { "type": "keyword" },
          "numeric_value": { "type": "float" }
        }
      },
      "reviews": {
        "type": "nested",
        "properties": {
          "user": { "type": "keyword" },
          "rating": { "type": "integer" },
          "comment": { "type": "text" },
          "date": { "type": "date" }
        }
      }
    }
  }
}
```

### 3.2 插入 nested 数据

```bash
# 插入产品数据
PUT /product/_doc/1
{
  "name": "iPhone 15 Pro",
  "category": "手机",
  "specs": [
    { "name": "内存", "value": "256GB", "numeric_value": 256 },
    { "name": "屏幕", "value": "6.1英寸", "numeric_value": 6.1 },
    { "name": "处理器", "value": "A17 Pro" }
  ],
  "reviews": [
    { "user": "张三", "rating": 5, "comment": "非常好用", "date": "2024-01-01" },
    { "user": "李四", "rating": 4, "comment": "还不错", "date": "2024-01-05" }
  ]
}

PUT /product/_doc/2
{
  "name": "Samsung Galaxy S24",
  "category": "手机",
  "specs": [
    { "name": "内存", "value": "128GB", "numeric_value": 128 },
    { "name": "屏幕", "value": "6.2英寸", "numeric_value": 6.2 }
  ],
  "reviews": [
    { "user": "王五", "rating": 5, "comment": "屏幕很棒", "date": "2024-01-02" }
  ]
}
```

### 3.3 nested 查询示例

```bash
# 查询内存大于等于 256GB 的产品
GET /product/_search
{
  "query": {
    "nested": {
      "path": "specs",
      "query": {
        "bool": {
          "must": [
            { "term": { "specs.name": "内存" }},
            { "range": { "specs.numeric_value": { "gte": 256 }}}
          ]
        }
      }
    }
  }
}

# 查询 5 星好评的产品
GET /product/_search
{
  "query": {
    "nested": {
      "path": "reviews",
      "score_mode": "max",
      "query": {
        "bool": {
          "must": [
            { "term": { "reviews.rating": 5 }}
          ]
        }
      }
    }
  }
}

# 组合查询: 内存 >= 256GB 且有 5 星评论
GET /product/_search
{
  "query": {
    "bool": {
      "must": [
        {
          "nested": {
            "path": "specs",
            "query": {
              "bool": {
                "must": [
                  { "term": { "specs.name": "内存" }},
                  { "range": { "specs.numeric_value": { "gte": 256 }}}
                ]
              }
            }
          }
        },
        {
          "nested": {
            "path": "reviews",
            "query": {
              "bool": {
                "must": [
                  { "term": { "reviews.rating": 5 }}
                ]
              }
            }
          }
        }
      ]
    }
  }
}
```

### 3.4 Java 客户端实现

```java
// NestedQueryDemo.java
package com.shuai.elasticsearch.query;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.shuai.elasticsearch.config.ElasticsearchConfig;
import com.shuai.elasticsearch.model.ProductDocument;
import com.shuai.elasticsearch.util.ResponsePrinter;

import java.io.IOException;

public class NestedQueryDemo {

    private final ElasticsearchClient client;
    private static final String INDEX_NAME = "product";

    public NestedQueryDemo() {
        this.client = ElasticsearchConfig.getClient();
    }

    /**
     * 运行所有嵌套查询演示
     */
    public void runAllDemos() throws IOException {
        ResponsePrinter.printMethodInfo("NestedQueryDemo", "嵌套查询演示");

        nestedQueryOverview();
        queryByNestedSpec();
        queryByNestedReview();
        combinedNestedQuery();
    }

    /**
     * nested 查询概述
     */
    private void nestedQueryOverview() {
        ResponsePrinter.printMethodInfo("nestedQueryOverview", "nested 查询概述");

        System.out.println("  nested vs object:");
        System.out.println("    - object: 数组内对象被扁平化存储");
        System.out.println("    - nested: 数组内对象独立索引");
        System.out.println("  使用场景:");
        System.out.println("    - 需要跨字段精确匹配时");
        System.out.println("    - 数组元素需要独立查询时");
    }

    /**
     * 根据嵌套规格查询
     */
    private void queryByNestedSpec() throws IOException {
        ResponsePrinter.printMethodInfo("queryByNestedSpec", "嵌套规格查询");

        SearchResponse<ProductDocument> response = client.search(s -> s
            .index(INDEX_NAME)
            .query(q -> q
                .nested(n -> n
                    .path("specs")
                    .query(nq -> nq
                        .bool(b -> b
                            .must(m -> m
                                .term(t -> t.field("specs.name").value("内存"))
                            )
                            .filter(f -> f
                                .range(r -> r
                                    .field("specs.numeric_value")
                                    .gte(co.elastic.clients.json.JsonData.of(256))
                                )
                            )
                        )
                    )
                )
            )
        , ProductDocument.class);

        printProductResults(response, "规格查询结果");
    }

    /**
     * 根据嵌套评论查询
     */
    private void queryByNestedReview() throws IOException {
        ResponsePrinter.printMethodInfo("queryByNestedReview", "嵌套评论查询");

        SearchResponse<ProductDocument> response = client.search(s -> s
            .index(INDEX_NAME)
            .query(q -> q
                .nested(n -> n
                    .path("reviews")
                    .scoreMode(co.elastic.clients.elasticsearch._types.query_dsl.NestedScoreMode.Avg)
                    .query(nq -> nq
                        .bool(b -> b
                            .must(m -> m
                                .term(t -> t.field("reviews.rating").value(5))
                            )
                        )
                    )
                )
            )
        , ProductDocument.class);

        printProductResults(response, "5星评论查询结果");
    }

    /**
     * 组合嵌套查询
     */
    private void combinedNestedQuery() throws IOException {
        ResponsePrinter.printMethodInfo("combinedNestedQuery", "组合嵌套查询");

        try {
            SearchResponse<ProductDocument> response = client.search(s -> s
                .index(INDEX_NAME)
                .query(q -> q
                    .bool(b -> b
                        .must(m -> m
                            .nested(n -> n
                                .path("specs")
                                .query(nq -> nq
                                    .bool(b2 -> b2
                                        .must(m2 -> m2.term(t -> t.field("specs.name").value("内存")))
                                        .filter(f2 -> f2.range(r -> r
                                            .field("specs.numeric_value")
                                            .gte(co.elastic.clients.json.JsonData.of(256))
                                        ))
                                    )
                                )
                            )
                        )
                        .must(m -> m
                            .nested(n -> n
                                .path("reviews")
                                .query(nq -> nq
                                    .bool(b2 -> b2
                                        .must(m2 -> m2.term(t -> t.field("reviews.rating").value(5)))
                                    )
                                )
                            )
                        )
                    )
                )
            , ProductDocument.class);

            printProductResults(response, "组合查询结果");
        } catch (Exception e) {
            System.out.println("    [跳过] 组合查询执行失败: " + e.getMessage());
        }
    }

    private void printProductResults(SearchResponse<ProductDocument> response, String description) {
        long total = response.hits().total() != null ? response.hits().total().value() : 0;
        System.out.println("  " + description + "，命中: " + total);

        for (Hit<ProductDocument> hit : response.hits().hits()) {
            ProductDocument doc = hit.source();
            if (doc != null) {
                System.out.println("    [" + hit.id() + "] " + doc.getName() +
                    " (评分: " + String.format("%.2f", hit.score() != null ? hit.score() : 0) + ")");
            }
        }
    }
}
```

### 3.5 模型类定义

```java
// ProductDocument.java
package com.shuai.elasticsearch.model;

import java.util.List;

public class ProductDocument {

    private String id;
    private String name;
    private String category;
    private List<Spec> specs;
    private List<Review> reviews;

    // getters and setters
    public static class Spec {
        private String name;
        private String value;
        private Float numericValue;

        // getters and setters
    }

    public static class Review {
        private String user;
        private Integer rating;
        private String comment;
        private String date;

        // getters and setters
    }
}
```

## 4. 嵌套聚合

### 4.1 nested 聚合

```bash
# 嵌套聚合查询
GET /product/_search
{
  "size": 0,
  "aggs": {
    "reviews_stats": {
      "nested": {
        "path": "reviews"
      },
      "aggs": {
        "rating_stats": {
          "stats": {
            "field": "reviews.rating"
          }
        }
      }
    }
  }
}
```

### 4.2 反向嵌套聚合

```bash
# 从 nested 返回到父级进行聚合
GET /product/_search
{
  "size": 0,
  "aggs": {
    "specs_nested": {
      "nested": {
        "path": "specs"
      },
      "aggs": {
        "spec_names": {
          "terms": {
            "field": "specs.name"
          },
          "aggs": {
            "avg_value": {
              "avg": {
                "field": "specs.numeric_value"
              }
            }
          }
        }
      }
    }
  }
}
```

## 5. 性能优化

### 5.1 避免深层嵌套

```
嵌套层级过深会影响性能和查询复杂度：
├── 层级 1: 产品
│   └── 层级 2: 规格 (nested)
│       └── 层级 3: 选项 (nested)  ⚠️ 不推荐
```

### 5.2 控制嵌套数量

```bash
# 避免单个文档有过多的 nested 元素
# 建议每个 nested 数组元素不超过 1000 个
```

### 5.3 使用 filter 上下文

```bash
# 在 nested 查询中使用 filter 上下文提高性能
GET /product/_search
{
  "query": {
    "nested": {
      "path": "reviews",
      "query": {
        "bool": {
          "filter": [  # 使用 filter 而非 must
            { "term": { "reviews.rating": 5 }}
          ]
        }
      }
    }
  }
}
```

## 6. 常见问题

| 问题 | 原因 | 解决方案 |
|------|------|----------|
| 查询结果不准确 | 使用 object 而非 nested | 改用 nested 类型 |
| nested 查询慢 | 嵌套层级过深或数量过多 | 减少嵌套层级 |
| 无法聚合 nested 字段 | 未使用 nested 聚合 | 使用 nested 聚合管道 |
| 评分异常 | 未理解 score_mode | 根据需求选择合适的评分模式 |

## 7. 扩展阅读

**代码位置**:
- [NestedQueryDemo.java](src/main/java/com/shuai/elasticsearch/query/NestedQueryDemo.java) - 嵌套查询演示
- [ProductDocument.java](src/main/java/com/shuai/elasticsearch/model/ProductDocument.java) - 嵌套文档模型

- [Nested Data Type](https://www.elastic.co/guide/en/elasticsearch/reference/current/nested.html)
- [Nested Query](https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-nested-query.html)
- [Nested Aggregation](https://www.elastic.co/guide/en/elasticsearch/reference/current/search-aggregations-bucket-nested-aggregation.html)
- [上一章: 查询类型](03-es-query-types.md) | [下一章: 评分函数](05-es-function-score.md)

---

**学习进度**: ⬜⬜⬜⬜⬜⬜⬜⬜ 0/18 文档完成

**最后更新**: 2026-01-17
