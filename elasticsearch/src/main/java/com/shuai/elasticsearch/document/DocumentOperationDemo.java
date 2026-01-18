package com.shuai.elasticsearch.document;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.bulk.BulkOperation;
import co.elastic.clients.elasticsearch.core.bulk.IndexOperation;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.shuai.elasticsearch.config.ElasticsearchConfig;
import com.shuai.elasticsearch.data.BlogDataGenerator;
import com.shuai.elasticsearch.model.BlogDocument;
import com.shuai.elasticsearch.util.ResponsePrinter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 文档操作演示类
 *
 * 模块概述
 * ----------
 * 本模块演示 Elasticsearch 文档的 CRUD 操作和批量处理。
 *
 * 核心内容
 * ----------
 *   - 插入文档: 单条/批量插入
 *   - 更新文档: 全量/部分更新
 *   - 删除文档: 按 ID/条件删除
 *   - 查询文档: 按 ID/批量查询
 *
 * @author Shuai
 * @version 1.0
 * @see <a href="https://www.elastic.co/guide/en/elasticsearch/reference/current/docs.html">Document APIs</a>
 */
public class DocumentOperationDemo {

    private final ElasticsearchClient client;
    private static final String INDEX_NAME = "blog";

    public DocumentOperationDemo() {
        this.client = ElasticsearchConfig.getClient();
    }

    /**
     * 运行所有文档操作演示
     */
    public void runAllDemos() throws IOException {
        ResponsePrinter.printMethodInfo("文档操作演示", "Elasticsearch 文档 CRUD 和批量操作");

        // 创建测试数据
        insertDocument();
        bulkInsertDocuments();
        updateDocument();
        partialUpdate();
        scriptUpdate();
        upsertDocument();
        queryDocument();
        bulkGetDocuments();
        deleteDocument();
        deleteByQuery();
    }

    /**
     * 插入文档
     *
     * REST API:
     *   POST /blog/_doc/1
     *   { "title": "Java 教程", ... }
     */
    private void insertDocument() throws IOException {
        ResponsePrinter.printMethodInfo("insertDocument", "插入单个文档");

        BlogDocument doc = BlogDocument.builder()
            .id("1")
            .title("Java 教程")
            .content("这是 Java 教程内容，包含 Java 基础知识和高级特性")
            .author("张三")
            .tags(Arrays.asList("java", "教程", "编程"))
            .views(100)
            .status("published")
            .createTime("2024-01-15")
            .build();

        IndexResponse response = client.index(i -> i
            .index(INDEX_NAME)
            .id(doc.getId())
            .document(doc)
        );

        ResponsePrinter.printResponse("插入文档", response.result().jsonValue());
        System.out.println("  索引: " + INDEX_NAME);
        System.out.println("  ID: " + doc.getId());
        System.out.println("  版本: " + response.version());
    }

    /**
     * 批量插入文档 - 使用真实数据
     *
     * REST API:
     *   POST /_bulk
     *   { "index": { "_index": "blog", "_id": "2" }}
     *   { "title": "Python 教程", ... }
     */
    private void bulkInsertDocuments() throws IOException {
        ResponsePrinter.printMethodInfo("bulkInsertDocuments", "批量插入文档");

        // 使用 BlogDataGenerator 生成真实数据
        List<BlogDocument> docs = BlogDataGenerator.getAllDocuments();

        System.out.println("  数据来源: BlogDataGenerator");
        System.out.println("  文档数量: " + docs.size());

        List<BulkOperation> operations = new ArrayList<>();
        for (BlogDocument doc : docs) {
            operations.add(BulkOperation.of(op -> op
                .index(IndexOperation.of(idx -> idx
                    .index(INDEX_NAME)
                    .id(doc.getId())
                    .document(doc)
                ))
            ));
        }

        BulkResponse response = client.bulk(b -> b
            .index(INDEX_NAME)
            .operations(operations)
        );

        ResponsePrinter.printBulkResponse(response);
    }

    /**
     * 更新文档 - 全量更新
     *
     * REST API:
     *   PUT /blog/_doc/1
     *   { "title": "Java 高级教程", ... }
     */
    private void updateDocument() throws IOException {
        ResponsePrinter.printMethodInfo("updateDocument", "全量更新文档");

        BlogDocument doc = BlogDocument.builder()
            .id("1")
            .title("Java 高级教程")
            .content("更新后的 Java 教程内容")
            .author("张三")
            .tags(Arrays.asList("java", "教程", "高级"))
            .views(150)
            .status("published")
            .createTime("2024-01-15")
            .build();

        IndexResponse response = client.index(i -> i
            .index(INDEX_NAME)
            .id(doc.getId())
            .document(doc)
        );

        ResponsePrinter.printResponse("全量更新", response.result().jsonValue());
    }

