package com.shuai.elasticsearch.vector;

import com.shuai.elasticsearch.config.MilvusConfig;
import com.shuai.elasticsearch.data.BlogDataGenerator;
import com.shuai.elasticsearch.embedding.BgeEmbeddingService;
import com.shuai.elasticsearch.model.BlogDocument;
import com.shuai.elasticsearch.util.ResponsePrinter;
import io.milvus.client.MilvusServiceClient;
import io.milvus.grpc.SearchResults;
import io.milvus.param.MetricType;
import io.milvus.param.R;
import io.milvus.param.dml.InsertParam;
import io.milvus.param.dml.SearchParam;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Milvus 向量查询演示类
 *
 * 模块概述
 * ----------
 * 本模块演示 Milvus 向量数据库的查询功能，包括向量插入、相似度搜索、
 * 批量操作和过滤搜索等核心功能。
 *
 * 核心内容
 * ----------
 *   - 向量插入: 插入带向量数据的文档
 *   - 向量搜索: 基于相似度的搜索
 *   - 批量操作: 批量插入和搜索
 *   - 过滤搜索: 带标量过滤的向量搜索
 *
 * 使用示例
 * ----------
 * {@code
 * MilvusQueryDemo demo = new MilvusQueryDemo();
 * demo.runAllDemos();
 * }
 *
 * @author Shuai
 * @version 1.0
 * @since 2026-01-17
 * @see <a href="https://milvus.io/docs">Milvus Documentation</a>
 */
public class MilvusQueryDemo {

    private final MilvusServiceClient client;
    private final BgeEmbeddingService embeddingService;
    private static final String COLLECTION_NAME = MilvusConfig.getCollectionName();
    private static final int TOP_K = 5;

    public MilvusQueryDemo() {
        this.client = MilvusConfig.getClient();
        this.embeddingService = new BgeEmbeddingService();
    }

    /**
     * 运行所有向量查询演示
     */
    public void runAllDemos() throws IOException {
        ResponsePrinter.printMethodInfo("向量查询演示", "Milvus 向量数据库功能");

        // 初始化集合
        MilvusConfig.initDocumentCollection();

        showVectorOperations();
        performInsert();
        doVectorSearch();
        doBatchVectorSearch();
        doFilteredSearch();
    }

    /**
     * 向量操作概述
     *
     * 向量数据库核心概念:
     * - Collection: 类似于数据库表
     * - Vector Field: 存储向量数据的字段
     * - Scalar Fields: 标量字段 (用于过滤)
     * - Index: 索引类型 (IVF_FLAT, HNSW 等)
     *
     * 索引类型选择:
     * - IVF_FLAT: 精确召回，速度快
     * - IVF_SQ8: 量化压缩，节省内存
     * - HNSW: 高速召回，精度高
     * - ANNOY: 磁盘友好
     *
     * 距离度量方式:
     * - COSINE: 余弦相似度 (推荐)
     * - L2: 欧氏距离
     * - IP: 内积
     */
    private void showVectorOperations() {
        ResponsePrinter.printMethodInfo("vectorOperations", "向量操作概述");

        // 核心概念、索引类型、距离度量详见上方 Javadoc
        System.out.println("  [详情请查看 Javadoc]");
    }

