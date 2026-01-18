# 评分函数

> 本章讲解 Elasticsearch 的 function_score 自定义评分机制，实现个性化的搜索结果排序。

## 学习目标

完成本章学习后，你将能够：
- 理解 function_score 的工作原理
- 掌握各种评分函数的使用场景
- 实现多因素综合评分
- 优化搜索结果排序策略

## 1. function_score 概述

### 1.1 什么是 function_score

function_score 允许你**自定义文档的评分逻辑**，通过评分函数修改原始相关性分数。

```
┌─────────────────────────────────────────────────────────────────────┐
│                    function_score 处理流程                           │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   原始查询分数                                                    │
│        │                                                           │
│        ▼                                                           │
│   ┌─────────────────────────────────────────────────────────────┐   │
│   │                    function_score                            │   │
│   │  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐    │   │
│   │  │ script   │  │ random   │  │   field  │  │  decay   │    │   │
│   │  │  score   │  │  score   │  │  value   │  │ function │    │   │
│   │  └──────────┘  └──────────┘  └──────────┘  └──────────┘    │   │
│   │                        │                                      │   │
│   │                        ▼                                      │   │
│   │                 score_mode (合并方式)                          │   │
│   └─────────────────────────────────────────────────────────────┘   │
│                        │                                           │
│                        ▼                                           │
│   ┌─────────────────────────────────────────────────────────────┐   │
│   │                    boost_mode (加权方式)                      │   │
│   │         multiply | sum | avg | max | min | replace          │   │
│   └─────────────────────────────────────────────────────────────┘   │
│                        │                                           │
│                        ▼                                           │
│                     最终分数                                        │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### 1.2 function_score 语法

```bash
GET /blog/_search
{
  "query": {
    "function_score": {
      "query": { ... },              # 原始查询
      "functions": [                 # 评分函数列表
        {
          "script_score": {
            "script": { ... }
          }
        }
      ],
      "score_mode": "sum",           # 函数评分合并方式
      "boost_mode": "multiply",      # 原始分与函数分合并方式
      "min_score": 0.5               # 最小评分阈值
    }
  }
}
```

## 2. 评分函数类型

### 2.1 script_score - 脚本评分

```bash
# 使用自定义脚本计算分数
GET /blog/_search
{
  "query": {
    "function_score": {
      "query": {
        "match": { "title": "Elasticsearch" }
      },
      "functions": [
        {
          "script_score": {
            "script": {
              "source": "Math.log(doc['views'].value + 1)"
            }
          }
        }
      ],
      "boost_mode": "multiply"
    }
  }
}
```

**常用脚本函数**：

| 函数 | 说明 |
|------|------|
| `doc['field'].value` | 获取字段值 |
| `_score` | 原始评分 |
| `Math.log(x)` | 对数 |
| `Math.sqrt(x)` | 平方根 |
| `Math.pow(x, n)` | 幂运算 |

### 2.2 random_score - 随机评分

```bash
# 随机评分 (探索性搜索)
GET /blog/_search
{
  "query": {
    "function_score": {
      "query": { "match_all": {} },
      "functions": [
        {
          "random_score": {
            "seed": "my_seed",
            "field": "_id"
          }
        }
      ]
    }
  }
}
```

**使用场景**：
- 探索性搜索 (避免固定排序)
- A/B 测试
- 商品推荐 (随机展示)

### 2.3 field_value_factor - 字段值因子

```bash
# 基于字段值调整评分
GET /blog/_search
{
  "query": {
    "function_score": {
      "query": {
        "match": { "title": "教程" }
      },
      "functions": [
        {
          "field_value_factor": {
            "field": "views",
            "factor": 0.1,
            "modifier": "log1p",
            "missing": 1
          }
        }
      ],
      "boost_mode": "multiply"
    }
  }
}
```

**modifier 选项**：

| modifier | 说明 | 示例 |
|----------|------|------|
| `none` | 无修改 | views * 0.1 |
| `log` | log(views) | log(views) * 0.1 |
| `log1p` | log(1 + views) | log(1 + views) * 0.1 |
| `ln` | ln(views) | ln(views) * 0.1 |
| `ln1p` | ln(1 + views) | ln(1 + views) * 0.1 |
| `sqrt` | square root | sqrt(views) * 0.1 |
| `square` | square | views^2 * 0.1 |
| `reciprocal` | reciprocal | 1 / views * 0.1 |

### 2.4 衰减函数 (Decay Functions)

```bash
# gauss 高斯衰减 (推荐)
GET /blog/_search
{
  "query": {
    "function_score": {
      "query": { "match_all": {} },
      "functions": [
        {
          "gauss": {
            "createTime": {
              "origin": "now",
              "scale": "30d",
              "offset": "7d",
              "decay": 0.5
            }
          }
        }
      ]
    }
  }
}

