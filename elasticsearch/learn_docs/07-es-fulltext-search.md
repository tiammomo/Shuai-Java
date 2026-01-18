# 全文检索

> 本章深入讲解 Elasticsearch 的全文检索功能，包括分词器原理、IK 分词器配置和高亮显示。

## 学习目标

完成本章学习后，你将能够：
- 理解分词器的工作原理和组成
- 配置 IK 分词器实现中文分词
- 掌握同义词和停用词配置
- 实现搜索结果高亮显示

## 1. 分词器原理

### 1.1 分词器组成

```
┌─────────────────────────────────────────────────────────────────────┐
│                     分词器 (Analyzer) 组成                           │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   输入文本: "Elasticsearch 是强大的搜索引擎"                         │
│        │                                                            │
│        ▼                                                            │
│   ┌─────────────────────────────────────────────────────────────┐   │
│   │                    Character Filters                         │   │
│   │  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐            │   │
│   │  │ HTML Strip  │ │  Mapping    │ │   Pattern   │            │   │
│   │  │ (去除HTML)  │ │  (字符映射) │ │  (正则替换) │            │   │
│   │  └─────────────┘ └─────────────┘ └─────────────┘            │   │
│   └─────────────────────────────────────────────────────────────┘   │
│        │                                                            │
│        ▼                                                            │
│   ┌─────────────────────────────────────────────────────────────┐   │
│   │                      Tokenizer                               │   │
│   │                                                               │   │
│   │   ┌─────────────────────────────────────────────────────┐    │   │
│   │   │ ik_max_word: "Elasticsearch/是/强大/的/搜索引擎"   │    │   │
│   │   │ ik_smart:  "Elasticsearch/是/强大/搜索引擎"         │    │   │
│   │   └─────────────────────────────────────────────────────┘    │   │
│   │                                                               │   │
│   └─────────────────────────────────────────────────────────────┘   │
│        │                                                            │
│        ▼                                                            │
│   ┌─────────────────────────────────────────────────────────────┐   │
│   │                    Token Filters                             │   │
│   │  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐            │   │
│   │  │   Lowercase │ │   Synonym   │ │   Stopwords │            │   │
│   │  │  (转小写)   │ │   (同义词)  │ │  (停用词)   │            │   │
│   │  └─────────────┘ └─────────────┘ └─────────────┘            │   │
│   │  ┌─────────────┐ ┌─────────────┐                           │   │
│   │  │  Stemmer    │ │   Unique    │                           │   │
│   │  │  (词形还原) │ │  (去重)     │                           │   │
│   │  └─────────────┘ └─────────────┘                           │   │
│   └─────────────────────────────────────────────────────────────┘   │
│        │                                                            │
│        ▼                                                            │
│   输出词元列表: ["elasticsearch", "是", "强大", "搜索引擎"]         │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### 1.2 内置分词器

| 分词器 | 说明 | 示例 |
|--------|------|------|
| `standard` | 默认分词器 | "The 3 cats" → ["the", "3", "cats"] |
| `simple` | 简单分词 | "The 3 cats" → ["the", "cats"] |
| `whitespace` | 空白分词 | "The 3 cats" → ["The", "3", "cats"] |
| `keyword` | 不分词 | "The 3 cats" → ["The 3 cats"] |
| `pattern` | 正则分词 | 自定义模式 |
| `language` | 语言分词 | 英语、法语等 |

### 1.3 IK 分词器

**IK 分词器**是 ES 的中文分词插件，支持两种分词模式：

- `ik_max_word`: 穷尽式分词，词汇更细粒度
- `ik_smart`: 智能分词，词汇更粗粒度

```bash
# 测试 ik_max_word
GET /_analyze
{
  "analyzer": "ik_max_word",
  "text": "中华人民共和国"
}
# 结果: ["中华人民共和国", "中华人民", "中华", "华人", "人民共和国", "人民", "人", "民", "共和国", "共和", "和", "国"]

# 测试 ik_smart
GET /_analyze
{
  "analyzer": "ik_smart",
  "text": "中华人民共和国"
}
# 结果: ["中华人民共和国"]
```

## 2. IK 分词器配置

### 2.1 安装 IK 分词器

```bash
# Elasticsearch 8.x 安装方式
# 方式1: 使用 elasticsearch-plugin
./bin/elasticsearch-plugin install https://github.com/medcl/elasticsearch-analysis-ik/releases/download/v8.18.0/elasticsearch-analysis-ik-8.18.0.zip

