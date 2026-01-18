package com.shuai.elasticsearch.embedding;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shuai.elasticsearch.config.SiliconFlowConfig;
import com.shuai.elasticsearch.util.ResponsePrinter;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * Cross-Encoder 重排序服务 (SiliconFlow API)
 * <p>
 * 模块概述
 * ----------
 * 本模块实现基于 Cross-Encoder 的文档重排序功能。
 * <p>
 * 实现原理
 * ----------
 * 两阶段检索流程：
 *   1. 召回阶段 (Embedding): 使用 Bi-Encoder 快速召回候选文档
 *   2. 排序阶段 (Reranker): 使用 Cross-Encoder 对候选文档精排
 * <p>
 * Cross-Encoder 特点
 * ----------
 *   - 同时接收 query 和 document 作为输入
 *   - 精度高但计算成本高
 *   - 适合对少量候选文档进行精排
 * <p>
 * 推荐模型
 * ----------
 *   - BAAI/bge-reranker-v2-m3 (推荐，中英文兼优)
 *   - BAAI/bge-reranker-base
 * <p>
 * 配置示例
 * ----------
 * {@code
 * // 配置文件: application-siliconflow.properties
 * sf.api.key=sk-xxx
 * sf.reranker.model=BAAI/bge-reranker-v2-m3
 * }
 * <p>
 * 使用示例
 * ----------
 * {@code
 * RerankerService reranker = new RerankerService();
 *
 * // 单条重排序
 * float score = reranker.score("如何学习Java", "Java是一门面向对象编程语言");
 *
 * // 批量重排序
 * List<RerankResult> results = reranker.rerank(
 *     "如何学习Java",
 *     List.of("Java教程", "Python教程", "C++教程")
 * );
 * }
 *
 * @author Shuai
 * @version 1.0
 * @since 2026-01-17
 * @see <a href="https://github.com/FlagOpen/FlagEmbedding">FlagEmbedding</a>
 * @see <a href="https://cloud.siliconflow.cn">SiliconFlow</a>
 */
public class RerankerService {

    private static final String DEFAULT_MODEL = "BAAI/bge-reranker-v2-m3";

    private final String modelName;
    private final String apiUrl;
    private String apiKey;
    private final boolean available;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final SiliconFlowConfig siliconFlowConfig;