    /**
     * 部分更新文档
     *
     * REST API:
     *   POST /blog/_update/1
     *   { "doc": { "views": 200 } }
     */
    private void partialUpdate() throws IOException {
        ResponsePrinter.printMethodInfo("partialUpdate", "部分更新文档");

        // 更新 views 字段
        UpdateResponse<BlogDocument> response = client.update(u -> u
            .index(INDEX_NAME)
            .id("1")
            .doc(new BlogDocument(null, null, null, null, null, 200, null, null))
            .docAsUpsert(true)
        , BlogDocument.class);

        ResponsePrinter.printResponse("部分更新", response.result().jsonValue());
        System.out.println("  新版本: " + response.version());
    }

    /**
     * 脚本更新
     *
     * REST API:
     *   POST /blog/_update/1
     *   { "script": { "source": "ctx._source.views += params.increment", "params": { "increment": 1 }}}
     */
    private void scriptUpdate() throws IOException {
        ResponsePrinter.printMethodInfo("scriptUpdate", "使用脚本更新文档");

        UpdateResponse<BlogDocument> response = client.update(u -> u
            .index(INDEX_NAME)
            .id("1")
            .script(s -> s
                .source("ctx._source.views += 1")
            )
        , BlogDocument.class);

        ResponsePrinter.printResponse("脚本更新", response.result().jsonValue());
    }

    /**
     * Upsert - 文档不存在则插入
     *
     * REST API:
     *   POST /blog/_update/5
     *   { "doc": { "title": "新文档" }, "upsert": { "title": "默认标题", "views": 0 }}
     */
    private void upsertDocument() throws IOException {
        ResponsePrinter.printMethodInfo("upsertDocument", "Upsert 操作（不存在则插入）");

        BlogDocument upsertDoc = BlogDocument.builder()
            .title("这是一篇新文章")
            .content("新文章的详细内容")
            .author("新作者")
            .tags(Arrays.asList("新文章"))
            .views(0)
            .status("draft")
            .createTime("2024-01-20")
            .build();

        UpdateResponse<BlogDocument> response = client.update(u -> u
            .index(INDEX_NAME)
            .id("5")
            .doc(upsertDoc)
            .docAsUpsert(true)
        , BlogDocument.class);

        ResponsePrinter.printResponse("Upsert", response.result().jsonValue());
    }

    /**
     * 查询文档
     *
     * REST API:
     *   GET /blog/_doc/1
     */
    private void queryDocument() throws IOException {
        ResponsePrinter.printMethodInfo("queryDocument", "按 ID 查询文档");

        GetResponse<BlogDocument> response = client.get(g -> g
            .index(INDEX_NAME)
            .id("1")
        , BlogDocument.class);

        if (response.found()) {
            BlogDocument doc = response.source();
            System.out.println("  找到文档:");
            System.out.println("    ID: " + response.id());
            System.out.println("    标题: " + doc.getTitle());
            System.out.println("    作者: " + doc.getAuthor());
            System.out.println("    浏览: " + doc.getViews());
        } else {
            System.out.println("  文档未找到");
        }
    }

    /**
     * 批量查询文档
     *
     * REST API:
     *   GET /blog/_search
     *   { "query": { "ids": { "values": ["1", "2", "3", "4"] }}}
     */
    private void bulkGetDocuments() throws IOException {
        ResponsePrinter.printMethodInfo("bulkGetDocuments", "批量查询文档");

        List<String> ids = Arrays.asList("1", "2", "3", "4");

        co.elastic.clients.elasticsearch.core.SearchResponse<BlogDocument> response = client.search(s -> s
            .index(INDEX_NAME)
            .query(q -> q.ids(i -> i.values(ids))), BlogDocument.class);

        System.out.println("  查询结果:");
        for (Hit<BlogDocument> hit : response.hits().hits()) {
            System.out.println("    [" + hit.id() + "] " + hit.source().getTitle());
        }
    }

    /**
     * 删除文档
     *
     * REST API:
     *   DELETE /blog/_doc/5
     */
    private void deleteDocument() throws IOException {
        ResponsePrinter.printMethodInfo("deleteDocument", "按 ID 删除文档");

        DeleteResponse response = client.delete(d -> d
            .index(INDEX_NAME)
            .id("5")
        );

        ResponsePrinter.printResponse("删除文档", response.result().jsonValue());
    }

    /**
     * 条件删除
     *
     * REST API:
     *   POST /blog/_delete_by_query
     *   { "query": { "term": { "status": "draft" }}}
     */
    private void deleteByQuery() throws IOException {
        ResponsePrinter.printMethodInfo("deleteByQuery", "按条件删除文档");

        DeleteByQueryResponse response = client.deleteByQuery(d -> d
            .index(INDEX_NAME)
            .query(q -> q.term(t -> t.field("status").value("draft")))
        );

        System.out.println("  删除的文档数: " + response.deleted());
        System.out.println("  超时: " + response.timedOut());
    }
}
