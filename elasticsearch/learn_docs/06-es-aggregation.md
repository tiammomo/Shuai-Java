# 聚合分析

> 本章讲解 Elasticsearch 的聚合分析功能，实现数据的统计、分类和复杂计算。

## 学习目标

完成本章学习后，你将能够：
- 理解聚合分析的基本概念
- 掌握指标聚合和桶聚合的使用
- 实现嵌套聚合和管道聚合
- 构建数据分析报表

## 1. 聚合类型概览

```
┌─────────────────────────────────────────────────────────────────────┐
│                         聚合类型分类                                  │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│  ┌─────────────────────────────────────────────────────────────┐    │
│  │                      指标聚合 (Metrics)                      │    │
│  │  ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐           │    │
│  │  │   avg   │ │   sum   │ │   min   │ │   max   │           │    │
│  │  └─────────┘ └─────────┘ └─────────┘ └─────────┘           │    │
│  │  ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐           │    │
│  │  │  card-  │ │ value_  │ │  perc-  │ │ stats/  │           │    │
│  │  │ inality │ │  count  │ │ entiles │ │ extended│           │    │
│  │  └─────────┘ └─────────┘ └─────────┘ └─────────┘           │    │
│  └─────────────────────────────────────────────────────────────┘    │
│                                                                      │
│  ┌─────────────────────────────────────────────────────────────┐    │
│  │                      桶聚合 (Bucket)                         │    │
│  │  ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐           │    │
│  │  │  terms  │ │  range  │ │histogram│ │  date_  │           │    │
│  │  │         │ │         │ │         │ │ histogram│          │    │
│  │  └─────────┘ └─────────┘ └─────────┘ └─────────┘           │    │
│  │  ┌─────────┐ ┌─────────┐ ┌─────────┐                       │    │
│  │  │  filter │ │  nested │ │  significant│                   │    │
│  │  │         │ │         │ │   _terms │                       │    │
│  │  └─────────┘ └─────────┘ └─────────┘                       │    │
│  └─────────────────────────────────────────────────────────────┘    │
│                                                                      │
│  ┌─────────────────────────────────────────────────────────────┐    │
│  │                      管道聚合 (Pipeline)                      │    │
│  │  ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐           │    │
│  │  │  avg_   │ │  sum_   │ │  min_   │ │  cumu-  │           │    │
│  │  │ bucket  │ │ bucket  │ │ bucket  │ │ lative_ │           │    │
│  │  │         │ │         │ │         │ │ sum     │           │    │
│  │  └─────────┘ └─────────┘ └─────────┘ └─────────┘           │    │
│  └─────────────────────────────────────────────────────────────┘    │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

## 2. 指标聚合 (Metrics Aggregation)

### 2.1 基本指标

```bash
# 基本统计
GET /blog/_search
{
  "size": 0,
  "aggs": {
    "stats_views": {
      "stats": {
        "field": "views"
      }
    }
  }
}

# 响应
{
  "aggregations" : {
    "stats_views" : {
      "count" : 10,
      "min" : 100.0,
      "max" : 10000.0,
      "avg" : 2150.0,
      "sum" : 21500.0
    }
  }
}
```

### 2.2 扩展统计

```bash
# 扩展统计 (包含方差、标准差等)
GET /blog/_search
{
  "size": 0,
  "aggs": {
    "extended_stats_views": {
      "extended_stats": {
        "field": "views",
        "sigma": 2   # 标准差倍数
      }
    }
  }
}

# 响应
{
  "aggregations" : {
    "extended_stats_views" : {
      "count" : 10,
      "min" : 100.0,
      "max" : 10000.0,
      "avg" : 2150.0,
      "sum" : 21500.0,
      "sum_of_squares" : 12500000.0,
      "variance" : 2500000.0,
      "std_deviation" : 1581.13883,
      "std_deviation_bounds" : {
        "upper" : 5312.2776,
        "lower" : -1012.2776
      }
    }
  }
}
```

### 2.3 百分位聚合

```bash
# 百分位统计
GET /blog/_search
{
  "size": 0,
  "aggs": {
    "percentile_views": {
      "percentiles": {
        "field": "views",
        "percents": [25, 50, 75, 90, 95, 99]
      }
    }
  }
}

# 近似百分位 (TDigest 算法)
GET /blog/_search
{
  "size": 0,
  "aggs": {
    "percentile_views": {
      "percentile_ranks": {
        "field": "views",
        "values": [100, 1000, 5000]
      }
    }
  }
}
```

## 3. 桶聚合 (Bucket Aggregation)

### 3.1 terms 聚合

```bash
# 按作者统计文章数
GET /blog/_search
{
  "size": 0,
  "aggs": {
    "authors": {
      "terms": {
        "field": "author.keyword",
        "size": 10
      }
    }
  }
}

