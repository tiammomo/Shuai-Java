package com.shuai.elasticsearch.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;
import co.elastic.clients.elasticsearch.indices.DeleteIndexRequest;
import co.elastic.clients.elasticsearch.indices.ExistsRequest;
import com.shuai.elasticsearch.util.ResponsePrinter;

import java.io.IOException;

/**
 * Elasticsearch 索引管理配置类
 * <p>
 * 模块概述
 * ----------
 * 本模块提供 Elasticsearch 索引的创建、删除、查询等管理功能，
 * 封装了常用的索引操作 API。
 * <p>
 * 核心内容
 * ----------
 *   - 索引创建: 支持自定义 Mapping 和 Settings
 *   - 索引删除: 按名称删除或批量删除
 *   - 索引查询: 检查存在性、获取信息
 *   - 初始化入口: initAllIndexes() 一键初始化所有演示索引
 * <p>
 * 支持的索引
 * ----------
 *   - blog: 博客文档索引，支持全文检索和聚合分析
 *   - locations: 地理位置索引，支持 geo_point 查询
 * <p>
 * Blog 索引 Mapping
 * ----------
 *   - id: keyword, 文档唯一标识
 *   - title: text(ik_analyzer), 标题，支持中文分词
 *   - content: text(ik_analyzer), 内容，支持中文分词
 *   - author: keyword, 作者，用于精确匹配和聚合
 *   - tags: keyword[], 标签数组，用于多值精确匹配
 *   - views: long, 浏览次数，用于范围查询和聚合
 *   - status: keyword, 状态 (published/draft)，用于过滤
 *   - createTime: date(yyyy-MM-dd), 创建时间，用于日期范围查询
 * <p>
 * Locations 索引 Mapping
 * ----------
 *   - id: keyword, 地点唯一标识
 *   - name: text, 地点名称
 *   - location: geo_point, 经纬度坐标
 *   - type: keyword, 地点类型 (景点/商业区/公园等)
 *
 * @author Shuai
 * @version 1.0
 * @since 2026-01-17
 * @see <a href="https://www.elastic.co/guide/en/elasticsearch/reference/current/indices.html">Index Management</a>
 */
public class IndexConfig {

    private static final String BLOG_INDEX = "blog";
    private static final String LOCATIONS_INDEX = "locations";
    private static final String PRODUCT_INDEX = "product";

    /**
     * 初始化所有索引
     *
     * @param client ES 客户端
     * @throws IOException IO异常
     */
    public static void initAllIndexes(ElasticsearchClient client) throws IOException {
        ResponsePrinter.printMethodInfo("initAllIndexes", "初始化所有索引");

        createBlogIndex(client);
        createLocationsIndex(client);
    }

    /**
     * 创建 blog 索引（包含完整的 mapping）
     *
     * Mapping 说明:
     *   - title: text 类型，支持 IK 分词
     *   - content: text 类型，支持 IK 分词
     *   - author: keyword 类型，用于精确匹配和聚合
     *   - tags: keyword 数组类型
     *   - views: long 类型，用于范围查询和聚合
     *   - status: keyword 类型，用于过滤
     *   - createTime: date 类型，用于日期范围查询
     *
     * @param client ES 客户端
     * @throws IOException IO异常
     */
    public static void createBlogIndex(ElasticsearchClient client) throws IOException {
        ResponsePrinter.printMethodInfo("createBlogIndex", "创建 blog 索引");

        // 检查索引是否存在
        if (indexExists(client, BLOG_INDEX)) {
            System.out.println("  索引 [" + BLOG_INDEX + "] 已存在，跳过创建");
            return;
        }

        // 创建索引
        CreateIndexRequest request = CreateIndexRequest.of(c -> c
            .index(BLOG_INDEX)
            .settings(s -> s
                .numberOfShards("1")
                .numberOfReplicas("0")
                .analysis(a -> a
                    .analyzer("ik_analyzer", an -> an
                        .custom(ct -> ct
                            .tokenizer("ik_max_word")
                        )
                    )
                )
            )
            .mappings(m -> m
                .properties("id", p -> p.keyword(k -> k))
                .properties("title", p -> p.text(t -> t.analyzer("ik_analyzer")))
                .properties("content", p -> p.text(t -> t.analyzer("ik_analyzer")))
                .properties("author", p -> p.keyword(k -> k))
                .properties("tags", p -> p.keyword(k -> k))
                .properties("views", p -> p.long_(l -> l))
                .properties("status", p -> p.keyword(k -> k))
                .properties("createTime", p -> p.date(d -> d.format("yyyy-MM-dd")))
            )
        );

        client.indices().create(request);
        ResponsePrinter.printResponse("创建索引", BLOG_INDEX + " 创建成功");
    }

    /**
     * 创建 locations 索引（地理索引）
     *
     * Mapping 说明:
     *   - name: 地点名称
     *   - location: geo_point 类型，用于地理位置查询
     *   - type: 地点类型
     *
     * @param client ES 客户端
     * @throws IOException IO异常
     */
    public static void createLocationsIndex(ElasticsearchClient client) throws IOException {
        ResponsePrinter.printMethodInfo("createLocationsIndex", "创建 locations 索引");

        if (indexExists(client, LOCATIONS_INDEX)) {
            System.out.println("  索引 [" + LOCATIONS_INDEX + "] 已存在，跳过创建");
            return;
        }

        CreateIndexRequest request = CreateIndexRequest.of(c -> c
            .index(LOCATIONS_INDEX)
            .settings(s -> s
                .numberOfShards("1")
                .numberOfReplicas("0")
            )
            .mappings(m -> m
                .properties("id", p -> p.keyword(k -> k))
                .properties("name", p -> p.text(t -> t))
                .properties("location", p -> p.geoPoint(gp -> gp))
                .properties("type", p -> p.keyword(k -> k))
            )
        );

        client.indices().create(request);
        ResponsePrinter.printResponse("创建索引", LOCATIONS_INDEX + " 创建成功");
    }

