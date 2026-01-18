package com.shuai.elasticsearch.embedding;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shuai.elasticsearch.util.ResponsePrinter;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * OpenAI 向量嵌入服务实现
 * <p>
 * 模块概述
 * ----------
 * 本模块实现基于 OpenAI API 的文本向量化功能。
 * <p>
 * 支持的模型
 * ----------
 *   - text-embedding-3-small: 高性价比，256/512/1024 维
 *   - text-embedding-3-large: 高质量，1024/3072 维
 *   - text-embedding-ada-002: 兼容性好，1536 维
 * <p>
 * 使用示例
 * ----------
 * {@code
 * // 创建服务
 * OpenAIEmbeddingService service = new OpenAIEmbeddingService(
 *     "sk-xxx",  // API Key
 *     "text-embedding-3-small"  // 模型
 * );
 *
 * // 向量化
 * float[] vector = service.embed("Hello, world!");
 *
 * // 批量向量化 (推荐，最多 2048 条)
 * List<float[]> vectors = service.embedBatch(List.of("文本1", "文本2"));
 * }
 * <p>
 * 费用参考 (2024)
 * ----------
 *   - text-embedding-3-small: $0.00002 / 1K tokens
 *   - text-embedding-3-large: $0.00013 / 1K tokens
 *   - text-embedding-ada-002: $0.00010 / 1K tokens
 * <p>
 * 配置说明
 * ----------
 * {@code
 * // 环境变量
 * export OPENAI_API_KEY=sk-xxx
 * export OPENAI_EMBEDDING_MODEL=text-embedding-3-small
 * }
 *
 * @author Shuai
 * @version 1.0
 * @since 2026-01-17
 * @see <a href="https://platform.openai.com/docs/embeddings">OpenAI Embeddings</a>
 */
public class OpenAIEmbeddingService implements EmbeddingService {

    private static final String API_URL = "https://api.openai.com/v1/embeddings";
    private static final int BATCH_SIZE_LIMIT = 2048;

    private final String apiKey;
    private final String model;
    private final int dimension;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    /**
     * 构造函数
     *
     * @param apiKey OpenAI API Key
     * @param model  模型名称
     */
    public OpenAIEmbeddingService(String apiKey, String model) {
        this.apiKey = apiKey;
        this.model = model != null ? model : "text-embedding-3-small";
        this.dimension = inferDimension(this.model);
        this.httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .build();
        this.objectMapper = new ObjectMapper();

        ResponsePrinter.printMethodInfo("OpenAIEmbeddingService",
            "初始化 OpenAI Embedding: " + this.model + " (维度: " + this.dimension + ")");
    }

    /**
     * 默认构造函数
     */
    public OpenAIEmbeddingService() {
        this(System.getenv("OPENAI_API_KEY"), System.getenv("OPENAI_EMBEDDING_MODEL"));
    }

    @Override
    public float[] embed(String text) {
        if (text == null || text.isEmpty()) {
            return new float[dimension];
        }

        try {
            List<float[]> results = embedBatch(List.of(text));
            return results.isEmpty() ? new float[dimension] : results.get(0);
        } catch (Exception e) {
            System.err.println("  OpenAI Embedding 失败: " + e.getMessage());
            return new float[dimension];
        }
    }

    @Override
    public List<float[]> embedBatch(List<String> texts) {
        List<float[]> vectors = new ArrayList<>();

        if (texts == null || texts.isEmpty()) {
            return vectors;
        }

        // 分批处理
        int index = 0;
        while (index < texts.size()) {
            int end = Math.min(index + BATCH_SIZE_LIMIT, texts.size());
            List<String> batch = texts.subList(index, end);

            try {
                vectors.addAll(fetchEmbeddings(batch));
            } catch (Exception e) {
                System.err.println("  批量 Embedding 失败: " + e.getMessage());
                // 返回零向量作为降级
                for (int i = 0; i < batch.size(); i++) {
                    vectors.add(new float[dimension]);
                }
            }

            index = end;
        }

        return vectors;
    }

    @Override
    public int getDimension() {
        return dimension;
    }

    @Override
    public String getModelName() {
        return model;
    }

    @Override
    public boolean isAvailable() {
        return apiKey != null && !apiKey.isEmpty();
    }

    /**
     * 调用 OpenAI API 获取向量
     */
    private List<float[]> fetchEmbeddings(List<String> texts) throws IOException, InterruptedException {
        // 构建请求体
        String requestBody = String.format("""
            {
                "input": %s,
                "model": "%s",
                "encoding_format": "float"
            }
            """, objectMapper.writeValueAsString(texts), model);

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(API_URL))
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer " + apiKey)
            .POST(HttpRequest.BodyPublishers.ofString(requestBody))
            .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IOException("API 请求失败: " + response.statusCode() + " - " + response.body());
        }

        // 解析响应
        JsonNode root = objectMapper.readTree(response.body());
        JsonNode data = root.get("data");

        List<float[]> vectors = new ArrayList<>();
        for (JsonNode item : data) {
            JsonNode embedding = item.get("embedding");
            float[] vector = new float[embedding.size()];
            for (int i = 0; i < embedding.size(); i++) {
                vector[i] = (float) embedding.get(i).asDouble();
            }
            vectors.add(vector);
        }

        return vectors;
    }

    /**
     * 根据模型名称推断维度
     */
    private int inferDimension(String modelName) {
        if (modelName.contains("3-small")) {
            return 1536; // text-embedding-3-small 默认 1536 维
        } else if (modelName.contains("3-large")) {
            return 3072; // text-embedding-3-large 默认 3072 维
        } else {
            return 1536; // ada-002 和其他
        }
    }
}