# 响应
{
  "aggregations" : {
    "authors" : {
      "doc_count_error_upper_bound" : 0,
      "sum_other_doc_count" : 0,
      "buckets" : [
        { "key" : "张三", "doc_count" : 5 },
        { "key" : "李四", "doc_count" : 3 },
        { "key" : "王五", "doc_count" : 2 }
      ]
    }
  }
}
```

### 3.2 range 聚合

```bash
# 按浏览量区间统计
GET /blog/_search
{
  "size": 0,
  "aggs": {
    "views_ranges": {
      "range": {
        "field": "views",
        "ranges": [
          { "key": "低", "to": 500 },
          { "key": "中", "from": 500, "to": 2000 },
          { "key": "高", "from": 2000 }
        ]
      }
    }
  }
}
```

### 3.3 histogram 聚合

```bash
# 按浏览量直方图统计
GET /blog/_search
{
  "size": 0,
  "aggs": {
    "views_histogram": {
      "histogram": {
        "field": "views",
        "interval": 1000
      }
    }
  }
}
```

### 3.4 date_histogram 聚合

```bash
# 按日期直方图统计
GET /blog/_search
{
  "size": 0,
  "aggs": {
    "date_histo": {
      "date_histogram": {
        "field": "createTime",
        "calendar_interval": "month",
        "format": "yyyy-MM"
      }
    }
  }
}

# 按年周统计
GET /blog/_search
{
  "size": 0,
  "aggs": {
    "weekly_posts": {
      "date_histogram": {
        "field": "createTime",
        "calendar_interval": "week",
        "format": "yyyy-WW"
      }
    }
  }
}
```

### 3.5 filter 聚合

```bash
# 过滤聚合
GET /blog/_search
{
  "size": 0,
  "aggs": {
    "high_views": {
      "filter": {
        "range": { "views": { "gte": 1000 }}
      },
      "aggs": {
        "avg_likes": {
          "avg": { "field": "likes" }
        }
      }
    }
  }
}
```

### 3.6 nested 聚合

```bash
# 嵌套聚合 (用于 nested 类型)
GET /product/_search
{
  "size": 0,
  "aggs": {
    "reviews_stats": {
      "nested": {
        "path": "reviews"
      },
      "aggs": {
        "avg_rating": {
          "avg": { "field": "reviews.rating" }
        }
      }
    }
  }
}
```

## 4. 嵌套聚合

### 4.1 多级桶聚合

```bash
# 按作者分组，再按年份分组
GET /blog/_search
{
  "size": 0,
  "aggs": {
    "by_author": {
      "terms": {
        "field": "author.keyword",
        "size": 10
      },
      "aggs": {
        "by_year": {
          "date_histogram": {
            "field": "createTime",
            "calendar_interval": "year",
            "format": "yyyy"
          },
          "aggs": {
            "avg_views": {
              "avg": { "field": "views" }
            }
          }
        }
      }
    }
  }
}
```

### 4.2 桶内指标聚合

```bash
# 分组后计算平均值
GET /blog/_search
{
  "size": 0,
  "aggs": {
    "by_category": {
      "terms": {
        "field": "category.keyword",
        "size": 10
      },
      "aggs": {
        "avg_views": {
          "avg": { "field": "views" }
        },
        "sum_likes": {
          "sum": { "field": "likes" }
        },
        "max_comments": {
          "max": { "field": "comments_count" }
        }
      }
    }
  }
}
```

### 4.3 排序聚合

```bash
# 按聚合结果排序
GET /blog/_search
{
  "size": 0,
  "aggs": {
    "by_author": {
      "terms": {
        "field": "author.keyword",
        "size": 10,
        "order": [
          { "avg_views": "desc" },
          { "_count": "desc" }
        ]
      },
      "aggs": {
        "avg_views": {
          "avg": { "field": "views" }
        }
      }
    }
  }
}
```

## 5. 管道聚合 (Pipeline Aggregation)

### 5.1 管道聚合概念

管道聚合**基于其他聚合的结果进行计算**：

```
原始聚合结果 ──► 管道聚合 ──► 最终结果
     │                │
     ▼                ▼
[桶/指标]        [新计算值]
```

### 5.2 常用管道聚合

```bash
# 平均桶聚合 (avg_bucket)
GET /blog/_search
{
  "size": 0,
  "aggs": {
    "by_author": {
      "terms": { "field": "author.keyword" },
      "aggs": {
        "avg_views": { "avg": { "field": "views" }}
      }
    },
    "avg_views_overall": {
      "avg_bucket": {
        "buckets_path": "by_author>avg_views"
      }
    }
  }
}

# 累计和聚合 (cumulative_sum)
GET /blog/_search
{
  "size": 0,
  "aggs": {
    "by_month": {
      "date_histogram": {
        "field": "createTime",
        "calendar_interval": "month"
      },
      "aggs": {
        "sales": { "sum": { "field": "amount" }},
        "cumulative_sales": {
          "cumulative_sum": {
            "buckets_path": "sales"
          }
        }
      }
    }
  }
}

