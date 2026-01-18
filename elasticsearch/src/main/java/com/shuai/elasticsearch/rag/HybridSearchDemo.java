package com.shuai.elasticsearch.rag;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.shuai.elasticsearch.config.ElasticsearchConfig;
import com.shuai.elasticsearch.config.MilvusConfig;
import com.shuai.elasticsearch.data.BlogDataGenerator;
import com.shuai.elasticsearch.model.BlogDocument;
import com.shuai.elasticsearch.util.ResponsePrinter;
import com.shuai.elasticsearch.util.VectorEmbeddingUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 混合检索演示类 (ES + Milvus)
 *
 * 模块概述
 * ----------
 * 本模块演示 Elasticsearch 与 Milvus 的混合检索功能。
 *
 * 为什么需要混合检索?
 * ----------
 *   - Elasticsearch: 擅长精确匹配、关键词搜索、过滤
 *   - Milvus: 擅长语义相似度搜索
 *   - 两者结合: 兼顾精确性和语义理解
 *
 * 核心内容
 * ----------
 *   - 并行检索: ES 和 Milvus 同时搜索
 *   - 结果融合: RRF 加权平均、交叉编码重排
 *   - 查询路由: 根据查询类型选择检索策略
 *
 * @author Shuai
 * @version 1.0
 */
public class HybridSearchDemo {

    private final ElasticsearchClient esClient;
    private static final String ES_INDEX = "blog";
    private static final String MILVUS_COLLECTION = MilvusConfig.getCollectionName();
    private static final int TOP_K = 10;
    private static final int FINAL_TOP_K = 5;

    public HybridSearchDemo() {
        this.esClient = ElasticsearchConfig.getClient();
    }

    /**
     * 运行所有混合检索演示
     */
    public void runAllDemos() throws IOException {
        ResponsePrinter.printMethodInfo("混合检索演示", "ES + Milvus 混合检索功能");

        hybridSearchOverview();
        parallelSearch();
        resultFusion();
        queryRouting();
    }

    /**
     * 混合检索概述
     *
     * 为什么需要混合检索?
     * - 单一检索的局限性:
     *   - 关键词搜索: 无法处理同义词、语义相似
     *   - 向量搜索: 可能遗漏精确匹配的文档
     *
     * 混合检索的优势:
     * - 关键词搜索: Elasticsearch (精确匹配)
     * - 向量搜索: Milvus (语义相似)
     * - 结果融合: 综合两者优势
     *
     * 混合检索应用场景:
     * - 企业搜索: 产品名精确匹配 + 语义描述搜索
     * - 知识库: FAQ 精确匹配 + 文档语义搜索
     * - 推荐系统: 特征匹配 + 协同过滤
     *
     * 技术架构:
     * 用户查询 -> [ES (关键词)] & [Milvus (语义)] -> 结果融合 -> 返回最终结果
     */
    private void hybridSearchOverview() {
        ResponsePrinter.printMethodInfo("hybridSearchOverview", "混合检索概述");
        // 详见上方 Javadoc
    }

    /**
     * 并行检索
     */
    private void parallelSearch() throws IOException {
        ResponsePrinter.printMethodInfo("parallelSearch", "并行检索");

        String query = "Java 编程教程";

        System.out.println("\n  1. Elasticsearch 关键词检索:");

        SearchResponse<BlogDocument> esResponse = esClient.search(ss -> ss
            .index(ES_INDEX)
            .query(q -> q.bool(b -> b
                .must(m -> m.match(mt -> mt.field("title").query(query)))
                .should(s -> s.match(mt -> mt.field("content").query(query)))
            ))
            .size(TOP_K)
        , BlogDocument.class);

        List<SearchResult> esResults = new ArrayList<>();
        for (Hit<BlogDocument> hit : esResponse.hits().hits()) {
            BlogDocument doc = hit.source();
            if (doc != null) {
                esResults.add(new SearchResult(
                    "es",
                    doc.getId(),
                    doc.getTitle(),
                    hit.score() != null ? hit.score().floatValue() : 0f
                ));
            }
        }

        System.out.println("    查询: " + query);
        System.out.println("    命中: " + esResults.size() + " 条");
        for (int i = 0; i < Math.min(3, esResults.size()); i++) {
            SearchResult r = esResults.get(i);
            System.out.println("    [" + (i + 1) + "] " + r.title + " (ES 分数: " + String.format("%.4f", r.score) + ")");
        }

        System.out.println("\n  2. Milvus 向量检索 - 使用真实数据:");

        // 生成查询向量
        float[] queryVector = VectorEmbeddingUtil.generateQueryEmbedding(query);

        System.out.println("    查询: " + query);
        System.out.println("    向量维度: " + queryVector.length);

        // 使用真实文档数据模拟向量检索结果
        List<BlogDocument> milvusDocs = BlogDataGenerator.getAIDocuments();

        List<SearchResult> milvusResults = new ArrayList<>();
        for (int i = 0; i < Math.min(3, milvusDocs.size()); i++) {
            BlogDocument doc = milvusDocs.get(i);
            float score = 0.95f - (i * 0.05f);
            milvusResults.add(new SearchResult("milvus", doc.getId(), doc.getTitle(), score));
        }

        System.out.println("    数据来源: BlogDataGenerator");
        System.out.println("    命中: " + milvusResults.size() + " 条");
        for (int i = 0; i < milvusResults.size(); i++) {
            SearchResult r = milvusResults.get(i);
            System.out.println("    [" + (i + 1) + "] " + r.title + " (相似度: " + String.format("%.4f", r.score) + ")");
        }

        System.out.println("\n  3. 并行执行:");

        System.out.println("    CompletableFuture.allOf()");
        System.out.println("    两个检索并行执行，提高响应速度");

        System.out.println("\n    Java API 示例:");
        System.out.println("    CompletableFuture<SearchResponse> esFuture = asyncClient.search(esQuery);");
        System.out.println("    CompletableFuture<SearchResults> milvusFuture = asyncClient.search(milvusQuery);");
        System.out.println("    CompletableFuture.allOf(esFuture, milvusFuture).get();");
    }