# linear 线性衰减
GET /blog/_search
{
  "query": {
    "function_score": {
      "query": { "match_all": {} },
      "functions": [
        {
          "linear": {
            "price": {
              "origin": 100,
              "scale": 50
            }
          }
        }
      ]
    }
  }
}

# exp 指数衰减
GET /blog/_search
{
  "query": {
    "function_score": {
      "query": { "match_all": {} },
      "functions": [
        {
          "exp": {
            "location": {
              "origin": "40.7128, -74.0060",
              "scale": "100km"
            }
          }
        }
      ]
    }
  }
}
```

**衰减函数参数**：

| 参数 | 说明 | 示例 |
|------|------|------|
| `origin` | 中心点 | `"now"`, `"100"`, `"lat,lon"` |
| `scale` | 衰减范围 | `"30d"`, `"50"`, `"100km"` |
| `offset` | 偏移量 (此范围内不衰减) | `"7d"` |
| `decay` | 衰减率 | `0.5` (默认) |

```
衰减曲线示意:
score
  │
 1│  ━━━━━━━━━━━━━━━━━
   │            ╲
   │             ╲____
   │                  ╲____
 0│                       ╲____
   └─────────────────────────────► distance/origin
      0    offset    scale
```

## 3. 评分合并模式

### 3.1 score_mode - 函数评分合并

```bash
# 多函数组合时的合并方式
GET /blog/_search
{
  "query": {
    "function_score": {
      "functions": [
        { "field_value_factor": { "field": "views", "factor": 0.01 }},
        { "random_score": {} }
      ],
      "score_mode": "sum",    # 函数评分合并方式
      "boost_mode": "multiply"
    }
  }
}
```

| score_mode | 说明 |
|------------|------|
| `sum` | 累加 (默认) |
| `avg` | 平均 |
| `max` | 最大 |
| `min` | 最小 |
| `first` | 使用第一个函数 |
| `multiply` | 乘积 |

### 3.2 boost_mode - 原始分与函数分合并

```bash
# 合并原始查询分数和函数分数
GET /blog/_search
{
  "query": {
    "function_score": {
      "query": { "match": { "title": "教程" }},
      "functions": [
        { "field_value_factor": { "field": "views" }}
      ],
      "boost_mode": "multiply"   # 合并方式
    }
  }
}
```

| boost_mode | 说明 | 公式 |
|------------|------|------|
| `multiply` | 相乘 (默认) | `score * function_score` |
| `sum` | 累加 | `score + function_score` |
| `avg` | 平均 | `(score + function_score) / 2` |
| `min` | 最小 | `min(score, function_score)` |
| `max` | 最大 | `max(score, function_score)` |
| `replace` | 替换 | `function_score` |

### 3.3 min_score - 最小评分阈值

```bash
# 过滤掉低评分结果
GET /blog/_search
{
  "query": {
    "function_score": {
      "query": { "match": { "title": "教程" }},
      "functions": [
        { "field_value_factor": { "field": "views" }}
      ],
      "min_score": 2.0
    }
  }
}
```

## 4. 实践部分

### 4.1 综合评分示例

```bash
# 综合评分: 相关性 * 热度因子 * 新鲜度
GET /blog/_search
{
  "query": {
    "function_score": {
      "query": {
        "multi_match": {
          "query": "Elasticsearch 教程",
          "fields": ["title^3", "content"]
        }
      },
      "functions": [
        {
          "field_value_factor": {
            "field": "views",
            "factor": 0.001,
            "modifier": "log1p",
            "missing": 1
          }
        },
        {
          "gauss": {
            "createTime": {
              "origin": "now",
              "scale": "90d"
            }
          }
        }
      ],
      "score_mode": "sum",
      "boost_mode": "multiply"
    }
  }
}
```

### 4.2 Java 客户端实现

```java
// FunctionScoreDemo.java
package com.shuai.elasticsearch.query;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.shuai.elasticsearch.config.ElasticsearchConfig;
import com.shuai.elasticsearch.model.BlogDocument;
import com.shuai.elasticsearch.util.ResponsePrinter;

import java.io.IOException;

public class FunctionScoreDemo {

    private final ElasticsearchClient client;
    private static final String INDEX_NAME = "blog";

    public FunctionScoreDemo() {
        this.client = ElasticsearchConfig.getClient();
    }

    /**
     * 运行所有评分函数演示
     */
    public void runAllDemos() throws IOException {
        ResponsePrinter.printMethodInfo("FunctionScoreDemo", "评分函数演示");

        functionScoreOverview();
        scriptScoreQuery();
        randomScoreQuery();
        fieldValueFactorQuery();
        decayFunctionQuery();
        combinedFunctionScore();
    }