# 移动平均聚合 (moving_avg)
GET /blog/_search
{
  "size": 0,
  "aggs": {
    "by_month": {
      "date_histogram": {
        "field": "createTime",
        "calendar_interval": "month"
      },
      "aggs": {
        "views": { "sum": { "field": "views" }},
        "moving_avg": {
          "moving_avg": {
            "buckets_path": "views",
            "window": 7,
            "model": "simple"
          }
        }
      }
    }
  }
}
```

### 5.3 桶排序聚合

```bash
# 分桶后排序
GET /blog/_search
{
  "size": 0,
  "aggs": {
    "by_author": {
      "terms": {
        "field": "author.keyword",
        "size": 10,
        "order": [
          { "total_views.value": "desc" }
        ]
      },
      "aggs": {
        "total_views": {
          "sum": { "field": "views" }
        }
      }
    }
  }
}
```

## 6. 实践部分

### 6.1 博客分析报表

```bash
# 综合分析报表
GET /blog/_search
{
  "size": 0,
  "aggs": {
    "整体统计": {
      "global": {},
      "aggs": {
        "总文章数": {
          "value_count": { "field": "id" }
        },
        "总浏览量": {
          "sum": { "field": "views" }
        }
      }
    },
    "作者分布": {
      "terms": {
        "field": "author.keyword",
        "size": 10
      },
      "aggs": {
        "平均浏览": {
          "avg": { "field": "views" }
        },
        "浏览量统计": {
          "stats": { "field": "views" }
        }
      }
    },
    "分类分布": {
      "terms": {
        "field": "category.keyword",
        "size": 5
      }
    },
    "月份趋势": {
      "date_histogram": {
        "field": "createTime",
        "calendar_interval": "month"
      },
      "aggs": {
        "文章数": {
          "value_count": { "field": "id" }
        },
        "平均浏览": {
          "avg": { "field": "views" }
        }
      }
    },
    "浏览量分布": {
      "range": {
        "field": "views",
        "ranges": [
          { "key": "0-100", "to": 100 },
          { "key": "100-500", "from": 100, "to": 500 },
          { "key": "500-1000", "from": 500, "to": 1000 },
          { "key": "1000+", "from": 1000 }
        ]
      }
    }
  }
}
```

### 6.2 Java 客户端实现

```java
// AggregationDemo.java
package com.shuai.elasticsearch.aggregation;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.aggregations.*;
import com.shuai.elasticsearch.config.ElasticsearchConfig;
import com.shuai.elasticsearch.util.ResponsePrinter;

import java.io.IOException;
import java.util.List;

public class AggregationDemo {

    private final ElasticsearchClient client;
    private static final String INDEX_NAME = "blog";

    public AggregationDemo() {
        this.client = ElasticsearchConfig.getClient();
    }

    /**
     * 运行所有聚合演示
     */
    public void runAllDemos() throws IOException {
        ResponsePrinter.printMethodInfo("AggregationDemo", "聚合分析演示");

        metricsAggregation();
        termsAggregation();
        dateHistogramAggregation();
        nestedAggregation();
        pipelineAggregation();
    }

    /**
     * 指标聚合
     */
    private void metricsAggregation() throws IOException {
        ResponsePrinter.printMethodInfo("metricsAggregation", "指标聚合");

        SearchResponse<Void> response = client.search(s -> s
            .index(INDEX_NAME)
            .size(0)
            .aggregations("stats_views", a -> a
                .stats(st -> st.field("views"))
            )
        , Void.class);

        var stats = response.aggregations().get("stats_views").stats();
        System.out.println("  统计结果:");
        System.out.println("    数量: " + stats.count());
        System.out.println("    最小: " + stats.min());
        System.out.println("    最大: " + stats.max());
        System.out.println("    平均: " + stats.avg());
        System.out.println("    总和: " + stats.sum());
    }

    /**
     * 词条聚合
     */
    private void termsAggregation() throws IOException {
        ResponsePrinter.printMethodInfo("termsAggregation", "词条聚合");

        SearchResponse<Void> response = client.search(s -> s
            .index(INDEX_NAME)
            .size(0)
            .aggregations("by_author", a -> a
                .terms(t -> t
                    .field("author.keyword")
                    .size(10)
                )
            )
        , Void.class);

        System.out.println("  作者分布:");
        var buckets = response.aggregations().get("by_author").sterms().buckets().array();
        for (var bucket : buckets) {
            System.out.println("    " + bucket.key().stringValue() + ": " + bucket.docCount() + " 篇");
        }
    }