    /**
     * 默认构造函数
     */
    public RerankerService() {
        this.siliconFlowConfig = SiliconFlowConfig.getInstance();
        this.modelName = siliconFlowConfig.getRerankerModel();
        this.apiUrl = siliconFlowConfig.getRerankerUrl();

        if (siliconFlowConfig.isConfigured()) {
            this.apiKey = siliconFlowConfig.getApiKey();
            this.available = true;
            ResponsePrinter.printMethodInfo("RerankerService",
                "Reranker 服务 (SiliconFlow): " + modelName);
        } else {
            this.apiKey = null;
            this.available = false;
            ResponsePrinter.printMethodInfo("RerankerService",
                "Reranker 服务 (未配置): 使用模拟模式");
            ResponsePrinter.printResponse("警告", "未配置 SiliconFlow API Key，使用模拟分数");
            ResponsePrinter.printResponse("配置", "请在 application-siliconflow.properties 中配置 sf.api.key");
        }

        this.httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .build();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * 检查服务是否可用
     *
     * @return 是否可用
     */
    public boolean isAvailable() {
        return available;
    }

    /**
     * 获取模型名称
     *
     * @return 模型标识
     */
    public String getModelName() {
        return modelName;
    }

    /**
     * 计算单条 query-document 相关性分数
     *
     * @param query 查询文本
     * @param document 文档文本
     * @return 相关性分数 (0-1，越高越相关)
     */
    public float score(String query, String document) {
        if (!available) {
            return generateMockScore(query, document);
        }

        try {
            List<RerankResult> results = rerank(query, List.of(document), 1);
            if (!results.isEmpty()) {
                return results.get(0).getScore();
            }
        } catch (Exception e) {
            System.err.println("  Reranker 评分失败: " + e.getMessage() + "，使用模拟分数");
        }
        return generateMockScore(query, document);
    }

    /**
     * 对文档列表进行重排序
     *
     * @param query 查询文本
     * @param documents 文档列表
     * @return 重排序结果 (按分数降序)
     */
    public List<RerankResult> rerank(String query, List<String> documents) {
        return rerank(query, documents, documents.size());
    }

    /**
     * 对文档列表进行重排序 (限制返回数量)
     *
     * @param query 查询文本
     * @param documents 文档列表
     * @param topK 返回前 topK 个结果
     * @return 重排序结果
     */
    public List<RerankResult> rerank(String query, List<String> documents, int topK) {
        if (documents == null || documents.isEmpty()) {
            return new ArrayList<>();
        }

        if (!available) {
            // 模拟模式
            List<RerankResult> mockResults = new ArrayList<>();
            for (int i = 0; i < Math.min(documents.size(), topK); i++) {
                mockResults.add(new RerankResult(i, documents.get(i), generateMockScore(query, documents.get(i))));
            }
            // 按分数降序排序
            mockResults.sort((a, b) -> Float.compare(b.getScore(), a.getScore()));
            return mockResults;
        }

        try {
            return fetchRerank(query, documents, topK);
        } catch (Exception e) {
            System.err.println("  Reranker 失败: " + e.getMessage() + "，使用模拟结果");
            List<RerankResult> mockResults = new ArrayList<>();
            for (int i = 0; i < Math.min(documents.size(), topK); i++) {
                mockResults.add(new RerankResult(i, documents.get(i), generateMockScore(query, documents.get(i))));
            }
            return mockResults;
        }
    }

    /**
     * 调用 SiliconFlow Rerank API
     */
    private List<RerankResult> fetchRerank(String query, List<String> documents, int topK)
            throws IOException, InterruptedException {

        StringBuilder docsBuilder = new StringBuilder("[\n");
        for (int i = 0; i < documents.size(); i++) {
            if (i > 0) docsBuilder.append(",\n");
            docsBuilder.append("      \"").append(documents.get(i).replace("\"", "\\\"")).append("\"");
        }
        docsBuilder.append("\n    ]");

        String requestBody = String.format("""
            {
                "model": "%s",
                "query": "%s",
                "documents": %s,
                "top_n": %d,
                "return_doc": false
            }
            """, modelName, query.replace("\"", "\\\""), docsBuilder.toString(), topK);

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(apiUrl))
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer " + apiKey)
            .POST(HttpRequest.BodyPublishers.ofString(requestBody))
            .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IOException("Rerank API 失败: " + response.statusCode() + " - " + response.body());
        }

        // 解析 SiliconFlow 响应
        JsonNode root = objectMapper.readTree(response.body());
        JsonNode results = root.get("results");

        List<RerankResult> rerankResults = new ArrayList<>();
        for (JsonNode item : results) {
            int index = item.get("index").asInt();
            float score = (float) item.get("relevance_score").asDouble();
            String text = documents.get(index);
            rerankResults.add(new RerankResult(index, text, score));
        }

        return rerankResults;
    }

    /**
     * 生成模拟分数 (基于文本哈希)
     */
    private float generateMockScore(String query, String document) {
        int hash = (query + ":" + document).hashCode();
        return Math.abs(hash % 10000) / 10000.0f;
    }

    /**
     * 重排序结果
     */
    public static class RerankResult {
        private final int index;
        private final String text;
        private final float score;

        public RerankResult(int index, String text, float score) {
            this.index = index;
            this.text = text;
            this.score = score;
        }

        public int getIndex() {
            return index;
        }

        public String getText() {
            return text;
        }

        public float getScore() {
            return score;
        }

        @Override
        public String toString() {
            return String.format("[%d] score=%.4f: %s...", index, score,
                text.substring(0, Math.min(50, text.length())));
        }
    }
}