# 方式2: 手动安装
cd plugins/
mkdir ik
cd ik
wget https://github.com/medcl/elasticsearch-analysis-ik/releases/download/v8.18.0/elasticsearch-analysis-ik-8.18.0.zip
unzip elasticsearch-analysis-ik-8.18.0.zip
rm *.zip
```

### 2.2 创建索引使用 IK

```bash
# 创建博客索引 (使用 IK 分词)
PUT /blog
{
  "settings": {
    "analysis": {
      "analyzer": {
        "ik_analyzer": {
          "type": "custom",
          "tokenizer": "ik_max_word"
        },
        "ik_smart_analyzer": {
          "type": "custom",
          "tokenizer": "ik_smart"
        }
      }
    }
  },
  "mappings": {
    "properties": {
      "title": {
        "type": "text",
        "analyzer": "ik_max_word",
        "search_analyzer": "ik_smart"
      },
      "content": {
        "type": "text",
        "analyzer": "ik_max_word",
        "search_analyzer": "ik_smart"
      }
    }
  }
}
```

### 2.3 自定义分词器

```bash
# 自定义中文分词器 (含同义词和停用词)
PUT /blog
{
  "settings": {
    "analysis": {
      "filter": {
        "synonym_filter": {
          "type": "synonym",
          "synonyms": [
            "elasticsearch, es",
            "搜索引擎, 搜索",
            "教程, 入门, 基础"
          ]
        },
        "stop_filter": {
          "type": "stop",
          "stopwords": ["的", "了", "是", "在", "和", "与"]
        }
      },
      "analyzer": {
        "my_analyzer": {
          "type": "custom",
          "tokenizer": "ik_max_word",
          "filter": ["lowercase", "synonym_filter", "stop_filter"]
        }
      }
    }
  }
}
```

## 3. 同义词配置

### 3.1 同义词格式

```bash
# 方式1: 词列表格式
elasticsearch,es,elastic
教程,入门,基础,学习

# 方式2: 规则格式
elasticsearch,es => es
搜索引擎,sphinx,elastic
```

### 3.2 同义词过滤器

```bash
PUT /blog
{
  "settings": {
    "analysis": {
      "filter": {
        "synonym": {
          "type": "synonym",
          "synonyms_path": "analysis/synonym.txt",
          "expand": true
        }
      },
      "analyzer": {
        "synonym_analyzer": {
          "type": "custom",
          "tokenizer": "ik_max_word",
          "filter": ["synonym"]
        }
      }
    }
  }
}
```

### 3.3 同义词搜索测试

```bash
# 搜索 "es" 会匹配包含 "elasticsearch" 的文档
GET /blog/_search
{
  "query": {
    "match": {
      "content": {
        "query": "es",
        "analyzer": "synonym_analyzer"
      }
    }
  }
}
```

## 4. 高亮显示

### 4.1 基本高亮

```bash
# 基本高亮配置
GET /blog/_search
{
  "query": {
    "match": { "content": "Elasticsearch 教程" }
  },
  "highlight": {
    "fields": {
      "content": {}
    },
    "pre_tags": ["<em>"],
    "post_tags": ["</em>"]
  }
}
```

### 4.2 高亮配置选项

```bash
# 完整高亮配置
GET /blog/_search
{
  "query": {
    "match": { "content": "Elasticsearch" }
  },
  "highlight": {
    "fields": {
      "content": {
        "fragment_size": 150,        # 片段长度
        "number_of_fragments": 3,    # 片段数量
        "fragmenter": "span",        # 片段化方式 (span/simple)
        "boundary_max_scan": 50,     # 边界扫描距离
        "encoder": "html"            # HTML 编码
      },
      "title": {
        "pre_tags": ["<strong>"],
        "post_tags": ["</strong>"]
      }
    },
    "tags_schema": "styled"         # 使用预定义样式
  }
}
```

### 4.3 高亮类型

```bash
# unified 高亮 (推荐，默认)
GET /blog/_search
{
  "highlight": {
    "type": "unified",
    "fields": {
      "content": {}
    }
  }
}