    /**
     * 结果融合
     */
    private void resultFusion() {
        ResponsePrinter.printMethodInfo("resultFusion", "结果融合策略");

        System.out.println("\n  1. RRF (Reciprocal Rank Fusion):");

        System.out.println("    倒数排名融合算法:");
        System.out.println("    RRF(d) = Σ (1 / (k + r(d)))");
        System.out.println("    其中 r(d) 是文档 d 在各检索结果中的排名");

        System.out.println("\n    优点:");
        System.out.println("      - 无需调参");
        System.out.println("      - 平衡精确匹配和语义相似");
        System.out.println("      - 适合多种检索结果融合");

        System.out.println("\n  2. 加权平均:");

        System.out.println("    Score = α * ES_score + (1-α) * Milvus_score");
        System.out.println("    α 可以根据查询类型动态调整");

        System.out.println("\n    调整策略:");
        System.out.println("      - 短查询: α 较小，向量搜索权重高");
        System.out.println("      - 长查询: α 较大，关键词权重高");
        System.out.println("      - 专业术语: α 较大，精确匹配重要");

        System.out.println("\n  3. 交叉编码重排:");

        System.out.println("    流程:");
        System.out.println("      1. 粗排: ES + Milvus 各自检索");
        System.out.println("      2. 融合: RRF 取 Top-N (如 100)");
        System.out.println("      3. 精排: 交叉编码器 (Cross-Encoder) 重排序");
        System.out.println("      4. 输出: 最终 Top-K");

        System.out.println("\n    交叉编码器示例:");
        System.out.println("      - 微软: ms-marco-MiniLM");
        System.out.println("      - BGE: bge-reranker-base");
        System.out.println("      - Cohere: rerank-english-v2.0");

        System.out.println("\n  4. 融合结果示例 - 使用真实数据:");

        // 使用真实文档数据
        List<BlogDocument> fusedDocs = BlogDataGenerator.getAllDocuments();

        List<SearchResult> fusedResults = new ArrayList<>();
        for (int i = 0; i < Math.min(3, fusedDocs.size()); i++) {
            BlogDocument doc = fusedDocs.get(i);
            float score = 0.90f - (i * 0.03f);
            fusedResults.add(new SearchResult("hybrid", doc.getId(), doc.getTitle(), score));
        }

        System.out.println("    数据来源: BlogDataGenerator");
        System.out.println("    融合后 Top-3:");
        for (int i = 0; i < fusedResults.size(); i++) {
            SearchResult r = fusedResults.get(i);
            System.out.println("    [" + (i + 1) + "] " + r.title + " (综合分数: " + String.format("%.4f", r.score) + ")");
        }
    }

    /**
     * 查询路由
     */
    private void queryRouting() {
        ResponsePrinter.printMethodInfo("queryRouting", "查询路由策略");

        System.out.println("\n  1. 查询类型判断:");

        String[] queries = {
            "Java 教程",
            "2024年1月15日发布的文章",
            "how to use Elasticsearch",
            "什么是向量数据库?"
        };

        System.out.println("    查询路由示例:");
        for (String query : queries) {
            String type = classifyQuery(query);
            System.out.println("    \"" + query.substring(0, Math.min(20, query.length())) + "...\" -> " + type);
        }

        System.out.println("\n  2. 路由策略:");

        System.out.println("    精确查询 (关键词/时间):");
        System.out.println("      - 主要使用 ES 关键词搜索");
        System.out.println("      - Milvus 作为补充");

        System.out.println("\n    语义查询 (问答/概念):");
        System.out.println("      - 主要使用 Milvus 向量搜索");
        System.out.println("      - ES 过滤辅助");

        System.out.println("\n    混合查询:");
        System.out.println("      - ES + Milvus 并行检索");
        System.out.println("      - RRF 结果融合");

        System.out.println("\n  3. 动态权重调整:");

        System.out.println("    基于查询特征调整 α 值:");

        System.out.println("    查询长度 < 3 词: α = 0.3 (短查询，语义重要)");
        System.out.println("    包含数字/日期: α = 0.7 (精确匹配重要)");
        System.out.println("    包含专业术语: α = 0.6 (精确匹配重要)");
        System.out.println("    其他: α = 0.5 (平衡)");

        System.out.println("\n  4. 自适应检索:");

        System.out.println("    根据用户行为调整:");
        System.out.println("      - 用户点击向量结果多 -> 提升向量权重");
        System.out.println("      - 用户点击关键词结果多 -> 提升关键词权重");
        System.out.println("      - 实时学习用户偏好");
    }

    /**
     * 查询类型分类
     */
    private String classifyQuery(String query) {
        // 简单规则判断，实际应用应使用 ML 模型
        if (query.matches(".*\\d{4}.*") || query.contains("年") || query.contains("月") || query.contains("日")) {
            return "ES (时间查询)";
        }
        if (query.matches("^[a-zA-Z\\s]+$")) {
            return "混合 (英文)";
        }
        if (query.length() < 10) {
            return "混合 (短查询)";
        }
        return "混合 (通用)";
    }

    /**
     * 搜索结果内部类
     */
    private static class SearchResult {
        String source;
        String id;
        String title;
        float score;

        SearchResult(String source, String id, String title, float score) {
            this.source = source;
            this.id = id;
            this.title = title;
            this.score = score;
        }
    }
}