    /**
     * 日期直方图聚合
     */
    private void dateHistogramAggregation() throws IOException {
        ResponsePrinter.printMethodInfo("dateHistogramAggregation", "日期直方图聚合");

        SearchResponse<Void> response = client.search(s -> s
            .index(INDEX_NAME)
            .size(0)
            .aggregations("by_month", a -> a
                .dateHistogram(dh -> dh
                    .field("createTime")
                    .calendarInterval(CalendarInterval.Month)
                    .format("yyyy-MM")
                )
                .aggregations("avg_views", aa -> aa.avg(av -> av.field("views")))
            )
        , Void.class);

        System.out.println("  月度统计:");
        var buckets = response.aggregations().get("by_month").dateHistogram().buckets().array();
        for (var bucket : buckets) {
            System.out.println("    " + bucket.keyAsString() + ": " + bucket.docCount() +
                " 篇，平均浏览: " + bucket.aggregations().get("avg_views").avg().value());
        }
    }

    /**
     * 嵌套聚合
     */
    private void nestedAggregation() throws IOException {
        ResponsePrinter.printMethodInfo("nestedAggregation", "嵌套聚合");

        SearchResponse<Void> response = client.search(s -> s
            .index(INDEX_NAME)
            .size(0)
            .aggregations("by_author", a -> a
                .terms(t -> t.field("author.keyword").size(10))
                .aggregations("views_stats", aa -> aa
                    .stats(st -> st.field("views"))
                )
            )
        , Void.class);

        System.out.println("  作者统计:");
        var buckets = response.aggregations().get("by_author").sterms().buckets().array();
        for (var bucket : buckets) {
            var stats = bucket.aggregations().get("views_stats").stats();
            System.out.println("    " + bucket.key().stringValue() +
                ": 平均 " + String.format("%.0f", stats.avg()) + " 浏览");
        }
    }

    /**
     * 管道聚合
     */
    private void pipelineAggregation() throws IOException {
        ResponsePrinter.printMethodInfo("pipelineAggregation", "管道聚合");

        SearchResponse<Void> response = client.search(s -> s
            .index(INDEX_NAME)
            .size(0)
            .aggregations("by_author", a -> a
                .terms(t -> t.field("author.keyword").size(10))
                .aggregations("total_views", aa -> aa.sum(su -> su.field("views")))
            )
            .aggregations("avg_total_views", a -> a
                .avgBucket(av -> av.bucketsPath("by_author>total_views"))
            )
        , Void.class);

        var avgViews = response.aggregations().get("avg_total_views").avgBucket().value();
        System.out.println("  各作者总浏览量的平均值: " + String.format("%.0f", avgViews));
    }
}
```

## 7. 聚合优化

### 7.1 性能优化建议

| 优化项 | 说明 |
|--------|------|
| `size: 0` | 仅返回聚合结果，不返回文档 |
| `shard_size` | 控制每个分片返回的桶数 |
| `execution_hint` | 使用 map 或 global_ordinals 模式 |
| 减少嵌套层级 | 避免过深的聚合嵌套 |

### 7.2 大数据量聚合

```bash
# 分页聚合结果
GET /blog/_search
{
  "size": 0,
  "aggs": {
    "by_author": {
      "terms": {
        "field": "author.keyword",
        "size": 10,
        "shard_size": 20,
        "execution_hint": "global_ordinals"
      }
    }
  }
}
```

## 8. 常见问题

| 问题 | 原因 | 解决方案 |
|------|------|----------|
| 聚合结果不完整 | 分片数据倾斜 | 增加 shard_size |
| 聚合慢 | 数据量太大 | 使用 filter 提前过滤 |
| 内存溢出 | 桶数量过多 | 限制 size |
| nested 聚合无效 | path 错误 | 检查 nested path |

## 9. 扩展阅读

**代码位置**:
- [AggregationDemo.java](src/main/java/com/shuai/elasticsearch/aggregation/AggregationDemo.java) - 聚合分析演示
- [AggregationDemoTest.java](src/test/java/com/shuai/elasticsearch/aggregation/AggregationDemoTest.java) - 聚合测试

- [Aggregations](https://www.elastic.co/guide/en/elasticsearch/reference/current/search-aggregations.html)
- [Metrics Aggregations](https://www.elastic.co/guide/en/elasticsearch/reference/current/search-aggregations-metrics-aggregation.html)
- [Bucket Aggregations](https://www.elastic.co/guide/en/elasticsearch/reference/current/search-aggregations-bucket-aggregation.html)
- [Pipeline Aggregations](https://www.elastic.co/guide/en/elasticsearch/reference/current/search-aggregations-pipeline-aggregation.html)
- [上一章: 评分函数](05-es-function-score.md) | [下一章: 全文检索](07-es-fulltext-search.md)

---

**学习进度**: ⬜⬜⬜⬜⬜⬜⬜⬜ 0/18 文档完成

**最后更新**: 2026-01-17