# plain 高亮 (纯文本，适合复杂查询)
GET /blog/_search
{
  "highlight": {
    "type": "plain",
    "fields": {
      "content": {
        "fragment_size": 100,
        "number_of_fragments": 2
      }
    }
  }
}

# fvh 高亮 (快速向量高亮)
GET /blog/_search
{
  "highlight": {
    "type": "fvh",
    "fields": {
      "content": {}
    }
  }
}
```

## 5. 实践部分

### 5.1 完整分词测试

```bash
# 测试不同分词器
GET /_analyze
{
  "text": "Elasticsearch 是分布式搜索引擎",
  "analyzer": "standard"
}

GET /_analyze
{
  "text": "Elasticsearch 是分布式搜索引擎",
  "analyzer": "ik_max_word"
}

GET /_analyze
{
  "text": "Elasticsearch 是分布式搜索引擎",
  "analyzer": "ik_smart"
}
```

### 5.2 Java 客户端实现

```java
// FullTextSearchDemo.java
package com.shuai.elasticsearch.fulltext;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.HighlightField;
import com.shuai.elasticsearch.config.ElasticsearchConfig;
import com.shuai.elasticsearch.model.BlogDocument;
import com.shuai.elasticsearch.util.ResponsePrinter;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FullTextSearchDemo {

    private final ElasticsearchClient client;
    private static final String INDEX_NAME = "blog";

    public FullTextSearchDemo() {
        this.client = ElasticsearchConfig.getClient();
    }

    /**
     * 运行所有全文检索演示
     */
    public void runAllDemos() throws IOException {
        ResponsePrinter.printMethodInfo("FullTextSearchDemo", "全文检索演示");

        analyzerTest();
        matchQuery();
        matchPhraseQuery();
        multiMatchQuery();
        highlightQuery();
        boolQuerySearch();
    }

    /**
     * 分词器测试
     */
    private void analyzerTest() throws IOException {
        ResponsePrinter.printMethodInfo("analyzerTest", "分词器测试");

        System.out.println("  测试文本: '中华人民共和国万岁'");

        System.out.println("\n  ik_max_word:");
        var response1 = client.indices().analyze(a -> a
            .analyzer("ik_max_word")
            .text("中华人民共和国万岁")
        );
        response1.tokens().forEach(t ->
            System.out.println("    - " + t.token())
        );

        System.out.println("\n  ik_smart:");
        var response2 = client.indices().analyze(a -> a
            .analyzer("ik_smart")
            .text("中华人民共和国万岁")
        );
        response2.tokens().forEach(t ->
            System.out.println("    - " + t.token())
        );
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
                    .query("Elasticsearch 教程")
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
                    .fields(List.of("title^3", "content^2"))
                )
            )
        , BlogDocument.class);

        printResults(response, "多字段匹配结果");
    }

    /**
     * 高亮查询
     */
    private void highlightQuery() throws IOException {
        ResponsePrinter.printMethodInfo("highlightQuery", "高亮查询");

        Map<String, HighlightField> highlightFields = new HashMap<>();
        highlightFields.put("title", HighlightField.of(h -> h
            .numberOfFragments(1)
        ));
        highlightFields.put("content", HighlightField.of(h -> h
            .fragmentSize(150)
            .numberOfFragments(2)
        ));

        SearchResponse<BlogDocument> response = client.search(s -> s
            .index(INDEX_NAME)
            .query(q -> q
                .match(m -> m.field("content").query("搜索"))
            )
            .highlight(h -> h
                .fields("title", hf -> hf)
                .fields("content", hf -> hf.fragmentSize(100).numberOfFragments(2))
                .preTags("<em>")
                .postTags("</em>")
            )
        , BlogDocument.class);

        System.out.println("  高亮结果:");
        for (Hit<BlogDocument> hit : response.hits().hits()) {
            System.out.println("  ID: " + hit.id());
            System.out.println("  标题: " + hit.source().getTitle());
            System.out.println("  高亮: " + hit.highlight());
        }
    }

    /**
     * 组合查询
     */
    private void boolQuerySearch() throws IOException {
        ResponsePrinter.printMethodInfo("boolQuerySearch", "组合查询");

        SearchResponse<BlogDocument> response = client.search(s -> s
            .index(INDEX_NAME)
            .query(q -> q
                .bool(b -> b
                    .must(m -> m
                        .multiMatch(mm -> mm
                            .query("分布式 搜索")
                            .fields(List.of("title^2", "content"))
                        )
                    )
                    .filter(f -> f
                        .term(t -> t.field("status").value("published"))
                    )
                )
            )
        , BlogDocument.class);

        printResults(response, "组合查询结果");
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

### 5.3 IK 分词器配置类

```java
// IkAnalyzerConfig.java
public class IkAnalyzerConfig {

    /**
     * 获取 IK 最大词分词器名称
     */
    public static String getIkMaxWordAnalyzer() {
        return "ik_max_word";
    }

    /**
     * 获取 IK 智能分词器名称
     */
    public static String getIkSmartAnalyzer() {
        return "ik_smart";
    }

    /**
     * 创建索引时配置 IK 分词器
     */
    public static void configureIndexSettings(CreateIndexRequest request) {
        request.settings(s -> s
            .numberOfShards(3)
            .numberOfReplicas(1)
            .analysis(a -> a
                .analyzer("ik_analyzer", an -> an
                    .custom(c -> c
                        .tokenizer("ik_max_word")
                        .filter(List.of("lowercase"))
                    )
                )
                .analyzer("ik_smart_analyzer", an -> an
                    .custom(c -> c
                        .tokenizer("ik_smart")
                        .filter(List.of("lowercase"))
                    )
                )
            )
        );
    }
}
```

## 6. 搜索优化技巧

### 6.1 查询优化

| 技巧 | 说明 | 示例 |
|------|------|------|
| 指定分词器 | 避免默认 standard 分词 | `search_analyzer: "ik_smart"` |
| 使用 bool should | 替代 OR 查询 | 多个 should 子句 |
| 限制高亮片段 | 减少响应大小 | `fragment_size: 100` |
| 过滤无效查询 | 提前过滤不相关数据 | bool filter |

### 6.2 相关性优化

```bash
# 调整字段权重
GET /blog/_search
{
  "query": {
    "multi_match": {
      "query": "Elasticsearch 教程",
      "fields": ["title^3", "content^2", "description"],
      "tie_breaker": 0.3
    }
  }
}

# 使用 phrase 提升精确度
GET /blog/_search
{
  "query": {
    "bool": {
      "should": [
        { "match_phrase": { "title": { "query": "Elasticsearch 教程", "boost": 3 }}},
        { "match": { "title": "Elasticsearch 教程" }}
      ]
    }
  }
}
```

## 7. 常见问题

| 问题 | 原因 | 解决方案 |
|------|------|----------|
| 中文分词过细 | 使用 ik_max_word | 搜索时用 ik_smart |
| 搜索无结果 | 查询词被过滤 | 检查停用词配置 |
| 高亮不正确 | 分词器不匹配 | 统一 analyzer |
| 同义词不生效 | 加载顺序问题 | 重启 ES |

## 8. 扩展阅读

**代码位置**:
- [FullTextSearchDemo.java](src/main/java/com/shuai/elasticsearch/fulltext/FullTextSearchDemo.java) - 全文检索演示
- [FullTextSearchTest.java](src/test/java/com/shuai/elasticsearch/fulltext/FullTextSearchTest.java) - 全文检索测试
- [BlogDocument.java](src/main/java/com/shuai/elasticsearch/model/BlogDocument.java) - 博客文档模型

- [Analysis](https://www.elastic.co/guide/en/elasticsearch/reference/current/analysis.html)
- [IK Analysis Plugin](https://github.com/medcl/elasticsearch-analysis-ik)
- [Synonym Filter](https://www.elastic.co/guide/en/elasticsearch/reference/current/analysis-synonym-tokenfilter.html)
- [Highlighting](https://www.elastic.co/guide/en/elasticsearch/reference/current/search-highlighting.html)
- [上一章: 聚合分析](06-es-aggregation.md) | [下一章: 地理查询](08-es-geo-query.md)

---

**学习进度**: ⬜⬜⬜⬜⬜⬜⬜⬜ 0/18 文档完成

**最后更新**: 2026-01-17