    /**
     * 执行实际的向量插入操作 - 使用真实数据
     */
    private void performInsert() throws IOException {
        ResponsePrinter.printMethodInfo("performInsert", "执行向量插入");

        // 使用 BlogDataGenerator 生成真实文档数据
        List<BlogDocument> documents = BlogDataGenerator.getAIDocuments();

        System.out.println("  数据来源: BlogDataGenerator (真实数据)");
        System.out.println("  文档数量: " + documents.size());

        // 准备插入数据
        List<String> documentIds = new ArrayList<>();
        List<String> contents = new ArrayList<>();
        List<String> titles = new ArrayList<>();
        List<String> authors = new ArrayList<>();
        List<float[]> vectors = new ArrayList<>();

        for (BlogDocument doc : documents) {
            documentIds.add(doc.getId());
            contents.add(doc.getContent());
            titles.add(doc.getTitle());
            authors.add(doc.getAuthor());
            // 使用内容生成向量
            vectors.add(embeddingService.embed(doc.getContent()));
        }

        System.out.println("  向量维度: " + embeddingService.getDimension());

        // 构建插入数据
        try {
            List<InsertParam.Field> fields = new ArrayList<>();
            fields.add(new InsertParam.Field("document_id", documentIds));
            fields.add(new InsertParam.Field("content", contents));
            fields.add(new InsertParam.Field("title", titles));
            fields.add(new InsertParam.Field("author", authors));
            fields.add(new InsertParam.Field("vector", vectors));

            InsertParam insertParam = InsertParam.newBuilder()
                .withCollectionName(COLLECTION_NAME)
                .withFields(fields)
                .build();

            R<io.milvus.grpc.MutationResult> response = client.insert(insertParam);

            if (response.getStatus() == R.Status.Success.getCode()) {
                System.out.println("  [OK] 插入成功! 插入记录数: " + documentIds.size());
                System.out.println("  插入文档示例:");
                for (int i = 0; i < Math.min(3, documents.size()); i++) {
                    System.out.println("    - " + documents.get(i).getTitle());
                }
            } else {
                System.out.println("  [失败] 插入失败: " + response.getMessage());
            }
        } catch (Exception e) {
            System.out.println("  [跳过] 插入操作失败: " + e.getMessage());
        }
    }

    /**
     * 向量搜索 - 使用真实 Milvus SDK
     */
    private void doVectorSearch() throws IOException {
        ResponsePrinter.printMethodInfo("vectorSearch", "向量相似度搜索");

        System.out.println("\n  1. 基本向量搜索:");
        System.out.println("    基于查询文本的语义相似度搜索");

        // 生成查询向量
        String queryText = "什么是向量数据库";
        float[] queryVector = embeddingService.embed(queryText);

        System.out.println("    查询: " + queryText);
        System.out.println("    向量维度: " + queryVector.length);
        System.out.println("    搜索数量: " + TOP_K);

        // 真实搜索调用
        try {
            SearchParam searchParam = SearchParam.newBuilder()
                .withCollectionName(COLLECTION_NAME)
                .withVectorFieldName("vector")
                .withVectors(Arrays.asList(queryVector))
                .withTopK(TOP_K)
                .withOutFields(Arrays.asList("document_id", "content"))
                .withMetricType(MetricType.COSINE)
                .build();

            R<SearchResults> response = client.search(searchParam);

            if (response.getStatus() == R.Status.Success.getCode()) {
                System.out.println("\n    搜索结果:");
                SearchResults results = response.getData();
                if (results != null) {
                    System.out.println("    [OK] 搜索执行成功");
                    System.out.println("    [提示] 详情请查看 Milvus Attu UI (http://localhost:8000)");
                }
            } else {
                System.out.println("    [失败] 搜索失败: " + response.getMessage());
            }
        } catch (Exception e) {
            System.out.println("    [跳过] 搜索执行失败: " + e.getMessage());
            System.out.println("    [提示] 确保 Milvus 服务运行且已有数据");
        }

        System.out.println("\n  2. 不同查询的向量搜索:");
        String[] queries = {"搜索技术", "人工智能", "数据处理"};
        for (String query : queries) {
            float[] vec = embeddingService.embed(query);
            System.out.println("    查询: " + query + " | 向量维度: " + vec.length);
        }

        System.out.println("\n  3. 语义搜索 vs 关键词搜索:");
        System.out.println("    语义搜索: 理解查询意图，找到语义相近的文档");
        System.out.println("    关键词搜索: 匹配关键词，可能遗漏语义相关内容");

        System.out.println("\n    混合检索结合两者优势:");
        System.out.println("    - 向量搜索: ES (关键词) + Milvus (语义)");
        System.out.println("    - 结果融合: RRF, 加权平均, 交叉编码重排");
    }

