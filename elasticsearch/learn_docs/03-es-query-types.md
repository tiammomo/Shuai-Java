# 查询类型

> 本章全面介绍 Elasticsearch 的查询类型，包括全文检索、精确匹配、复合查询等，是构建搜索功能的核心。

## 学习目标

完成本章学习后，你将能够：
- 区分并使用全文检索查询和精确匹配查询
- 掌握复合查询 (bool) 的组合方式
- 理解查询上下文和过滤上下文的区别
- 熟练使用各种查询类型解决实际问题

## 1. 查询分类概览

```
┌─────────────────────────────────────────────────────────────────────┐
│                         查询类型分类                                  │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│  ┌─────────────────────────────────────────────────────────────┐    │
│  │                      全文检索查询                            │    │
│  │  ◉ match      ◉ match_phrase    ◉ multi_match             │    │
│  │  ◉ match_bool_prefix    ◉ query_string    ◉ simple_query  │    │
│  └─────────────────────────────────────────────────────────────┘    │
│                                                                      │
│  ┌─────────────────────────────────────────────────────────────┐    │
│  │                      精确匹配查询                            │    │
│  │  ◉ term      ◉ terms      ◉ range                         │    │
│  │  ◉ exists    ◉ prefix     ◉ wildcard / regex / fuzzy      │    │
│  └─────────────────────────────────────────────────────────────┘    │
│                                                                      │
│  ┌─────────────────────────────────────────────────────────────┐    │
│  │                      复合查询                               │    │
│  │  ◉ bool      ◉ constant_score    ◉ function_score         │    │
│  │  ◉ dis_max   ◉ boosting                                 │    │
│  └─────────────────────────────────────────────────────────────┘    │
│                                                                      │
│  ┌─────────────────────────────────────────────────────────────┐    │
│  │                      嵌套查询                               │    │
│  │  ◉ nested    ◉ parent_id                                 │    │
│  └─────────────────────────────────────────────────────────────┘    │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

## 2. 全文检索查询

### 2.1 match 查询

```bash
# 基本 match 查询
GET /blog/_search
{
  "query": {
    "match": {
      "title": "Elasticsearch 教程"
    }
  }
}

# 分词后任一词匹配 (OR)
# 搜索 "Elasticsearch 教程" → "elasticsearch" OR "教程"

# 使用 AND 操作符
GET /blog/_search
{
  "query": {
    "match": {
      "title": {
        "query": "Elasticsearch 教程",
        "operator": "and"
      }
    }
  }
}
# 分词后所有词匹配 (AND)
# 搜索 "Elasticsearch 教程" → "elasticsearch" AND "教程"
```

**match 查询特点**：
- 对查询文本进行分词
- 默认使用 OR 逻辑
- 支持最小词匹配数 (minimum_should_match)

```bash
# 最小匹配 2 个词
GET /blog/_search
{
  "query": {
    "match": {
      "title": {
        "query": "Elasticsearch 教程 高级",
        "minimum_should_match": "2"
      }
    }
  }
}
```

### 2.2 match_phrase 查询

```bash
# 短语匹配 (保持词序)
GET /blog/_search
{
  "query": {
    "match_phrase": {
      "title": "Elasticsearch 教程"
    }
  }
}

# 带 slop 的短语匹配 (允许词间隔)
GET /blog/_search
{
  "query": {
    "match_phrase": {
      "title": {
        "query": "Elasticsearch 教程",
        "slop": 2    # 允许最多 2 个词的间隔
      }
    }
  }
}
```

**match_phrase vs match**：

| 特性 | match | match_phrase |
|------|-------|--------------|
| 分词 | 是 | 是 |
| 词序 | 无关 | 必须保持 |
| 间隔 | 允许 | 严格 (可配置 slop) |
| 评分 | 词频加权 | 包含完整短语更高 |

### 2.3 multi_match 查询

```bash
# 多字段匹配
GET /blog/_search
{
  "query": {
    "multi_match": {
      "query": "Elasticsearch 教程",
      "fields": ["title", "content", "description"]
    }
  }
}

