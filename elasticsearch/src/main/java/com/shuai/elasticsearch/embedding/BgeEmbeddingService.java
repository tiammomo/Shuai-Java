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
 * BGE 向量嵌入服务实现 (SiliconFlow API)
 * <p>
 * 模块概述
 * ----------
 * 本模块实现基于 SiliconFlow API 的 BGE 文本向量化功能。
 * <p>
 * 实现说明
 * ----------
 * 通过 HTTP 调用 SiliconFlow API 获取真实的 BGE 向量。
 * 支持以下模型：
 *   - BAAI/bge-large-zh-v1.5 (1024维，中文最优)
 *   - BAAI/bge-base-zh-v1.5 (768维)
 *   - BAAI/bge-small-zh (384维)
 * <p>
 * BGE 模型特点
 * ----------
 *   - 国产开源，由智源研究院开发
 *   - 针对中文优化，效果优秀
 *   - 多种规格: small (384维), base (768维), large (1024维)
 *   - 使用时建议添加查询前缀: "为检索任务生成表示: "
 * <p>
 * 配置示例
 * ----------
 * {@code
 * // 配置文件: application-siliconflow.properties
 * sf.api.key=sk-xxx
 * sf.embedding.model=BAAI/bge-large-zh-v1.5
 * sf.reranker.model=BAAI/bge-reranker-v2-m3
 * sf.api.url=https://api.siliconflow.cn/v1
 * }
 * <p>
 * 免费额度参考
 * ----------
 *   - SiliconFlow: 新用户有免费额度，国内访问快
 *
 * @author Shuai
 * @version 1.0
 * @since 2026-01-17
 * @see <a href="https://github.com/FlagOpen/FlagEmbedding">FlagEmbedding</a>
 * @see <a href="https://cloud.siliconflow.cn">SiliconFlow</a>
 */
public class BgeEmbeddingService implements EmbeddingService {

    private static final String DEFAULT_MODEL = "BAAI/bge-small-zh";
    private static final int DEFAULT_DIMENSION = 384;

    private final String modelName;
    private final int dimension;
    private final String apiUrl;
    private String apiKey;
    private final String provider; // "sf" 或 "mock"
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final SiliconFlowConfig siliconFlowConfig;

    /**
     * 构造函数
     *
     * @param modelName 模型名称（可选，为 null 时从配置读取）
     */
    public BgeEmbeddingService(String modelName) {
        this.siliconFlowConfig = SiliconFlowConfig.getInstance();

        // 使用 SiliconFlow 配置
        if (siliconFlowConfig.isConfigured()) {
            this.provider = "sf";
            this.apiUrl = siliconFlowConfig.getEmbeddingUrl();
            this.apiKey = siliconFlowConfig.getApiKey();
            // 优先使用传入的模型名，否则从配置读取
            this.modelName = modelName != null ? modelName : siliconFlowConfig.getEmbeddingModel();
            this.dimension = inferDimension(this.modelName);
            ResponsePrinter.printMethodInfo("BgeEmbeddingService",
                "BGE Embedding 服务 (SiliconFlow): " + this.modelName + " (维度: " + this.dimension + ")");
        } else {
            // 无配置，回退到模拟模式
            this.provider = "mock";
            this.apiUrl = null;
            this.modelName = modelName != null ? modelName : DEFAULT_MODEL;
            this.dimension = inferDimension(this.modelName);
            ResponsePrinter.printMethodInfo("BgeEmbeddingService",
                "BGE Embedding 服务 (模拟): " + this.modelName + " (维度: " + this.dimension + ")");
            ResponsePrinter.printResponse("警告", "未配置 SiliconFlow API Key，使用模拟向量");
            ResponsePrinter.printResponse("配置", "请在 application-siliconflow.properties 中配置 sf.api.key");
        }

        this.httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .build();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * 默认构造函数 - 从配置文件读取模型配置
     */
    public BgeEmbeddingService() {
        this(null);
    }

    @Override
    public float[] embed(String text) {
        if (text == null || text.isEmpty()) {
            return new float[dimension];
        }

        // 如果没有配置 API Key，使用模拟模式
        if (!"hf".equals(provider) && !"sf".equals(provider)) {
            return generateMockVector(text);
        }

        try {
            return fetchSingleEmbedding(text);
        } catch (Exception e) {
            System.err.println("  BGE Embedding 失败: " + e.getMessage() + "，使用模拟向量");
            return generateMockVector(text);
        }
    }

    @Override
    public List<float[]> embedBatch(List<String> texts) {
        List<float[]> vectors = new ArrayList<float[]>();
        if (texts == null || texts.isEmpty()) {
            return vectors;
        }

        // 分批处理 (SiliconFlow 支持批量)
        if ("sf".equals(provider)) {
            try {
                vectors.addAll(fetchBatchEmbedding(texts));
            } catch (Exception e) {
                System.err.println("  批量 Embedding 失败: " + e.getMessage());
                for (String text : texts) {
                    vectors.add(embed(text));
                }
            }
        } else {
            // HuggingFace 需要逐条调用
            for (String text : texts) {
                vectors.add(embed(text));
            }
        }

        return vectors;
    }

    @Override
    public int getDimension() {
        return dimension;
    }

    @Override
    public String getModelName() {
        return modelName;
    }

    @Override
    public boolean isAvailable() {
        return "hf".equals(provider) || "sf".equals(provider);
    }

    /**
     * 调用 SiliconFlow API 获取单个向量
     */
    private float[] fetchSingleEmbedding(String text) throws IOException, InterruptedException {
        // 清理文本中的特殊字符
        String cleanText = text.replace("\\", "\\\\")
                               .replace("\"", "\\\"")
                               .replace("\n", "\\n")
                               .replace("\r", "\\r")
                               .replace("\t", "\\t");

        String requestBody = String.format(
            "{\"model\":\"%s\",\"input\":\"%s\",\"encoding_format\":\"float\"}",
            modelName, cleanText);

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(apiUrl))
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer " + apiKey)
            .POST(HttpRequest.BodyPublishers.ofString(requestBody))
            .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IOException("SF API 失败: " + response.statusCode() + " - " + response.body());
        }

