package com.shuai.elasticsearch.util;

import co.elastic.clients.elasticsearch._types.aggregations.Aggregate;
import co.elastic.clients.elasticsearch._types.aggregations.StringTermsAggregate;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.TotalHits;

import java.util.List;
import java.util.Map;

/**
 * 响应格式化输出工具类
 *
 * 功能说明
 * ----------
 * 提供各种 Elasticsearch 响应的格式化输出方法，
 * 方便在控制台查看查询结果。
 *
 * @author Shuai
 * @version 1.0
 */
public class ResponsePrinter {

    /**
     * 打印搜索响应结果
     */
    public static void printSearchResponse(co.elastic.clients.elasticsearch.core.SearchResponse<?> response) {
        TotalHits totalHits = response.hits().total();
        long total = totalHits != null ? totalHits.value() : 0;
        System.out.println("========================================");
        System.out.println("总命中数: " + total);
        System.out.println("----------------------------------------");

        List<? extends Hit<?>> hits = response.hits().hits();
        if (hits.isEmpty()) {
            System.out.println("未找到匹配的文档");
        } else {
            for (int i = 0; i < hits.size(); i++) {
                Hit<?> hit = hits.get(i);
                System.out.println("\n结果 " + (i + 1) + ":");
                System.out.println("  ID: " + hit.id());
                System.out.println("  得分: " + hit.score());
                if (hit.source() != null) {
                    System.out.println("  数据: " + hit.source());
                }
            }
        }
        System.out.println("========================================\n");
    }

    /**
     * 打印聚合结果（简化版）
     */
    public static void printAggregationResponse(Map<String, Aggregate> aggs, String... aggNames) {
        System.out.println("========================================");
        System.out.println("聚合结果:");
        System.out.println("----------------------------------------");

        if (aggs == null || aggs.isEmpty()) {
            System.out.println("无聚合结果");
        } else {
            for (String aggName : aggNames) {
                Aggregate aggregate = aggs.get(aggName);
                if (aggregate != null) {
                    System.out.println("\n聚合 [" + aggName + "]:");
                    printAggregateSimple(aggregate, "  ");
                }
            }
        }
        System.out.println("========================================\n");
    }

    /**
     * 打印单个聚合结果（简化版）
     */
    private static void printAggregateSimple(Aggregate aggregate, String indent) {
        if (aggregate.isSterms()) {
            StringTermsAggregate sterms = aggregate.sterms();
            sterms.buckets().array().forEach(bucket ->
                System.out.println(indent + bucket.key().stringValue() + ": " + bucket.docCount())
            );
        } else if (aggregate.isAvg()) {
            System.out.println(indent + "平均值: " + aggregate.avg().value());
        } else if (aggregate.isSum()) {
            System.out.println(indent + "求和: " + aggregate.sum().value());
        } else if (aggregate.isMin()) {
            System.out.println(indent + "最小值: " + aggregate.min().value());
        } else if (aggregate.isMax()) {
            System.out.println(indent + "最大值: " + aggregate.max().value());
        } else if (aggregate.isStats()) {
            var stats = aggregate.stats();
            System.out.println(indent + "统计: count=" + stats.count() +
                    ", min=" + stats.min() +
                    ", max=" + stats.max() +
                    ", avg=" + stats.avg() +
                    ", sum=" + stats.sum());
        } else if (aggregate.isCardinality()) {
            System.out.println(indent + "基数: " + aggregate.cardinality().value());
        } else {
            System.out.println(indent + "聚合类型: " + aggregate.getClass().getSimpleName());
        }
    }

    /**
     * 打印 Bulk 操作结果
     */
    public static void printBulkResponse(co.elastic.clients.elasticsearch.core.BulkResponse response) {
        System.out.println("========================================");
        System.out.println("Bulk 操作结果:");
        System.out.println("  耗时: " + response.took());
        System.out.println("  错误: " + response.errors());
        System.out.println("========================================\n");
    }

    /**
     * 打印索引信息
     */
    public static void printIndexInfo(String indexName, Map<String, Object> info) {
        System.out.println("========================================");
        System.out.println("索引 [" + indexName + "] 信息:");
        System.out.println("----------------------------------------");
        info.forEach((key, value) ->
            System.out.println("  " + key + ": " + value)
        );
        System.out.println("========================================\n");
    }

    /**
     * 打印集群健康状态
     */
    public static void printClusterHealth(String status, int numberOfNodes,
                                          int numberOfDataNodes, long activeShards) {
        System.out.println("========================================");
        System.out.println("集群健康状态:");
        System.out.println("  状态: " + status);
        System.out.println("  节点数: " + numberOfNodes);
        System.out.println("  数据节点数: " + numberOfDataNodes);
        System.out.println("  活跃分片数: " + activeShards);
        System.out.println("========================================\n");
    }

    /**
     * 打印分隔线
     */
    public static void printDivider(String title) {
        System.out.println("\n" + "=".repeat(50));
        if (title != null && !title.isEmpty()) {
            System.out.println("  " + title);
            System.out.println("=".repeat(50));
        }
    }

    /**
     * 打印方法执行信息
     */
    public static void printMethodInfo(String methodName, String description) {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("  " + methodName);
        System.out.println("  " + description);
        System.out.println("=".repeat(50));
    }

    /**
     * 打印执行结果
     */
    public static void printResult(String operation, String result) {
        System.out.println("\n执行 [" + operation + "]: " + result);
    }

    /**
     * 打印操作响应
     */
    public static void printResponse(String operation, String result) {
        System.out.println("\n[" + operation + "] " + result);
    }
}