# 字段加权
GET /blog/_search
{
  "query": {
    "multi_match": {
      "query": "Elasticsearch",
      "fields": ["title^3", "content^2", "description"]
    }
  }
}

# 使用通配符
GET /blog/_search
{
  "query": {
    "multi_match": {
      "query": "Elasticsearch",
      "fields": ["title*", "content*"]
    }
  }
}
```

### 2.4 query_string 查询

```bash
# 复杂查询语法
GET /blog/_search
{
  "query": {
    "query_string": {
      "query": "(Elasticsearch AND 教程) OR (搜索 AND 高级)",
      "fields": ["title", "content"]
    }
  }
}

# 默认字段
GET /blog/_search
{
  "query": {
    "query_string": {
      "query": "Elasticsearch 教程",
      "default_field": "content"
    }
  }
}
```

### 2.5 simple_query_string 查询

```bash
# 简化版 query_string，更健壮
GET /blog/_search
{
  "query": {
    "simple_query_string": {
      "query": "Elasticsearch +教程 -高级",
      "fields": ["title", "content"],
      "operators": ["+", "-", "|", "*"]
    }
  }
}
# + 表示必须包含
# - 表示必须排除
# | 表示 OR
# * 表示通配符
```

## 3. 精确匹配查询

### 3.1 term 查询

```bash
# 精确值匹配 (不分词)
GET /blog/_search
{
  "query": {
    "term": {
      "author.keyword": "张三"
    }
  }
}

# 多值精确匹配
GET /blog/_search
{
  "query": {
    "terms": {
      "author.keyword": ["张三", "李四", "王五"]
    }
  }
}
```

**term 使用场景**：

| 字段类型 | 是否使用 term | 示例 |
|----------|---------------|------|
| `keyword` | ✅ | `{"term": {"status": "published"}}` |
| `text` | ❌ (通常) | `{"match": {"title": "关键词"}}` |
| `integer` | ✅ | `{"term": {"views": 100}}` |
| `date` | ✅ | `{"term": {"createTime": "2024-01-17"}}` |
| `boolean` | ✅ | `{"term": {"published": true}}` |

### 3.2 range 查询

```bash
# 数值范围
GET /blog/_search
{
  "query": {
    "range": {
      "views": {
        "gte": 100,
        "lte": 500
      }
    }
  }
}

# 日期范围
GET /blog/_search
{
  "query": {
    "range": {
      "createTime": {
        "gte": "2024-01-01",
        "lte": "2024-12-31",
        "format": "yyyy-MM-dd"
      }
    }
  }
}

# 日期数学表达式
GET /blog/_search
{
  "query": {
    "range": {
      "createTime": {
        "gte": "now-30d/d"
      }
    }
  }
}
```

**range 操作符**：

| 操作符 | 说明 |
|--------|------|
| `gt` | 大于 |
| `gte` | 大于等于 |
| `lt` | 小于 |
| `lte` | 小于等于 |

### 3.3 exists 查询

```bash
# 字段存在查询
GET /blog/_search
{
  "query": {
    "exists": {
      "field": "tags"
    }
  }
}

# 字段不存在
GET /blog/_search
{
  "query": {
    "bool": {
      "must_not": [
        {
          "exists": {
            "field": "tags"
          }
        }
      ]
    }
  }
}
```

### 3.4 前缀/通配符查询

```bash
# 前缀匹配
GET /blog/_search
{
  "query": {
    "prefix": {
      "title.keyword": "Elastic"
    }
  }
}

# 通配符查询
GET /blog/_search
{
  "query": {
    "wildcard": {
      "title.keyword": "Elast*c"
    }
  }
}

# 正则表达式查询
GET /blog/_search
{
  "query": {
    "regexp": {
      "title.keyword": "Elastic[0-9]+"
    }
  }
}
```

### 3.5 fuzzy 模糊查询

```bash
# 模糊匹配 (容忍拼写错误)
GET /blog/_search
{
  "query": {
    "fuzzy": {
      "title": {
        "value": "Elasticsearc",
        "fuzziness": "AUTO"
      }
    }
  }
}

