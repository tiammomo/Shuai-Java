package com.shuai.elasticsearch.query;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.shuai.elasticsearch.config.ElasticsearchConfig;
import com.shuai.elasticsearch.model.BlogDocument;
import com.shuai.elasticsearch.util.ResponsePrinter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * 查询演示类
 *
 * 模块概述
 * ----------
 * 本模块演示 Elasticsearch 各种查询类型的使用方法。
 *
 * 核心内容
 * ----------
 *   - 全文检索查询: match、match_phrase、multi_match
 *   - 精确匹配查询: term、terms、range
 *   - 复合查询: bool、function_score
 *   - 过滤查询: filter、constant_score
 *
 * @author Shuai
 * @version 1.0
 * @see <a href="https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl.html">Query DSL</a>
 */
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
        ResponsePrinter.printMethodInfo("查询演示", "Elasticsearch 各种查询类型");

        queryTypesOverview();
        fullTextQuery();
        termLevelQuery();
        rangeQuery();
        compoundQuery();
    }

    /**
     * 查询类型概述
     */
    private void queryTypesOverview() {
        ResponsePrinter.printMethodInfo("queryTypesOverview", "查询类型分类");

        System.out.println("  查询类型分类:");
        System.out.println("    - Full-text Query (全文检索) - 分词后匹配");
        System.out.println("    - Term-level Query (精确匹配) - 精确值匹配");
        System.out.println("    - Compound Query (复合查询) - 组合多个查询");
        System.out.println("    - Geo Query (地理查询) - 位置相关查询");
        System.out.println("    - Nested Query (嵌套查询) - 嵌套文档查询");
    }

    /**
     * 全文检索查询
     *
     * REST API:
     *   GET /blog/_search
     *   { "query": { "match": { "content": "Java 教程" }}}
     */
    private void fullTextQuery() throws IOException {
        ResponsePrinter.printMethodInfo("fullTextQuery", "全文检索查询");

        // 1. match - 标准全文检索
        System.out.println("\n  1. match 查询:");
        SearchResponse<BlogDocument> response1 = client.search(s -> s
            .index(INDEX_NAME)
            .query(q -> q.match(m -> m.field("content").query("Java 教程")))
        , BlogDocument.class);
        printSearchResults(response1, "匹配 'Java 教程' 的文档");

        // 2. match_phrase - 短语匹配
        System.out.println("\n  2. match_phrase 查询:");
        SearchResponse<BlogDocument> response2 = client.search(s -> s
            .index(INDEX_NAME)
            .query(q -> q.matchPhrase(mp -> mp.field("content").query("Java 教程")))
        , BlogDocument.class);
        printSearchResults(response2, "短语 'Java 教程'");

        // 3. multi_match - 多字段匹配
        System.out.println("\n  3. multi_match 查询:");
        SearchResponse<BlogDocument> response3 = client.search(s -> s
            .index(INDEX_NAME)
            .query(q -> q.multiMatch(mm -> mm
                .query("Python")
                .fields(Arrays.asList("title", "content"))
            ))
        , BlogDocument.class);
        printSearchResults(response3, "多字段匹配 'Python'");

        // 4. query_string - 查询字符串
        System.out.println("\n  4. query_string 查询:");
        SearchResponse<BlogDocument> response4 = client.search(s -> s
            .index(INDEX_NAME)
            .query(q -> q.queryString(qs -> qs
                .query("(Java AND 教程) OR (Python)")
            ))
        , BlogDocument.class);
        printSearchResults(response4, "查询字符串 '(Java AND 教程) OR (Python)'");
    }

    /**
     * 精确匹配查询
     *
     * REST API:
     *   GET /blog/_search
     *   { "query": { "term": { "status": "published" }}}
     */
    private void termLevelQuery() throws IOException {
        ResponsePrinter.printMethodInfo("termLevelQuery", "精确匹配查询");

        // 1. term - 单词精确匹配
        System.out.println("\n  1. term 查询:");
        SearchResponse<BlogDocument> response1 = client.search(s -> s
            .index(INDEX_NAME)
            .query(q -> q.term(t -> t.field("status").value("published")))
        , BlogDocument.class);
        printSearchResults(response1, "status = published");

        // 2. terms - 多值精确匹配
        System.out.println("\n  2. terms 查询:");
        SearchResponse<BlogDocument> response2 = client.search(s -> s
            .index(INDEX_NAME)
            .query(q -> q.terms(t -> t
                .field("tags.keyword")
                .terms(tt -> tt.value(Arrays.asList(
                    co.elastic.clients.elasticsearch._types.FieldValue.of("java"),
                    co.elastic.clients.elasticsearch._types.FieldValue.of("python")
                )))
            ))
        , BlogDocument.class);
        printSearchResults(response2, "tags 包含 java 或 python");

        // 3. exists - 字段存在查询
        System.out.println("\n  3. exists 查询:");
        SearchResponse<BlogDocument> response3 = client.search(s -> s
            .index(INDEX_NAME)
            .query(q -> q.exists(e -> e.field("author")))
        , BlogDocument.class);
        printSearchResults(response3, "author 字段存在");

        // 4. prefix - 前缀匹配
        System.out.println("\n  4. prefix 查询:");
        SearchResponse<BlogDocument> response4 = client.search(s -> s
            .index(INDEX_NAME)
            .query(q -> q.prefix(p -> p.field("title").value("Java")))
        , BlogDocument.class);
        printSearchResults(response4, "title 前缀为 'Java'");

        // 5. wildcard - 通配符匹配
        System.out.println("\n  5. wildcard 查询:");
        SearchResponse<BlogDocument> response5 = client.search(s -> s
            .index(INDEX_NAME)
            .query(q -> q.wildcard(w -> w.field("title").value("J*a")))
        , BlogDocument.class);
        printSearchResults(response5, "title 匹配 'J*a'");
    }

    /**
     * 范围查询
     *
     * REST API:
     *   GET /blog/_search
     *   { "query": { "range": { "views": { "gte": 100, "lte": 500 }}}}
     */
    private void rangeQuery() throws IOException {
        ResponsePrinter.printMethodInfo("rangeQuery", "范围查询");

        // 1. 数值范围查询
        System.out.println("\n  1. 数值范围查询:");
        SearchResponse<BlogDocument> response1 = client.search(s -> s
            .index(INDEX_NAME)
            .query(q -> q.range(r -> r
                .number(n -> n.field("views").gte(100d).lte(500d))
            ))
        , BlogDocument.class);
        printSearchResults(response1, "views >= 100 AND views <= 500");

        // 2. 日期范围查询
        System.out.println("\n  2. 日期范围查询:");
        SearchResponse<BlogDocument> response2 = client.search(s -> s
            .index(INDEX_NAME)
            .query(q -> q.range(r -> r
                .date(d -> d.field("createTime")
                    .gte("2024-01-15")
                    .lte("2024-01-18"))
            ))
        , BlogDocument.class);
        printSearchResults(response2, "createTime 在 2024-01-15 到 2024-01-18 之间");
    }

    /**
     * 复合查询
     *
     * REST API:
     *   GET /blog/_search
     *   { "query": { "bool": { "must": [...], "filter": [...] }}}
     */
    private void compoundQuery() throws IOException {
        ResponsePrinter.printMethodInfo("compoundQuery", "复合查询");

        // 1. bool 查询 - must + filter + should
        System.out.println("\n  1. bool 查询:");
        SearchResponse<BlogDocument> response1 = client.search(s -> s
            .index(INDEX_NAME)
            .query(q -> q.bool(b -> b
                .must(m -> m.match(mt -> mt.field("title").query("Java")))
                .filter(f -> f.term(t -> t.field("status").value("published")))
                .should(sh -> sh.range(r -> r.number(n -> n.field("views").gte(100d))))
                .minimumShouldMatch("1")
            ))
        , BlogDocument.class);
        printSearchResults(response1, "title 包含 Java AND status=published");

        // 2. bool 查询 - must_not
        System.out.println("\n  2. bool must_not 查询:");
        SearchResponse<BlogDocument> response2 = client.search(s -> s
            .index(INDEX_NAME)
            .query(q -> q.bool(b -> b
                .must(m -> m.match(mt -> mt.field("content").query("教程")))
                .mustNot(mn -> mn.term(t -> t.field("status").value("draft")))
            ))
        , BlogDocument.class);
        printSearchResults(response2, "content 包含教程 且 status != draft");

        // 3. constant_score - 常量评分
        System.out.println("\n  3. constant_score 查询:");
        SearchResponse<BlogDocument> response3 = client.search(s -> s
            .index(INDEX_NAME)
            .query(q -> q.constantScore(cs -> cs
                .filter(f -> f.term(t -> t.field("status").value("published")))
                .boost(1.5f)
            ))
        , BlogDocument.class);
        printSearchResults(response3, "status=published (常量评分)");

        // 4. boosting - 降级查询
        System.out.println("\n  4. boosting 查询:");
        SearchResponse<BlogDocument> response4 = client.search(s -> s
            .index(INDEX_NAME)
            .query(q -> q.boosting(b -> b
                .positive(p -> p.match(mt -> mt.field("title").query("教程")))
                .negative(n -> n.match(mt -> mt.field("content").query("广告")))
                .negativeBoost(0.2f)
            ))
        , BlogDocument.class);
        printSearchResults(response4, "教程相关，降权广告内容");
    }

    /**
     * 打印搜索结果
     */
    private void printSearchResults(SearchResponse<BlogDocument> response, String description) {
        long total = response.hits().total() != null ? response.hits().total().value() : 0;
        System.out.println("    描述: " + description);
        System.out.println("    命中: " + total);

        for (Hit<BlogDocument> hit : response.hits().hits()) {
            BlogDocument doc = hit.source();
            System.out.println("    - [" + hit.id() + "] " + doc.getTitle() + " (得分: " + hit.score() + ")");
        }
    }
}
