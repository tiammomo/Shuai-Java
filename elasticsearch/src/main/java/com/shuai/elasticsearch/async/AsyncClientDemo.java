package com.shuai.elasticsearch.async;

import co.elastic.clients.elasticsearch.ElasticsearchAsyncClient;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.bulk.BulkResponseItem;
import com.shuai.elasticsearch.config.ElasticsearchConfig;
import com.shuai.elasticsearch.model.BlogDocument;
import com.shuai.elasticsearch.util.ResponsePrinter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

/**
 * 异步客户端演示类
 *
 * 模块概述
 * ----------
 * 本模块演示 Elasticsearch 异步 Java Client 的使用方法。
 *
 * 核心内容
 * ----------
 *   - ElasticsearchAsyncClient: 异步客户端
 *   - CompletableFuture: Java 并发编程
 *   - 非阻塞操作: 搜索、索引、批量操作
 *   - 超时控制: Future 超时设置
 *
 * 应用场景
 * ----------
 *   - 高并发场景下的异步请求处理
 *   - 批量操作时不阻塞主线程
 *   - 与响应式编程框架集成
 *
 * @author Shuai
 * @version 1.0
 * @see <a href="https://www.elastic.co/guide/en/elasticsearch/client/java-api-client/current/java-async-intro.html">Async Client</a>
 */
public class AsyncClientDemo {

    private final ElasticsearchClient syncClient;
    private final ElasticsearchAsyncClient asyncClient;
    private static final String INDEX_NAME = "blog";

    public AsyncClientDemo() {
        this.syncClient = ElasticsearchConfig.getClient();
        this.asyncClient = ElasticsearchConfig.getAsyncClient();
    }

    /**
     * 运行所有异步客户端演示
     */
    public void runAllDemos() throws IOException, InterruptedException, ExecutionException, TimeoutException {
        ResponsePrinter.printMethodInfo("异步客户端演示", "Elasticsearch 异步 Java Client 功能");

        asyncOverview();
        asyncIndexDocument();
        asyncSearch();
        asyncBulkOperation();
        parallelOperations();
        timeoutHandling();
    }

    /**
     * 异步客户端概述
     */
    private void asyncOverview() {
        ResponsePrinter.printMethodInfo("asyncOverview", "异步客户端概述");

        System.out.println("  同步 vs 异步:");
        System.out.println("    - 同步: 请求阻塞等待响应");
        System.out.println("    - 异步: 返回 CompletableFuture，非阻塞");

        System.out.println("\n  ElasticsearchAsyncClient:");
        System.out.println("    - 所有方法返回 CompletableFuture");
        System.out.println("    - 支持链式调用和组合操作");
        System.out.println("    - 可与 Java 并发框架无缝集成");

        System.out.println("\n  异步操作优势:");
        System.out.println("    - 提高吞吐量");
        System.out.println("    - 减少线程阻塞");
        System.out.println("    - 支持并行请求");
    }

    /**
     * 异步索引文档
     */
    private void asyncIndexDocument() throws InterruptedException, ExecutionException {
        ResponsePrinter.printMethodInfo("asyncIndexDocument", "异步索引文档");

        BlogDocument doc = BlogDocument.builder()
            .id("async-1")
            .title("异步测试文档")
            .content("这是一篇通过异步客户端插入的测试文档")
            .author("异步测试")
            .tags(List.of("async", "test"))
            .views(100)
            .status("published")
            .createTime("2024-01-17")
            .build();

        System.out.println("\n  1. 异步索引文档 (CompletableFuture):");

        CompletableFuture<IndexResponse> future = asyncClient.index(i -> i
            .index(INDEX_NAME)
            .id(doc.getId())
            .document(doc)
        );

        // 阻塞等待结果
        IndexResponse response = future.get();
        System.out.println("    索引结果: " + response.result());

        System.out.println("\n  2. 非阻塞方式处理结果:");
        future.thenAccept(r -> System.out.println("    文档已索引: " + r.result()));
        System.out.println("    (异步处理中...)");
    }

    /**
     * 异步搜索
     */
    private void asyncSearch() throws InterruptedException, ExecutionException {
        ResponsePrinter.printMethodInfo("asyncSearch", "异步搜索");

        System.out.println("\n  1. 基本异步搜索:");

        CompletableFuture<SearchResponse<BlogDocument>> future = asyncClient.search(s -> s
            .index(INDEX_NAME)
            .query(q -> q.match(m -> m.field("title").query("Java")))
            .size(5)
        , BlogDocument.class);

        SearchResponse<BlogDocument> response = future.get();
        long total = response.hits().total() != null ? response.hits().total().value() : 0;
        System.out.println("    命中: " + total);

        for (var hit : response.hits().hits()) {
            if (hit.source() != null) {
                System.out.println("    - " + hit.source().getTitle());
            }
        }

        System.out.println("\n  2. 链式异步处理:");
        asyncClient.search(s -> s.index(INDEX_NAME).query(q -> q.matchAll(m -> m)).size(3), BlogDocument.class)
            .thenApply(SearchResponse::hits)
            .thenAccept(hits -> {
                System.out.println("    搜索结果数: " + hits.total().value());
                hits.hits().forEach(h -> {
                    if (h.source() != null) {
                        System.out.println("    - " + h.source().getTitle());
                    }
                });
            })
            .get();
    }