        // 解析 SiliconFlow 响应格式: {"data": [{"embedding": [...]}]}
        JsonNode root = objectMapper.readTree(response.body());
        JsonNode embedding = root.get("data").get(0).get("embedding");
        float[] vector = new float[embedding.size()];
        for (int i = 0; i < embedding.size(); i++) {
            vector[i] = (float) embedding.get(i).asDouble();
        }
        return normalizeVector(vector);
    }

    /**
     * 调用 SiliconFlow API 获取向量 (支持批量)
     */
    private List<float[]> fetchBatchEmbedding(List<String> texts) throws IOException, InterruptedException {
        StringBuilder inputs = new StringBuilder("[");
        for (int i = 0; i < texts.size(); i++) {
            if (i > 0) inputs.append(", ");
            inputs.append("\"").append(texts.get(i).replace("\"", "\\\"")).append("\"");
        }
        inputs.append("]");

        String requestBody = String.format("""
            {
                "model": "%s",
                "input": %s,
                "encoding_format": "float"
            }
            """, modelName, inputs.toString());

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(apiUrl))
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer " + apiKey)
            .POST(HttpRequest.BodyPublishers.ofString(requestBody))
            .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IOException("SF API 失败: " + response.statusCode() + " - " + response.body());
        }

        // 解析 SiliconFlow 响应
        JsonNode root = objectMapper.readTree(response.body());
        JsonNode data = root.get("data");

        List<float[]> vectors = new ArrayList<float[]>();
        for (JsonNode item : data) {
            JsonNode embedding = item.get("embedding");
            float[] vector = new float[embedding.size()];
            for (int i = 0; i < embedding.size(); i++) {
                vector[i] = (float) embedding.get(i).asDouble();
            }
            vectors.add(normalizeVector(vector));
        }

        return vectors;
    }

    /**
     * 生成模拟向量 (一致性哈希)
     */
    private float[] generateMockVector(String text) {
        java.util.Random random = new java.util.Random(text.hashCode());
        float[] vector = new float[dimension];
        for (int i = 0; i < dimension; i++) {
            vector[i] = random.nextFloat() * 2 - 1;
        }
        return normalizeVector(vector);
    }

    /**
     * 根据模型名称推断向量维度
     */
    private int inferDimension(String modelName) {
        if (modelName == null) {
            return DEFAULT_DIMENSION;
        }
        String lower = modelName.toLowerCase();
        // 处理带版本号的模型名，如 bge-large-zh-v1.5
        if (lower.contains("small") || lower.contains("m3")) {
            return 384;
        } else if (lower.contains("base")) {
            return 768;
        } else if (lower.contains("large")) {
            return 1024;
        }
        return DEFAULT_DIMENSION;
    }

    /**
     * 向量归一化 (L2 范数)
     */
    private float[] normalizeVector(float[] vector) {
        double magnitude = 0.0;
        for (float v : vector) {
            magnitude += v * v;
        }
        magnitude = Math.sqrt(magnitude);

        if (magnitude == 0) {
            return vector;
        }

        float[] normalized = new float[vector.length];
        for (int i = 0; i < vector.length; i++) {
            normalized[i] = (float) (vector[i] / magnitude);
        }
        return normalized;
    }
}