# 带前缀的模糊查询
GET /blog/_search
{
  "query": {
    "fuzzy": {
      "title": {
        "value": "Elasticsearc",
        "fuzziness": "AUTO",
        "prefix_length": 2    # 前 2 个字符必须匹配
      }
    }
  }
}
```

## 4. 复合查询

### 4.1 bool 查询

```bash
# bool 查询组合
GET /blog/_search
{
  "query": {
    "bool": {
      "must": [
        { "match": { "title": "Elasticsearch" }}
      ],
      "should": [
        { "match": { "content": "教程" }},
        { "match": { "content": "入门" }}
      ],
      "must_not": [
        { "term": { "status": "draft" }}
      ],
      "filter": [
        { "term": { "published": true }},
        { "range": { "views": { "gte": 100 }}}
      ]
    }
  }
}
```

**bool 子句说明**：

| 子句 | 查询上下文 | 说明 |
|------|-----------|------|
| `must` | ✅ | 必须匹配，贡献评分 |
| `should` | ✅ | 应该匹配，贡献评分 |
| `must_not` | ❌ | 必须不匹配 (过滤上下文) |
| `filter` | ❌ | 必须匹配 (过滤上下文) |

**查询上下文 vs 过滤上下文**：

```
┌─────────────────────────────────────────────────────────────────────┐
│                    查询上下文 vs 过滤上下文                           │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│  查询上下文 (must, should)                                           │
│  ├── 计算相关性评分                                                  │
│  ├── 受 TF-IDF / BM25 影响                                           │
│  ├── 缓存与否不确定                                                  │
│  └── 示例: match, term, bool.must                                    │
│                                                                      │
│  过滤上下文 (filter, must_not)                                       │
│  ├── 只判断是否匹配                                                  │
│  ├── 不计算评分 (评分=1)                                             │
│  ├── 结果会被缓存                                                    │
│  └── 示例: term, range, bool.filter                                  │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### 4.2 constant_score 查询

```bash
# 固定评分查询
GET /blog/_search
{
  "query": {
    "constant_score": {
      "filter": {
        "term": { "status": "published" }
      },
      "boost": 1.5
    }
  }
}
```

### 4.3 dis_max 查询

```bash
# 分离最大化查询
GET /blog/_search
{
  "query": {
    "dis_max": {
      "queries": [
        { "match": { "title": "Elasticsearch 教程" }},
        { "match": { "content": "Elasticsearch 教程" }}
      ],
      "tie_breaker": 0.3
    }
  }
}
# tie_breaker: 将其他字段的评分乘以此系数后累加
```

### 4.4 boosting 查询

```bash
# 降权查询 (保留但降低评分)
GET /blog/_search
{
  "query": {
    "boosting": {
      "positive": {
        "match": { "title": "Elasticsearch" }
      },
      "negative": {
        "match": { "content": "入门" }
      },
      "negative_boost": 0.2
    }
  }
}
```

## 5. 分页与排序

### 5.1 分页查询

```bash
# 基本分页 (from/size)
GET /blog/_search
{
  "from": 0,
  "size": 10,
  "query": {
    "match": { "title": "Elasticsearch" }
  }
}

# 深度分页问题
# from + size 超过 max_result_window (默认 10000) 时会报错
# 解决方案: search_after
```

### 5.2 search_after 深分页

```bash
# 第一次查询获取 sort 值
GET /blog/_search
{
  "query": { "match_all": {} },
  "sort": [
    { "views": "desc" },
    { "_id": "asc" }
  ],
  "size": 10
}

# 使用上次的 sort 值继续查询
GET /blog/_search
{
  "query": { "match_all": {} },
  "sort": [
    { "views": "desc" },
    { "_id": "asc" }
  ],
  "search_after": [100, "doc_id_10"],
  "size": 10
}
```

### 5.3 排序