    /**
     * function_score 概述
     */
    private void functionScoreOverview() {
        ResponsePrinter.printMethodInfo("functionScoreOverview", "function_score 概述");

        System.out.println("  function_score 组件:");
        System.out.println("    - query: 原始查询条件");
        System.out.println("    - functions: 评分函数列表");
        System.out.println("    - score_mode: 多函数评分合并方式");
        System.out.println("    - boost_mode: 原始分与函数分合并方式");
        System.out.println("    - min_score: 最小评分阈值");

        System.out.println("\n  评分合并模式 (score_mode):");
        System.out.println("    - sum: 累加 (默认)");
        System.out.println("    - avg: 平均");
        System.out.println("    - max: 取最大");
        System.out.println("    - min: 取最小");
        System.out.println("    - first: 使用第一个函数");

        System.out.println("\n  权重合并模式 (boost_mode):");
        System.out.println("    - multiply: 相乘 (默认)");
        System.out.println("    - sum: 累加");
        System.out.println("    - avg: 平均");
        System.out.println("    - max: 取最大");
        System.out.println("    - min: 取最小");
        System.out.println("    - replace: 替换原始分");
    }

    /**
     * script_score - 脚本评分
     */
    private void scriptScoreQuery() throws IOException {
        ResponsePrinter.printMethodInfo("scriptScoreQuery", "script_score 脚本评分");

        SearchResponse<BlogDocument> response = client.search(s -> s
            .index(INDEX_NAME)
            .query(q -> q.functionScore(fs -> fs
                .query(fq -> fq.match(m -> m.field("title").query("Java")))
                .functions(f -> f
                    .scriptScore(ss -> ss
                        .script(sc -> sc.source("Math.log(doc['views'].value + 1) * 0.1"))
                    )
                )
                .scoreMode(co.elastic.clients.elasticsearch._types.query_dsl.FunctionScoreMode.Sum)
                .boostMode(co.elastic.clients.elasticsearch._types.query_dsl.FunctionBoostMode.Multiply)
            ))
        , BlogDocument.class);

        printBlogResultsWithScore(response, "脚本评分结果");
    }

    /**
     * random_score - 随机评分
     */
    private void randomScoreQuery() throws IOException {
        ResponsePrinter.printMethodInfo("randomScoreQuery", "random_score 随机评分");

        SearchResponse<BlogDocument> response1 = client.search(s -> s
            .index(INDEX_NAME)
            .query(q -> q.functionScore(fs -> fs
                .query(fq -> fq.matchAll(m -> m))
                .functions(f -> f
                    .randomScore(rs -> rs.seed("12345").field("id"))
                )
                .scoreMode(co.elastic.clients.elasticsearch._types.query_dsl.FunctionScoreMode.Sum)
            ))
        , BlogDocument.class);

        printBlogResultsWithScore(response1, "随机评分结果");
    }

    /**
     * field_value_factor - 字段值因子
     */
    private void fieldValueFactorQuery() throws IOException {
        ResponsePrinter.printMethodInfo("fieldValueFactorQuery", "field_value_factor 字段值因子");

        SearchResponse<BlogDocument> response = client.search(s -> s
            .index(INDEX_NAME)
            .query(q -> q.functionScore(fs -> fs
                .query(fq -> fq.match(m -> m.field("title").query("Java")))
                .functions(f -> f
                    .fieldValueFactor(fvf -> fvf
                        .field("views")
                        .factor(0.01)
                        .modifier(co.elastic.clients.elasticsearch._types.query_dsl.FieldValueFactorModifier.Log1p)
                        .missing(1.0)
                    )
                )
                .scoreMode(co.elastic.clients.elasticsearch._types.query_dsl.FunctionScoreMode.Sum)
                .boostMode(co.elastic.clients.elasticsearch._types.query_dsl.FunctionBoostMode.Multiply)
            ))
        , BlogDocument.class);

        printBlogResultsWithScore(response, "浏览量加权结果");
    }

    /**
     * 衰减函数模拟
     */
    private void decayFunctionQuery() throws IOException {
        ResponsePrinter.printMethodInfo("decayFunctionQuery", "decay 衰减函数");

        try {
            SearchResponse<BlogDocument> response = client.search(s -> s
                .index(INDEX_NAME)
                .query(q -> q.functionScore(fs -> fs
                    .query(fq -> fq.matchAll(m -> m))
                    .functions(f -> f
                        .scriptScore(ss -> ss
                            .script(sc -> sc.source("_score * Math.log(doc['views'].value + 1)"))
                        )
                    )
                    .scoreMode(co.elastic.clients.elasticsearch._types.query_dsl.FunctionScoreMode.Sum)
                    .boostMode(co.elastic.clients.elasticsearch._types.query_dsl.FunctionBoostMode.Multiply)
                ))
            , BlogDocument.class);

            printBlogResultsWithScore(response, "衰减模拟结果");
        } catch (Exception e) {
            System.out.println("    [跳过] 查询执行失败: " + e.getMessage());
        }
    }

