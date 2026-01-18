package com.shuai.elasticsearch.query;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.shuai.elasticsearch.config.ElasticsearchConfig;
import com.shuai.elasticsearch.model.BlogDocument;
import com.shuai.elasticsearch.util.ResponsePrinter;

import java.io.IOException;

/**
 * 评分函数演示类
 *
 * 模块概述
 * ----------
 * 本模块演示 Elasticsearch function_score 自定义评分功能。
 *
 * 核心内容
 * ----------
 *   - function_score: 自定义评分机制
 *   - 评分函数: script_score, random_score, field_value_factor
 *   - 评分模式: sum, avg, max, min, first
 *   - 权重调整: boost_mode, score_mode
 *
 * @author Shuai
 * @version 1.0
 * @see <a href="https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-function-score-query.html">Function Score Query</a>
 */
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
        ResponsePrinter.printMethodInfo("评分函数演示", "Elasticsearch function_score 功能");

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
     *
     * REST API:
     *   GET /blog/_search
     *   { "query": { "function_score": { "query": { "match": { "title": "Java" }}, "script_score": { "script": { "source": "Math.log(doc['views'].value + 1)" }}}}}
     */
    private void scriptScoreQuery() throws IOException {
        ResponsePrinter.printMethodInfo("scriptScoreQuery", "script_score 脚本评分");

        System.out.println("\n  1. 基本脚本评分 (浏览量对数加权):");
        System.out.println("    REST API: POST /blog/_search");
        System.out.println("    {");
        System.out.println("      \"query\": {");
        System.out.println("        \"function_score\": {");
        System.out.println("          \"query\": { \"match\": { \"title\": \"Java\" }},");
        System.out.println("          \"script_score\": {");
        System.out.println("            \"script\": { \"source\": \"Math.log(doc['views'].value + 1) * 0.1\" }");
        System.out.println("          }");
        System.out.println("        }");
        System.out.println("      }");
        System.out.println("    }");

        SearchResponse<BlogDocument> response1 = client.search(s -> s
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

        printBlogResultsWithScore(response1, "脚本评分结果");

        System.out.println("\n  2. 复杂脚本评分 (浏览量 + 时间因子):");
        System.out.println("    使用多个函数组合实现多因素评分");

        SearchResponse<BlogDocument> response2 = client.search(s -> s
            .index(INDEX_NAME)
            .query(q -> q.functionScore(fs -> fs
                .query(fq -> fq.match(m -> m.field("title").query("教程")))
                .functions(f -> f
                    .scriptScore(ss -> ss
                        .script(sc -> sc.source("Math.log(doc['views'].value + 1)"))
                    )
                )
                .scoreMode(co.elastic.clients.elasticsearch._types.query_dsl.FunctionScoreMode.Sum)
                .boostMode(co.elastic.clients.elasticsearch._types.query_dsl.FunctionBoostMode.Multiply)
            ))
        , BlogDocument.class);

        printBlogResultsWithScore(response2, "综合评分结果");
    }

    /**
     * random_score - 随机评分
     *
     * REST API:
     *   GET /blog/_search
     *   { "query": { "function_score": { "query": { "match_all": {} }, "random_score": { "seed\": \"123\" }}}}
     */
    private void randomScoreQuery() throws IOException {
        ResponsePrinter.printMethodInfo("randomScoreQuery", "random_score 随机评分");

        System.out.println("\n  1. 随机评分 (探索性搜索):");
        System.out.println("    使用 random_score 实现探索性搜索，每次返回结果顺序不同");

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

        System.out.println("\n  2. 随机评分 + 过滤 (指定作者):");
        SearchResponse<BlogDocument> response2 = client.search(s -> s
            .index(INDEX_NAME)
            .query(q -> q.functionScore(fs -> fs
                .query(fq -> fq.term(t -> t.field("author.keyword").value("张三")))
                .functions(f -> f
                    .randomScore(rs -> rs.seed(String.valueOf(System.currentTimeMillis())).field("id"))
                )
                .scoreMode(co.elastic.clients.elasticsearch._types.query_dsl.FunctionScoreMode.Sum)
            ))
        , BlogDocument.class);

        printBlogResultsWithScore(response2, "过滤+随机结果");
    }

    /**
     * field_value_factor - 字段值因子
     *
     * REST API:
     *   GET /blog/_search
     *   { "query": { "function_score": { "query": { "match": { "title": "Java" }}, "field_value_factor\": { "field\": \"views\", \"factor\": 0.1, \"modifier\": \"log1p\", \"missing\": 1 }}}}
     */
    private void fieldValueFactorQuery() throws IOException {
        ResponsePrinter.printMethodInfo("fieldValueFactorQuery", "field_value_factor 字段值因子");

        System.out.println("\n  1. 字段值因子 (浏览量加权):");
        System.out.println("    使用 field_value_factor 直接基于字段值调整评分");

        SearchResponse<BlogDocument> response1 = client.search(s -> s
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

        printBlogResultsWithScore(response1, "浏览量加权结果");

        System.out.println("\n  2. 字段值因子 + modifier (sqrt 平方根):");
        SearchResponse<BlogDocument> response2 = client.search(s -> s
            .index(INDEX_NAME)
            .query(q -> q.functionScore(fs -> fs
                .query(fq -> fq.matchAll(m -> m))
                .functions(f -> f
                    .fieldValueFactor(fvf -> fvf
                        .field("views")
                        .factor(0.1)
                        .modifier(co.elastic.clients.elasticsearch._types.query_dsl.FieldValueFactorModifier.Sqrt)
                        .missing(1.0)
                    )
                )
                .scoreMode(co.elastic.clients.elasticsearch._types.query_dsl.FunctionScoreMode.Sum)
            ))
        , BlogDocument.class);

        printBlogResultsWithScore(response2, "平方根加权结果");
    }

    /**
     * 衰减函数 - 时间衰减、地理位置衰减
     *
     * 说明: 衰减函数在 ES Java Client 8.x 中需要通过 FunctionScore 数组实现
     * 这里演示使用权重(weight)模拟简单的衰减效果
     */
    private void decayFunctionQuery() throws IOException {
        ResponsePrinter.printMethodInfo("decayFunctionQuery", "decay 衰减函数");

        System.out.println("\n  1. gauss 高斯衰减 (模拟):");
        System.out.println("    衰减函数用于实现时间衰减、地理位置衰减等场景");
        System.out.println("    实际场景: 越近期的文档评分越高");

        // 使用 script_score 模拟衰减效果
        try {
            SearchResponse<BlogDocument> response1 = client.search(s -> s
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

            printBlogResultsWithScore(response1, "衰减模拟结果");
        } catch (Exception e) {
            System.out.println("    [跳过] 查询执行失败: " + e.getMessage());
        }

        System.out.println("\n  2. linear 线性衰减 (模拟):");
        System.out.println("    线性衰减: 评分随距离线性下降");

        // 使用不同的加权模拟
        try {
            SearchResponse<BlogDocument> response2 = client.search(s -> s
                .index(INDEX_NAME)
                .query(q -> q.functionScore(fs -> fs
                    .query(fq -> fq.matchAll(m -> m))
                    .functions(f -> f
                        .scriptScore(ss -> ss
                            .script(sc -> sc.source("_score * Math.sqrt(doc['views'].value + 1)"))
                        )
                    )
                    .scoreMode(co.elastic.clients.elasticsearch._types.query_dsl.FunctionScoreMode.Max)
                ))
            , BlogDocument.class);

            printBlogResultsWithScore(response2, "线性衰减模拟结果");
        } catch (Exception e) {
            System.out.println("    [跳过] 查询执行失败: " + e.getMessage());
        }
    }

    /**
     * 组合评分 - 多函数组合使用
     */
    private void combinedFunctionScore() throws IOException {
        ResponsePrinter.printMethodInfo("combinedFunctionScore", "组合评分函数");

        System.out.println("\n  1. 多函数组合评分 (浏览量 + 随机):");
        System.out.println("    将多个评分函数组合使用，实现复杂的评分逻辑");

        try {
            // 使用 field_value_factor + weight 实现组合评分
            SearchResponse<BlogDocument> response1 = client.search(s -> s
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

            printBlogResultsWithScore(response1, "组合评分结果");
        } catch (Exception e) {
            System.out.println("    [跳过] 查询执行失败: " + e.getMessage());
        }

        System.out.println("\n  2. 过滤条件下的评分函数:");
        System.out.println("    在过滤条件基础上应用评分函数");

        try {
            SearchResponse<BlogDocument> response2 = client.search(s -> s
                .index(INDEX_NAME)
                .query(q -> q.functionScore(fs -> fs
                    .query(fq -> fq.bool(b -> b
                        .must(m -> m.match(mt -> mt.field("title").query("教程")))
                        .filter(f -> f.term(t -> t.field("status").value("published")))
                    ))
                    .functions(f -> f
                        .fieldValueFactor(fvf -> fvf
                            .field("views")
                            .factor(0.02)
                            .modifier(co.elastic.clients.elasticsearch._types.query_dsl.FieldValueFactorModifier.Log1p)
                        )
                    )
                    .scoreMode(co.elastic.clients.elasticsearch._types.query_dsl.FunctionScoreMode.Sum)
                    .boostMode(co.elastic.clients.elasticsearch._types.query_dsl.FunctionBoostMode.Multiply)
                ))
            , BlogDocument.class);

            printBlogResultsWithScore(response2, "过滤+评分结果");
        } catch (Exception e) {
            System.out.println("    [跳过] 查询执行失败: " + e.getMessage());
        }
    }

    /**
     * 打印博客查询结果（带评分）
     */
    private void printBlogResultsWithScore(SearchResponse<BlogDocument> response, String description) {
        long total = response.hits().total() != null ? response.hits().total().value() : 0;
        System.out.println("    " + description + "，命中: " + total);

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