```bash
# 单字段排序
GET /blog/_search
{
  "query": { "match_all": {} },
  "sort": [
    { "views": "desc" }
  ]
}

# 多字段排序
GET /blog/_search
{
  "query": { "match_all": {} },
  "sort": [
    { "views": "desc" },
    { "createTime": "asc" }
  ]
}

# 按评分排序
GET /blog/_search
{
  "query": {
    "match": { "title": "Elasticsearch" }
  },
  "sort": [
    "_score",
    { "views": "desc" }
  ]
}

# 对 text 字段排序 (需要使用 fielddata)
GET /blog/_search
{
  "query": { "match_all": {} },
  "sort": [
    { "title.keyword": "asc" }
  ]
}
```

## 6. 高亮显示

```bash
# 高亮显示
GET /blog/_search
{
  "query": {
    "match": { "title": "Elasticsearch" }
  },
  "highlight": {
    "fields": {
      "title": {},
      "content": {
        "fragment_size": 150,
        "number_of_fragments": 3
      }
    },
    "pre_tags": ["<em>"],
    "post_tags": ["</em>"]
  }
}
```

**高亮配置**：

| 参数 | 说明 | 默认值 |
|------|------|--------|
| `fragment_size` | 片段长度 | 100 |
| `number_of_fragments` | 片段数量 | 5 |
| `pre_tags` | 高亮前缀 | `<em>` |
| `post_tags` | 高亮后缀 | `</em>` |

## 7. 实践部分

### 7.1 Java 客户端查询示例

```java
// QueryDemo.java
package com.shuai.elasticsearch.query;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import com.shuai.elasticsearch.config.ElsearchConfig;
import com.shuai.elasticsearch.model.BlogDocument;
import com.shuai.elasticsearch.util.ResponsePrinter;

import java.io.IOException;
import java.util.List;

public class QueryDemo {

    private final ElasticsearchClient client;
    private static final String INDEX_NAME = "blog";

    public QueryDemo() {
        this.client = ElasticsearchConfig.getClient();
    }

    /**
     * 运行所有查询演示
     */
    public void runAllDemos() throws IOException {
        ResponsePrinter.printMethodInfo("QueryDemo", "查询类型演示");

        matchQuery();
        matchPhraseQuery();
        multiMatchQuery();
        termQuery();
        rangeQuery();
        boolQuery();
        highlightQuery();
    }

    /**
     * match 查询
     */
    private void matchQuery() throws IOException {
        ResponsePrinter.printMethodInfo("matchQuery", "match 查询");

        SearchResponse<BlogDocument> response = client.search(s -> s
            .index(INDEX_NAME)
            .query(q -> q
                .match(m -> m
                    .field("title")
                    .query("Elasticsearch")
                )
            )
        , BlogDocument.class);

        printResults(response, "match 查询结果");
    }

    /**
     * match_phrase 查询
     */
    private void matchPhraseQuery() throws IOException {
        ResponsePrinter.printMethodInfo("matchPhraseQuery", "match_phrase 查询");

        SearchResponse<BlogDocument> response = client.search(s -> s
            .index(INDEX_NAME)
            .query(q -> q
                .matchPhrase(mp -> mp
                    .field("title")
                    .query("Elasticsearch 教程")
                    .slop(2)
                )
            )
        , BlogDocument.class);

        printResults(response, "短语匹配结果");
    }

    /**
     * multi_match 查询
     */
    private void multiMatchQuery() throws IOException {
        ResponsePrinter.printMethodInfo("multiMatchQuery", "multi_match 查询");

        SearchResponse<BlogDocument> response = client.search(s -> s
            .index(INDEX_NAME)
            .query(q -> q
                .multiMatch(mm -> mm
                    .query("Elasticsearch 教程")
                    .fields(List.of("title^3", "content^2", "description"))
                )
            )
        , BlogDocument.class);

        printResults(response, "多字段匹配结果");
    }

    /**
     * term 查询
     */
    private void termQuery() throws IOException {
        ResponsePrinter.printMethodInfo("termQuery", "term 查询");

        SearchResponse<BlogDocument> response = client.search(s -> s
            .index(INDEX_NAME)
            .query(q -> q
                .term(t -> t
                    .field("author.keyword")
                    .value("张三")
                )
            )
        , BlogDocument.class);

        printResults(response, "精确匹配结果");
    }

    /**
     * range 查询
     */
    private void rangeQuery() throws IOException {
        ResponsePrinter.printMethodInfo("rangeQuery", "range 查询");

        SearchResponse<BlogDocument> response = client.search(s -> s
            .index(INDEX_NAME)
            .query(q -> q
                .range(r -> r
                    .field("views")
                    .gte(co.elastic.clients.json.JsonData.of(100))
                )
            )
        , BlogDocument.class);

        printResults(response, "范围查询结果");
    }

    /**
     * bool 查询
     */
    private void boolQuery() throws IOException {
        ResponsePrinter.printMethodInfo("boolQuery", "bool 查询");

        SearchResponse<BlogDocument> response = client.search(s -> s
            .index(INDEX_NAME)
            .query(q -> q
                .bool(b -> b
                    .must(m -> m
                        .match(mt -> mt.field("title").query("教程"))
                    )
                    .filter(f -> f
                        .term(t -> t.field("status").value("published"))
                    )
                )
            )
        , BlogDocument.class);

        printResults(response, "复合查询结果");
    }

    /**
     * 高亮查询
     */
    private void highlightQuery() throws IOException {
        ResponsePrinter.printMethodInfo("highlightQuery", "高亮查询");

        SearchResponse<BlogDocument> response = client.search(s -> s
            .index(INDEX_NAME)
            .query(q -> q
                .match(m -> m.field("title").query("Elasticsearch"))
            )
            .highlight(h -> h
                .fields("title", hf -> hf)
                .fields("content", hf -> hf
                    .fragmentSize(100)
                    .numberOfFragments(2)
                )
            )
        , BlogDocument.class);

        System.out.println("  高亮结果:");
        for (Hit<BlogDocument> hit : response.hits().hits()) {
            System.out.println("    ID: " + hit.id());
            System.out.println("    标题: " + hit.source().getTitle());
            System.out.println("    高亮: " + hit.highlight());
        }
    }

    private void printResults(SearchResponse<BlogDocument> response, String description) {
        long total = response.hits().total() != null ? response.hits().total().value() : 0;
        System.out.println("  " + description + "，命中: " + total);

        for (Hit<BlogDocument> hit : response.hits().hits()) {
            BlogDocument doc = hit.source();
            if (doc != null) {
                System.out.println("    [" + hit.id() + "] " + doc.getTitle() +
                    " (评分: " + String.format("%.2f", hit.score() != null ? hit.score() : 0) + ")");
            }
        }
    }
}
```