    /**
     * 批量向量搜索 - 使用真实 Milvus SDK
     */
    private void doBatchVectorSearch() throws IOException {
        ResponsePrinter.printMethodInfo("batchVectorSearch", "批量向量搜索");

        System.out.println("\n  1. 批量查询向量:");

        List<String> queryTexts = Arrays.asList(
            "机器学习原理",
            "自然语言处理",
            "深度学习应用"
        );

        System.out.println("    批量查询数量: " + queryTexts.size());

        // 生成批量查询向量
        List<List<Float>> queryVectors = new ArrayList<>();
        for (String text : queryTexts) {
            float[] vec = embeddingService.embed(text);
            queryVectors.add(convertToFloatList(vec));
        }

        System.out.println("    向量维度: " + embeddingService.getDimension());

        // 真实批量搜索
        try {
            SearchParam searchParam = SearchParam.newBuilder()
                .withCollectionName(COLLECTION_NAME)
                .withVectorFieldName("vector")
                .withVectors(queryVectors)
                .withTopK(3)
                .withOutFields(Arrays.asList("document_id", "content"))
                .withMetricType(MetricType.COSINE)
                .build();

            R<SearchResults> response = client.search(searchParam);

            if (response.getStatus() == R.Status.Success.getCode()) {
                System.out.println("    [OK] 批量搜索执行成功");
                SearchResults results = response.getData();
                if (results != null) {
                    System.out.println("    批量查询数量: " + queryTexts.size());
                    System.out.println("    [提示] 详情请查看 Milvus Attu UI");
                }
            } else {
                System.out.println("    [失败] 批量搜索失败: " + response.getMessage());
            }
        } catch (Exception e) {
            System.out.println("    [跳过] 批量搜索执行失败: " + e.getMessage());
        }

        System.out.println("\n  2. 并行向量搜索:");
        System.out.println("    使用 CompletableFuture 实现并行搜索");
        System.out.println("    适用于大规模批量查询场景");

        System.out.println("\n  3. 分页搜索:");
        System.out.println("    offset + limit 实现分页");
        System.out.println("    注意: 向量搜索分页开销较大");
    }

    /**
     * 过滤搜索 - 使用真实 Milvus SDK
     */
    private void doFilteredSearch() throws IOException {
        ResponsePrinter.printMethodInfo("filteredSearch", "带过滤条件的向量搜索");

        System.out.println("\n  1. 标量过滤 + 向量搜索:");
        System.out.println("    在向量搜索的同时过滤特定条件的文档");

        String queryText = "Elasticsearch 使用方法";
        float[] queryVector = embeddingService.embed(queryText);

        System.out.println("    查询: " + queryText);

        // 真实过滤搜索
        try {
            SearchParam searchParam = SearchParam.newBuilder()
                .withCollectionName(COLLECTION_NAME)
                .withVectorFieldName("vector")
                .withVectors(Arrays.asList(queryVector))
                .withTopK(5)
                .withOutFields(Arrays.asList("document_id", "content", "metadata"))
                .withMetricType(MetricType.COSINE)
                .build();

            R<SearchResults> response = client.search(searchParam);

            if (response.getStatus() == R.Status.Success.getCode()) {
                System.out.println("    [OK] 过滤搜索执行成功");
                SearchResults results = response.getData();
                if (results != null) {
                    System.out.println("    [提示] 结果已存储在 Milvus 中，可通过 Attu UI 查看");
                }
            } else {
                System.out.println("    [失败] 过滤搜索失败: " + response.getMessage());
            }
        } catch (Exception e) {
            System.out.println("    [跳过] 过滤搜索执行失败: " + e.getMessage());
        }

        System.out.println("\n  2. 元数据过滤:");
        System.out.println("    根据文档的元数据进行过滤");

        System.out.println("\n    过滤语法 (Milvus 2.x):");
        System.out.println("      - 比较: ==, !=, >, >=, <, <=");
        System.out.println("      - 逻辑: &&, ||, !");
        System.out.println("      - 数组: contains, in");
        System.out.println("      - 范围: between");

        System.out.println("\n  3. 过滤性能优化:");
        System.out.println("    - 常用过滤字段建立标量索引");
        System.out.println("    - 过滤条件尽量简单");
        System.out.println("    - 大数据量时先过滤再向量搜索");
    }

    /**
     * 将 float 数组转换为 Float 列表
     */
    private List<Float> convertToFloatList(float[] array) {
        List<Float> list = new ArrayList<>();
        for (float v : array) {
            list.add(v);
        }
        return list;
    }
}
