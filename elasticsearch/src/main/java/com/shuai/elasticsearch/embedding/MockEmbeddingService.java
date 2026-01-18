package com.shuai.elasticsearch.embedding;

import com.shuai.elasticsearch.util.ResponsePrinter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Mock 向量嵌入服务 (开发测试用)
 * <p>
 * 模块概述
 * ----------
 * 本模块提供模拟的文本向量化功能，用于开发测试阶段。
 * <p>
 * 特点说明
 * ----------
 *   - 无需外部依赖
 *   - 向量基于文本哈希生成，一致性好
 *   - 相同的文本产生相同的向量
 *   - ⚠️ 向量无实际语义含义，仅用于演示
 * <p>
 * 使用场景
 * ----------
 *   - 开发阶段功能验证
 *   - CI/CD 单元测试
 *   - 演示环境快速启动
 *   - 集成测试
 * <p>
 * 切换到真实模型
 * ----------
 * {@code
 * // 通过环境变量切换
 * export EMBEDDING_TYPE=bge  // 使用 BGE 模型
 * export EMBEDDING_TYPE=openai  // 使用 OpenAI API
 * }
 *
 * @author Shuai
 * @version 1.0
 * @since 2026-01-17
 */
public class MockEmbeddingService implements EmbeddingService {

    private static final int DEFAULT_DIMENSION = 384;
    private final int dimension;
    private final Random random;

    /**
     * 默认构造函数 (384 维)
     */
    public MockEmbeddingService() {
        this(DEFAULT_DIMENSION);
    }

    /**
     * 自定义维度构造函数
     *
     * @param dimension 向量维度
     */
    public MockEmbeddingService(int dimension) {
        this.dimension = dimension;
        this.random = new Random(42); // 固定种子保证一致性

        ResponsePrinter.printMethodInfo("MockEmbeddingService", "Mock 向量服务初始化 (维度: " + dimension + ")");
    }

    @Override
    public float[] embed(String text) {
        if (text == null || text.isEmpty()) {
            return new float[dimension];
        }

        // 基于文本内容生成一致性向量
        float[] vector = new float[dimension];
        Random textRandom = new Random(text.hashCode());

        for (int i = 0; i < dimension; i++) {
            vector[i] = textRandom.nextFloat() * 2 - 1;
        }

        return normalizeVector(vector);
    }

    @Override
    public List<float[]> embedBatch(List<String> texts) {
        List<float[]> vectors = new ArrayList<>();
        for (String text : texts) {
            vectors.add(embed(text));
        }
        return vectors;
    }

    @Override
    public int getDimension() {
        return dimension;
    }

    @Override
    public String getModelName() {
        return "mock-v1.0";
    }

    @Override
    public boolean isAvailable() {
        return true;
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