## 8. 常见问题

| 问题 | 原因 | 解决方案 |
|------|------|----------|
| 搜索无结果 | text 字段用 term 查询 | 改用 match 或 keyword 子字段 |
| 评分异常 | 未理解评分机制 | 使用 explain 调试 |
| 深度分页失败 | 超过 max_result_window | 使用 search_after |
| 高亮不正确 | 字段类型或分词器 | 检查 mapping 配置 |
| 模糊查询慢 | 通配符在词首 | 使用 prefix 而非 wildcard |

## 9. 扩展阅读

**代码位置**:
- [QueryDemo.java](src/main/java/com/shuai/elasticsearch/query/QueryDemo.java) - 查询类型演示
- [QueryDemoTest.java](src/test/java/com/shuai/elasticsearch/query/QueryDemoTest.java) - 查询测试

- [Query DSL](https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl.html)
- [Full Text Queries](https://www.elastic.co/guide/en/elasticsearch/reference/current/full-text-queries.html)
- [Term-level Queries](https://www.elastic.co/guide/en/elasticsearch/reference/current/term-level-queries.html)
- [Compound Queries](https://www.elastic.co/guide/en/elasticsearch/reference/current/compound-queries.html)
- [上一章: 文档操作](02-es-document-operations.md) | [下一章: 嵌套查询](04-es-nested-query.md)

---

**学习进度**: ⬜⬜⬜⬜⬜⬜⬜⬜ 0/18 文档完成

**最后更新**: 2026-01-17
