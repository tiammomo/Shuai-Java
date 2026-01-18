package com.shuai.elasticsearch.aggregation;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.aggregations.CalendarInterval;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.shuai.elasticsearch.config.ElasticsearchConfig;
import com.shuai.elasticsearch.model.BlogDocument;
import com.shuai.elasticsearch.util.ResponsePrinter;

import java.io.IOException;
import java.util.Map;

/**
 * 聚合分析演示类
 *
 * 模块概述
 * ----------
 * 本模块演示 Elasticsearch 聚合分析功能。
 *
 * 核心内容
 * ----------
 *   - 指标聚合: avg、sum、max、min、stats
 *   - 桶聚合: terms、histogram、date_histogram
 *   - 管道聚合: 前一个聚合结果基础上再聚合
 *   - 嵌套聚合: 多层聚合组合
 *
 * @author Shuai
 * @version 1.0
 * @see <a href="https://www.elastic.co/guide/en/elasticsearch/reference/current/search-aggregations.html">Aggregations</a>
 */
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
        ResponsePrinter.printMethodInfo("聚合分析演示", "Elasticsearch 聚合分析功能");

        aggregationTypes();
        metricAggregation();
        bucketAggregation();
        pipelineAggregation();
        nestedAggregation();
    }

    /**
     * 聚合类型概述
     */
    private void aggregationTypes() {
        ResponsePrinter.printMethodInfo("aggregationTypes", "聚合类型分类");

        System.out.println("  聚合类型分类:");
        System.out.println("    - Metric Aggregation (指标聚合) - 计算数值指标");
        System.out.println("    - Bucket Aggregation (桶聚合) - 按条件分组");
        System.out.println("    - Pipeline Aggregation (管道聚合) - 聚合结果再聚合");
        System.out.println("    - Matrix Aggregation (矩阵聚合) - 多字段操作");
        System.out.println("\n  提示: 设置 \"size\": 0 可只返回聚合结果，不返回文档");
    }

    /**
     * 指标聚合
     *
     * REST API:
     *   GET /blog/_search
     *   { "size": 0, "aggs": { "avg_views": { "avg": { "field": "views" }}}}
     */
    private void metricAggregation() throws IOException {
        ResponsePrinter.printMethodInfo("metricAggregation", "指标聚合");

        // 1. 基本指标聚合
        System.out.println("\n  1. 基本指标聚合:");
        SearchResponse<BlogDocument> response1 = client.search(s -> s
            .index(INDEX_NAME)
            .size(0)
            .aggregations("avg_views", a -> a.avg(avg -> avg.field("views")))
            .aggregations("sum_views", a -> a.sum(sum -> sum.field("views")))
            .aggregations("max_views", a -> a.max(max -> max.field("views")))
            .aggregations("min_views", a -> a.min(min -> min.field("views")))
        , BlogDocument.class);

        printAggregationResults(response1.aggregations(),
            "avg_views", "sum_views", "max_views", "min_views");

        // 2. stats - 综合统计
        System.out.println("\n  2. stats 综合统计:");
        SearchResponse<BlogDocument> response2 = client.search(s -> s
            .index(INDEX_NAME)
            .size(0)
            .aggregations("views_stats", a -> a.stats(stats -> stats.field("views")))
        , BlogDocument.class);

        if (response2.aggregations().containsKey("views_stats")) {
            var stats = response2.aggregations().get("views_stats").stats();
            System.out.println("    count: " + stats.count());
            System.out.println("    min: " + stats.min());
            System.out.println("    max: " + stats.max());
            System.out.println("    avg: " + stats.avg());
            System.out.println("    sum: " + stats.sum());
        }

        // 3. cardinality - 去重计数
        System.out.println("\n  3. cardinality 去重计数:");
        SearchResponse<BlogDocument> response3 = client.search(s -> s
            .index(INDEX_NAME)
            .size(0)
            .aggregations("unique_authors", a -> a.cardinality(c -> c.field("author.keyword")))
        , BlogDocument.class);

        if (response3.aggregations().containsKey("unique_authors")) {
            System.out.println("    不同作者数: " + response3.aggregations().get("unique_authors").cardinality().value());
        }
    }

    /**
     * 桶聚合
     *
     * REST API:
     *   GET /blog/_search
     *   { "size": 0, "aggs": { "by_author": { "terms": { "field": "author.keyword" }}}}
     */
    private void bucketAggregation() throws IOException {
        ResponsePrinter.printMethodInfo("bucketAggregation", "桶聚合");

        // 1. terms - 术语聚合
        System.out.println("\n  1. terms 术语聚合:");
        SearchResponse<BlogDocument> response1 = client.search(s -> s
            .index(INDEX_NAME)
            .size(0)
            .aggregations("tags_buckets", a -> a.terms(t -> t.field("tags.keyword").size(10)))
        , BlogDocument.class);

        if (response1.aggregations().containsKey("tags_buckets")) {
            response1.aggregations().get("tags_buckets").sterms().buckets().array()
                .forEach(b -> System.out.println("    " + b.key().stringValue() + ": " + b.docCount()));
        }

        // 2. histogram - 数值直方图
        System.out.println("\n  2. histogram 数值直方图:");
        SearchResponse<BlogDocument> response2 = client.search(s -> s
            .index(INDEX_NAME)
            .size(0)
            .aggregations("views_histogram", a -> a.histogram(h -> h
                .field("views")
                .interval(100d)
            ))
        , BlogDocument.class);

        if (response2.aggregations().containsKey("views_histogram")) {
            response2.aggregations().get("views_histogram").histogram().buckets().array()
                .forEach(b -> System.out.println("    " + b.key() + " - " + b.docCount() + " 篇"));
        }

        // 3. date_histogram - 日期直方图
        System.out.println("\n  3. date_histogram 日期直方图:");
        SearchResponse<BlogDocument> response3 = client.search(s -> s
            .index(INDEX_NAME)
            .size(0)
            .aggregations("date_buckets", a -> a.dateHistogram(dh -> dh
                .field("createTime")
                .calendarInterval(CalendarInterval.Month)
            ))
        , BlogDocument.class);

        if (response3.aggregations().containsKey("date_buckets")) {
            response3.aggregations().get("date_buckets").dateHistogram().buckets().array()
                .forEach(b -> System.out.println("    " + b.keyAsString() + " - " + b.docCount() + " 篇"));
        }
    }

    /**
     * 管道聚合
     * 注意: 管道聚合需要使用子聚合方式实现
     */
    private void pipelineAggregation() throws IOException {
        ResponsePrinter.printMethodInfo("pipelineAggregation", "管道聚合");

        // 使用嵌套聚合模拟管道聚合效果
        System.out.println("\n  1. 嵌套聚合模拟管道聚合 (按作者分组并计算平均值):");
        SearchResponse<BlogDocument> response1 = client.search(s -> s
            .index(INDEX_NAME)
            .size(0)
            .aggregations("by_author", a -> a.terms(t -> t.field("author.keyword").size(10))
                .aggregations("avg_views", aa -> aa.avg(avg -> avg.field("views")))
            )
        , BlogDocument.class);

        if (response1.aggregations().containsKey("by_author")) {
            response1.aggregations().get("by_author").sterms().buckets().array()
                .forEach(authorBucket -> {
                    var avgViews = authorBucket.aggregations().get("avg_views").avg();
                    System.out.println("    " + authorBucket.key().stringValue() +
                        " - 平均浏览: " + (avgViews.value() > 0 ? String.format("%.1f", avgViews.value()) : 0));
                });
        }
    }

    /**
     * 嵌套聚合
     */
    private void nestedAggregation() throws IOException {
        ResponsePrinter.printMethodInfo("nestedAggregation", "嵌套聚合");

        System.out.println("\n  1. 多级桶聚合 (作者 -> 标签):");
        SearchResponse<BlogDocument> response1 = client.search(s -> s
            .index(INDEX_NAME)
            .size(0)
            .aggregations("by_author", a -> a.terms(t -> t.field("author.keyword").size(10))
                .aggregations("by_tags", aa -> a.terms(tt -> tt.field("tags.keyword").size(5)))
            )
        , BlogDocument.class);

        if (response1.aggregations().containsKey("by_author")) {
            response1.aggregations().get("by_author").sterms().buckets().array()
                .forEach(authorBucket -> {
                    System.out.println("    作者: " + authorBucket.key().stringValue() + " (" + authorBucket.docCount() + " 篇)");
                    if (authorBucket.aggregations().containsKey("by_tags")) {
                        authorBucket.aggregations().get("by_tags").sterms().buckets().array()
                            .forEach(tagBucket -> System.out.println("      - " + tagBucket.key().stringValue() + ": " + tagBucket.docCount()));
                    }
                });
        }

        System.out.println("\n  2. 桶 + 指标聚合 (作者 -> 平均浏览量):");
        SearchResponse<BlogDocument> response2 = client.search(s -> s
            .index(INDEX_NAME)
            .size(0)
            .aggregations("by_author", a -> a.terms(t -> t.field("author.keyword").size(10))
                .aggregations("avg_views", aa -> aa.avg(avg -> avg.field("views")))
                .aggregations("max_views", aa -> aa.max(max -> max.field("views")))
            )
        , BlogDocument.class);

        if (response2.aggregations().containsKey("by_author")) {
            response2.aggregations().get("by_author").sterms().buckets().array()
                .forEach(authorBucket -> {
                    var avgViews = authorBucket.aggregations().get("avg_views").avg();
                    var maxViews = authorBucket.aggregations().get("max_views").max();
                    System.out.println("    " + authorBucket.key().stringValue() +
                        " - 平均浏览: " + (avgViews.value() > 0 ? avgViews.value() : 0) +
                        ", 最高浏览: " + (maxViews.value() > 0 ? maxViews.value() : 0));
                });
        }
    }

    /**
     * 打印聚合结果
     */
    private void printAggregationResults(Map<String, co.elastic.clients.elasticsearch._types.aggregations.Aggregate> aggs, String... names) {
        for (String name : names) {
            if (aggs.containsKey(name)) {
                System.out.println("    " + name + ":");
                var agg = aggs.get(name);
                if (agg.isAvg()) {
                    System.out.println("      值: " + agg.avg().value());
                } else if (agg.isSum()) {
                    System.out.println("      值: " + agg.sum().value());
                } else if (agg.isMax()) {
                    System.out.println("      值: " + agg.max().value());
                } else if (agg.isMin()) {
                    System.out.println("      值: " + agg.min().value());
                }
            }
        }
    }
}