    /**
     * 创建 product 索引（嵌套查询演示）
     *
     * Mapping 说明:
     *   - id: 产品唯一标识
     *   - name: 产品名称，支持中文分词
     *   - category: 产品分类，用于精确匹配
     *   - price: 价格，用于范围查询
     *   - properties: nested 类型，产品属性列表
     *     - name: 属性名称 (如 "内存"、"硬盘")
     *     - value: 属性值 (如 "16GB"、"512GB")
     *     - type: 属性类型
     *   - tags: 标签数组
     *   - status: 状态 (on_sale/off_sale/pre_order)
     *   - stock: 库存数量
     *   - rating: 评分 (1-5)
     *
     * @param client ES 客户端
     * @throws IOException IO异常
     */
    public static void createProductIndex(ElasticsearchClient client) throws IOException {
        ResponsePrinter.printMethodInfo("createProductIndex", "创建 product 索引");

        if (indexExists(client, PRODUCT_INDEX)) {
            System.out.println("  索引 [" + PRODUCT_INDEX + "] 已存在，跳过创建");
            return;
        }

        CreateIndexRequest request = CreateIndexRequest.of(c -> c
            .index(PRODUCT_INDEX)
            .settings(s -> s
                .numberOfShards("1")
                .numberOfReplicas("0")
                .analysis(a -> a
                    .analyzer("ik_analyzer", an -> an
                        .custom(ct -> ct
                            .tokenizer("ik_max_word")
                        )
                    )
                )
            )
            .mappings(m -> m
                // 产品基本信息
                .properties("id", p -> p.keyword(k -> k))
                .properties("name", p -> p.text(t -> t.analyzer("ik_analyzer")))
                .properties("category", p -> p.keyword(k -> k))
                .properties("price", p -> p.double_(d -> d))
                // 嵌套属性列表 - nested 类型
                .properties("properties", p -> p.nested(n -> n
                    .properties("name", pp -> pp.keyword(k -> k))
                    .properties("value", pp -> pp.keyword(k -> k))
                    .properties("type", pp -> pp.keyword(k -> k))
                ))
                // 其他字段
                .properties("tags", p -> p.keyword(k -> k))
                .properties("status", p -> p.keyword(k -> k))
                .properties("stock", p -> p.integer(i -> i))
                .properties("rating", p -> p.double_(d -> d))
            )
        );

        client.indices().create(request);
        ResponsePrinter.printResponse("创建索引", PRODUCT_INDEX + " 创建成功");
    }

    /**
     * 删除索引
     *
     * @param client      ES 客户端
     * @param indexName   索引名称
     * @throws IOException IO异常
     */
    public static void deleteIndex(ElasticsearchClient client, String indexName) throws IOException {
        ResponsePrinter.printMethodInfo("deleteIndex", "删除索引: " + indexName);

        if (!indexExists(client, indexName)) {
            System.out.println("  索引 [" + indexName + "] 不存在，无需删除");
            return;
        }

        DeleteIndexRequest request = DeleteIndexRequest.of(d -> d
            .index(indexName)
        );

        client.indices().delete(request);
        ResponsePrinter.printResponse("删除索引", indexName + " 删除成功");
    }

    /**
     * 删除所有演示索引
     *
     * @param client ES 客户端
     * @throws IOException IO异常
     */
    public static void deleteAllDemoIndexes(ElasticsearchClient client) throws IOException {
        ResponsePrinter.printMethodInfo("deleteAllDemoIndexes", "删除所有演示索引");

        deleteIndex(client, BLOG_INDEX);
        deleteIndex(client, LOCATIONS_INDEX);
    }

    /**
     * 检查索引是否存在
     *
     * @param client     ES 客户端
     * @param indexName  索引名称
     * @return 是否存在
     */
    public static boolean indexExists(ElasticsearchClient client, String indexName) {
        try {
            ExistsRequest request = ExistsRequest.of(e -> e
                .index(indexName)
            );
            return client.indices().exists(request).value();
        } catch (IOException e) {
            System.err.println("  检查索引存在性失败: " + e.getMessage());
            return false;
        }
    }

    /**
     * 获取索引信息
     *
     * @param client    ES 客户端
     * @param indexName 索引名称
     */
    public static void getIndexInfo(ElasticsearchClient client, String indexName) {
        ResponsePrinter.printMethodInfo("getIndexInfo", "获取索引信息: " + indexName);

        if (!indexExists(client, indexName)) {
            System.out.println("  索引 [" + indexName + "] 不存在");
            return;
        }

        try {
            var indexStats = client.indices().stats(s -> s.index(indexName));
            var primaries = indexStats.all().primaries();

            System.out.println("  索引: " + indexName);
            System.out.println("  文档数: " + (primaries.docs() != null ? primaries.docs().count() : 0));
            System.out.println("  存储大小: " + (primaries.store() != null ? primaries.store().sizeInBytes() : 0) + " bytes");
        } catch (IOException e) {
            System.err.println("  获取索引信息失败: " + e.getMessage());
        }
    }
}