    /**
     * 组合评分
     */
    private void combinedFunctionScore() throws IOException {
        ResponsePrinter.printMethodInfo("combinedFunctionScore", "组合评分函数");

        try {
            SearchResponse<BlogDocument> response = client.search(s -> s
                .index(INDEX_NAME)
                .query(q -> q.functionScore(fs -> fs
                    .query(fq -> fq.match(m -> m.field("title").query("Java")))
                    .functions(f -> f
                        .fieldValueFactor(fvf -> fvf
                            .field("views")
                            .factor(0.01)
                            .modifier(co.elastic.clients.elasticsearch._types.query_dsl.FieldValueFactorModifier.Log1p)
                            .missing(1.0)
                        )
                    )
                    .scoreMode(co.elastic.clients.elasticsearch._types.query_dsl.FunctionScoreMode.Sum)
                    .boostMode(co.elastic.clients.elasticsearch._types.query_dsl.FunctionBoostMode.Multiply)
                ))
            , BlogDocument.class);

            printBlogResultsWithScore(response, "组合评分结果");
        } catch (Exception e) {
            System.out.println("    [跳过] 查询执行失败: " + e.getMessage());
        }
    }

    private void printBlogResultsWithScore(SearchResponse<BlogDocument> response, String description) {
        long total = response.hits().total() != null ? response.hits().total().value() : 0;
        System.out.println("  " + description + "，命中: " + total);

        for (Hit<BlogDocument> hit : response.hits().hits()) {
            BlogDocument doc = hit.source();
            if (doc != null) {
                System.out.println("    [" + hit.id() + "] " + doc.getTitle() +
                    " (评分: " + String.format("%.4f", hit.score() != null ? hit.score() : 0.0) +
                    ", 浏览: " + doc.getViews() + ")");
            }
        }
    }
}
```

## 5. 评分策略设计

### 5.1 电商搜索评分

```bash
# 综合评分 = 相关性 * 销量权重 * 价格权重 * 新品权重
GET /product/_search
{
  "query": {
    "function_score": {
      "query": {
        "multi_match": {
          "query": "iPhone",
          "fields": ["name^3", "description"]
        }
      },
      "functions": [
        {
          "field_value_factor": {
            "field": "sales_count",
            "factor": 0.001,
            "modifier": "log1p",
            "missing": 1
          }
        },
        {
          "script_score": {
            "script": {
              "source": "1.0 / (1.0 + Math.pow(doc['price'].value / 1000, 2))"
            }
          }
        },
        {
          "gauss": {
            "publish_date": {
              "origin": "now",
              "scale": "180d"
            }
          }
        }
      ],
      "score_mode": "sum",
      "boost_mode": "multiply"
    }
  }
}
```

### 5.2 内容推荐评分

```bash
# 内容推荐 = 相关性 * 用户偏好 * 内容热度
GET /article/_search
{
  "query": {
    "function_score": {
      "query": {
        "match": { "content": "技术" }
      },
      "functions": [
        {
          "filter": { "term": { "category": "技术" }},
          "weight": 2
        },
        {
          "filter": { "term": { "user_tags": "java" }},
          "weight": 1.5
        },
        {
          "field_value_factor": {
            "field": "likes",
            "factor": 0.1,
            "modifier": "log1p"
          }
        }
      ],
      "score_mode": "sum",
      "boost_mode": "multiply"
    }
  }
}
```

## 6. 常见问题

| 问题 | 原因 | 解决方案 |
|------|------|----------|
| 评分过低 | boost_mode 设置不当 | 使用 replace 模式 |
| 随机结果不稳定 | 未设置 seed | 设置固定的 seed 值 |
| 评分不均衡 | 函数权重差异过大 | 调整因子和 modifier |
| 性能差 | 复杂脚本计算 | 使用简单的 field_value_factor |

## 7. 扩展阅读

**代码位置**:
- [FunctionScoreDemo.java](src/main/java/com/shuai/elasticsearch/query/FunctionScoreDemo.java) - 评分函数演示

- [Function Score Query](https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-function-score-query.html)
- [Decay Functions](https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-function-score-query.html#decay-functions)
- [Weighting Queries](https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-function-score-query.html#function-weight)
- [上一章: 嵌套查询](04-es-nested-query.md) | [下一章: 聚合分析](06-es-aggregation.md)

---

**学习进度**: ⬜⬜⬜⬜⬜⬜⬜⬜ 0/18 文档完成

**最后更新**: 2026-01-17
