package com.shuai.elasticsearch.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 向量工具类测试
 *
 * 测试向量生成、归一化等工具方法。
 *
 * @author Shuai
 * @version 1.0
 * @since 2026-01-18
 */
class VectorEmbeddingUtilTest {

    @Test
    @DisplayName("测试向量生成")
    void testGenerateEmbedding() {
        float[] vector1 = VectorEmbeddingUtil.generateEmbedding("测试文本");
        float[] vector2 = VectorEmbeddingUtil.generateEmbedding("测试文本");

        // 相同文本应该生成相同向量
        assertNotNull(vector1);
        assertNotNull(vector2);
        assertEquals(vector1.length, vector2.length);

        // 验证向量维度
        assertEquals(384, vector1.length);

        // 验证归一化
        float normalizedMag = VectorEmbeddingUtil.normalizeVector(vector1)[0];
        assertTrue(normalizedMag >= -1.0f && normalizedMag <= 1.0f);
    }

    @Test
    @DisplayName("测试向量归一化")
    void testNormalizeVector() {
        float[] vector = {3.0f, 4.0f, 0.0f};
        float[] normalized = VectorEmbeddingUtil.normalizeVector(vector);

        // 归一化后向量模长应为1
        double magnitude = 0.0;
        for (float v : normalized) {
            magnitude += v * v;
        }
        assertEquals(1.0, Math.sqrt(magnitude), 0.001);

        // 方向应该相同
        assertTrue(normalized[0] > 0);
        assertTrue(normalized[1] > 0);
    }

    @Test
    @DisplayName("测试零向量归一化")
    void testNormalizeZeroVector() {
        float[] zeroVector = {0.0f, 0.0f, 0.0f};
        float[] result = VectorEmbeddingUtil.normalizeVector(zeroVector);

        assertNotNull(result);
        assertEquals(3, result.length);
    }

    @Test
    @DisplayName("测试查询向量生成")
    void testGenerateQueryEmbedding() {
        float[] vector = VectorEmbeddingUtil.generateQueryEmbedding("查询文本");

        assertNotNull(vector);
        assertEquals(384, vector.length);
    }

    @Test
    @DisplayName("测试余弦相似度计算")
    void testCosineSimilarity() {
        float[] v1 = {1.0f, 0.0f, 0.0f};
        float[] v2 = {1.0f, 0.0f, 0.0f};
        float[] v3 = {-1.0f, 0.0f, 0.0f};
        float[] v4 = {0.0f, 1.0f, 0.0f};

        // 相同向量相似度为1
        assertEquals(1.0f, VectorEmbeddingUtil.cosineSimilarity(v1, v2), 0.001f);

        // 相反向量相似度为-1
        assertEquals(-1.0f, VectorEmbeddingUtil.cosineSimilarity(v1, v3), 0.001f);

        // 正交向量相似度为0
        assertEquals(0.0f, VectorEmbeddingUtil.cosineSimilarity(v1, v4), 0.001f);
    }

    @Test
    @DisplayName("测试批量向量生成")
    void testBatchEmbedding() {
        java.util.List<String> texts = java.util.Arrays.asList(
            "文本1",
            "文本2",
            "文本3"
        );

        java.util.List<float[]> vectors = VectorEmbeddingUtil.generateEmbeddings(texts);

        assertEquals(3, vectors.size());
        for (float[] vector : vectors) {
            assertEquals(384, vector.length);
        }
    }

    @Test
    @DisplayName("测试文本分块")
    void testChunkText() {
        String longText = "这是第一句。这是第二句。这是第三句。这是第四句。这是第五句。";

        java.util.List<VectorEmbeddingUtil.TextChunk> chunks = VectorEmbeddingUtil.chunkText(longText);

        assertFalse(chunks.isEmpty());
        for (VectorEmbeddingUtil.TextChunk chunk : chunks) {
            assertNotNull(chunk.getContent());
            assertTrue(chunk.getLength() > 0);
        }
    }

    @Test
    @DisplayName("测试空文本分块")
    void testChunkEmptyText() {
        java.util.List<VectorEmbeddingUtil.TextChunk> chunks = VectorEmbeddingUtil.chunkText(null);
        assertTrue(chunks.isEmpty());

        chunks = VectorEmbeddingUtil.chunkText("");
        assertTrue(chunks.isEmpty());
    }

    @Test
    @DisplayName("测试获取向量维度")
    void testGetVectorDimension() {
        int dimension = VectorEmbeddingUtil.getVectorDimension();
        assertEquals(384, dimension);
    }
}