    /**
     * 异步批量操作
     */
    private void asyncBulkOperation() throws InterruptedException, ExecutionException {
        ResponsePrinter.printMethodInfo("asyncBulkOperation", "异步批量操作");

        System.out.println("\n    批量插入测试数据...");

        BulkRequest.Builder bulkBuilder = new BulkRequest.Builder();
        for (int i = 0; i < 10; i++) {
            BlogDocument doc = BlogDocument.builder()
                .id("bulk-async-" + i)
                .title("批量异步文档 " + i)
                .content("这是批量插入的第 " + i + " 篇文档")
                .author("批量作者")
                .tags(List.of("bulk", "async"))
                .views(50 + i * 10)
                .status(i % 2 == 0 ? "published" : "draft")
                .createTime("2024-01-17")
                .build();

            bulkBuilder.operations(op -> op
                .index(idx -> idx
                    .index(INDEX_NAME)
                    .id(doc.getId())
                    .document(doc)
                )
            );
        }

        System.out.println("\n  1. 异步批量操作:");
        CompletableFuture<BulkResponse> future = asyncClient.bulk(bulkBuilder.build());

        BulkResponse response = future.get();
        if (response.errors()) {
            System.out.println("    部分文档处理失败:");
            for (BulkResponseItem item : response.items()) {
                if (item.error() != null) {
                    System.out.println("      - " + item.id() + ": " + item.error().reason());
                }
            }
        } else {
            System.out.println("    批量操作成功: " + response.items().size() + " 条");
        }

        System.out.println("\n  2. 异步处理批量结果:");
        asyncClient.bulk(bulkBuilder.build())
            .thenAccept(r -> System.out.println("    完成: " + r.items().size() + " 条操作"))
            .exceptionally(e -> {
                System.out.println("    错误: " + e.getMessage());
                return null;
            })
            .get();
    }

    /**
     * 并行操作
     */
    private void parallelOperations() throws InterruptedException, ExecutionException {
        ResponsePrinter.printMethodInfo("parallelOperations", "并行操作");

        System.out.println("\n  1. 并行执行多个搜索请求:");

        CompletableFuture<SearchResponse<BlogDocument>> future1 = asyncClient.search(s -> s
            .index(INDEX_NAME)
            .query(q -> q.term(t -> t.field("author.keyword").value("张三")))
            .size(5)
        , BlogDocument.class);

        CompletableFuture<SearchResponse<BlogDocument>> future2 = asyncClient.search(s -> s
            .index(INDEX_NAME)
            .query(q -> q.term(t -> t.field("tags.keyword").value("Java")))
            .size(5)
        , BlogDocument.class);

        CompletableFuture<SearchResponse<BlogDocument>> future3 = asyncClient.search(s -> s
            .index(INDEX_NAME)
            .query(q -> q.range(r -> r.number(n -> n.field("views").gte(300.0))))
            .size(5)
        , BlogDocument.class);

        // 等待所有请求完成
        CompletableFuture.allOf(future1, future2, future3).get();

        System.out.println("    搜索请求 1 (张三): " + future1.get().hits().total().value() + " 条");
        System.out.println("    搜索请求 2 (Java标签): " + future2.get().hits().total().value() + " 条");
        System.out.println("    搜索请求 3 (高浏览量): " + future3.get().hits().total().value() + " 条");

        System.out.println("\n  2. 收集所有结果:");
        List<CompletableFuture<Long>> futures = List.of(
            asyncClient.search(s -> s.index(INDEX_NAME).query(q -> q.matchAll(m -> m)), BlogDocument.class)
                .thenApply(r -> r.hits().total().value())
        );

        List<Long> results = futures.stream()
            .map(f -> {
                try { return f.get(); } catch (Exception e) { return 0L; }
            })
            .collect(Collectors.toList());

        System.out.println("    收集结果: " + results);
    }

    /**
     * 超时处理
     */
    private void timeoutHandling() throws InterruptedException, ExecutionException, TimeoutException {
        ResponsePrinter.printMethodInfo("timeoutHandling", "超时处理");

        System.out.println("\n  1. 设置超时时间:");

        CompletableFuture<SearchResponse<BlogDocument>> future = asyncClient.search(s -> s
            .index(INDEX_NAME)
            .query(q -> q.matchAll(m -> m))
            .size(10)
        , BlogDocument.class);

        try {
            // 最多等待 5 秒
            SearchResponse<BlogDocument> response = future.get(5, TimeUnit.SECONDS);
            System.out.println("    搜索完成，命中: " + response.hits().total().value());
        } catch (TimeoutException e) {
            System.out.println("    搜索超时!");
        }

        System.out.println("\n  2. 超时默认值:");

        try {
            // 使用默认超时配置
            SearchRequest request = SearchRequest.of(s -> s
                .index(INDEX_NAME)
                .query(q -> q.matchAll(m -> m))
                .timeout("3s")
            );

            SearchResponse<BlogDocument> response = syncClient.search(request, BlogDocument.class);
            System.out.println("    使用 timeout 参数: " + request.timeout());
            System.out.println("    命中: " + response.hits().total().value());
        } catch (IOException e) {
            System.out.println("    搜索失败: " + e.getMessage());
        }
    }
}
