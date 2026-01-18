package com.shuai.elasticsearch.fulltext;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Highlight;
import co.elastic.clients.elasticsearch.core.search.HighlightField;
import com.shuai.elasticsearch.config.ElasticsearchConfig;
import com.shuai.elasticsearch.model.BlogDocument;
import com.shuai.elasticsearch.util.ResponsePrinter;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 全文检索演示类
 *
 * 模块概述
 * ----------
 * 本模块演示 Elasticsearch 全文检索的高级功能，
 * 包括分词器配置、高亮显示、相关性排序等。
 *
 * 核心内容
 * ----------
 *   - 分词器配置: IK 分词器、自定义分词
 *   - 高亮显示: 匹配文本高亮
 *   - 相关性排序: _score、function_score
 *   - 查询建议: Did you mean、Completion Suggester
 *
 * @author Shuai
 * @version 1.0
 * @see <a href="https://www.elastic.co/guide/en/elasticsearch/reference/current/full-text-queries.html">Full Text Queries</a>
 */
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
        ResponsePrinter.printMethodInfo("全文检索演示", "Elasticsearch 全文检索高级功能");

        analyzerConfig();
        highlightDemo();
        relevanceScoring();
    }

    /**
     * 分词器配置说明
     */
    private void analyzerConfig() {
        ResponsePrinter.printMethodInfo("analyzerConfig", "分词器配置");

        System.out.println("  内置分词器:");
        System.out.println("    - standard: 默认分词器，按单词边界切分");
        System.out.println("    - simple: 简单分词器，只处理字母");
        System.out.println("    - whitespace: 按空格切分");
        System.out.println("    - keyword: 不分词，整个作为整体");

        System.out.println("\n  IK 分词器配置:");
        System.out.println("    安装: ./bin/elasticsearch-plugin install analysis-ik");
        System.out.println("    ik_max_word: 最细粒度分词（覆盖面广）");
        System.out.println("    ik_smart: 智能分词（覆盖面精）");

        System.out.println("\n  Java Client 创建带分词器的索引:");
        System.out.println("    client.indices().create(c -> c");
        System.out.println("      .index(INDEX_NAME)");
        System.out.println("      .settings(s -> s");
        System.out.println("        .analysis(a -> a");
        System.out.println("          .analyzer(\"ik_analyzer\", an -> an");
        System.out.println("            .custom(ct -> ct");
        System.out.println("              .tokenizer(\"ik_max_word\")");
        System.out.println("            )");
        System.out.println("          )");
        System.out.println("        )");
        System.out.println("      )");
        System.out.println("    )");
    }

    /**
     * 高亮显示演示
     *
     * REST API:
     *   GET /blog/_search
     *   { "query": { "match": { "content": "Java" }}, "highlight": { "fields": { "content": {} }}}
     */
    private void highlightDemo() throws IOException {
        ResponsePrinter.printMethodInfo("highlightDemo", "高亮显示");

        // 1. 基本高亮
        System.out.println("\n  1. 基本高亮:");
        SearchResponse<BlogDocument> response1 = client.search(s -> s
            .index(INDEX_NAME)
            .query(q -> q.match(m -> m.field("content").query("Java")))
            .highlight(h -> h
                .fields("content", HighlightField.of(hf -> hf))
            )
        , BlogDocument.class);

        printHighlightResults(response1, "content");

        // 2. 自定义高亮标签
        System.out.println("\n  2. 自定义高亮标签:");
        SearchResponse<BlogDocument> response2 = client.search(s -> s
            .index(INDEX_NAME)
            .query(q -> q.match(m -> m.field("content").query("教程")))
            .highlight(h -> h
                .preTags("<em>")
                .postTags("</em>")
                .fields("content", HighlightField.of(hf -> hf
                    .fragmentSize(150)
                    .numberOfFragments(3)
                ))
            )
        , BlogDocument.class);

        printHighlightResults(response2, "content");

        // 3. 多字段高亮
        System.out.println("\n  3. 多字段高亮:");
        SearchResponse<BlogDocument> response3 = client.search(s -> s
            .index(INDEX_NAME)
            .query(q -> q.multiMatch(mm -> mm
                .query("Java 教程")
                .fields(Arrays.asList("title", "content"))
            ))
            .highlight(h -> h
                .fields("title", HighlightField.of(hf -> hf.numberOfFragments(1)))
                .fields("content", HighlightField.of(hf -> hf.numberOfFragments(3)))
            )
        , BlogDocument.class);

        printMultiFieldHighlightResults(response3);
    }

    /**
     * 相关性排序演示
     *
     * REST API:
     *   GET /blog/_search
     *   { "query": { "multi_match": { "query": "Java 教程", "fields": ["title^2", "content"] }}}
     */
    private void relevanceScoring() throws IOException {
        ResponsePrinter.printMethodInfo("relevanceScoring", "相关性排序");

        // 1. 多字段匹配评分（带权重）
        System.out.println("\n  1. multi_match 多字段匹配（title 权重 2）:");
        SearchResponse<BlogDocument> response1 = client.search(s -> s
            .index(INDEX_NAME)
            .query(q -> q.multiMatch(mm -> mm
                .query("Java 教程")
                .fields(Arrays.asList("title^2", "content"))
                .type(co.elastic.clients.elasticsearch._types.query_dsl.TextQueryType.BestFields)
            ))
        , BlogDocument.class);

        printRelevanceResults(response1, "多字段匹配");

        // 2. 按相关性评分排序
        System.out.println("\n  2. 按 _score 排序（默认降序）:");
        SearchResponse<BlogDocument> response2 = client.search(s -> s
            .index(INDEX_NAME)
            .query(q -> q.match(m -> m.field("content").query("教程")))
            .sort(so -> so.score(sc -> sc.order(co.elastic.clients.elasticsearch._types.SortOrder.Desc)))
        , BlogDocument.class);

        printRelevanceResults(response2, "按评分排序");

        // 3. 综合排序（评分 + 字段）
        System.out.println("\n  3. 综合排序（评分降序 + 浏览量降序）:");
        SearchResponse<BlogDocument> response3 = client.search(s -> s
            .index(INDEX_NAME)
            .query(q -> q.match(m -> m.field("content").query("教程")))
            .sort(Arrays.asList(
                co.elastic.clients.elasticsearch._types.SortOptions.of(so -> so.score(sc -> sc.order(co.elastic.clients.elasticsearch._types.SortOrder.Desc))),
                co.elastic.clients.elasticsearch._types.SortOptions.of(so -> so.field(f -> f.field("views").order(co.elastic.clients.elasticsearch._types.SortOrder.Desc)))
            ))
        , BlogDocument.class);

        printRelevanceResults(response3, "综合排序");
    }

    /**
     * 打印高亮结果
     */
    private void printHighlightResults(SearchResponse<BlogDocument> response, String field) {
        long total = response.hits().total() != null ? response.hits().total().value() : 0;
        System.out.println("    命中: " + total);

        for (var hit : response.hits().hits()) {
            System.out.println("    [" + hit.id() + "] 得分: " + hit.score());
            if (hit.highlight() != null && hit.highlight().containsKey(field)) {
                System.out.println("    高亮片段: " + hit.highlight().get(field));
            }
        }
    }

    /**
     * 打印多字段高亮结果
     */
    private void printMultiFieldHighlightResults(SearchResponse<BlogDocument> response) {
        long total = response.hits().total() != null ? response.hits().total().value() : 0;
        System.out.println("    命中: " + total);

        for (var hit : response.hits().hits()) {
            System.out.println("    [" + hit.id() + "] 得分: " + hit.score());
            if (hit.highlight() != null) {
                hit.highlight().forEach((field, fragments) ->
                    System.out.println("      " + field + ": " + fragments)
                );
            }
        }
    }

    /**
     * 打印相关性结果
     */
    private void printRelevanceResults(SearchResponse<BlogDocument> response, String description) {
        long total = response.hits().total() != null ? response.hits().total().value() : 0;
        System.out.println("    描述: " + description + ", 命中: " + total);

        for (var hit : response.hits().hits()) {
            BlogDocument doc = hit.source();
            System.out.println("    [" + hit.id() + "] " + doc.getTitle());
            System.out.println("      评分: " + hit.score() + ", 浏览: " + doc.getViews());
        }
    }
}
