package com.shuai.elasticsearch.util;

import com.shuai.elasticsearch.embedding.EmbeddingService;
import com.shuai.elasticsearch.embedding.EmbeddingServiceFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * 向量嵌入工具类
 * <p>
 * 模块概述
 * ----------
 * 提供文本向量化功能，支持：
 *   - 文本分块 (Chunking)
 *   - 向量生成 (支持多种 Embedding 后端)
 *   - 向量归一化
 *   - 余弦相似度计算
 * <p>
 * 向量服务配置
 * ----------
 * 通过 EmbeddingServiceFactory 管理，支持:
 *   - mock: 模拟向量 (默认，开发测试用)
 *   - bge: BGE 本地模型 (推荐生产使用)
 *   - openai: OpenAI API
 * <p>
 * 配置示例
 * ----------
 * {@code
 * // 环境变量方式
 * export EMBEDDING_TYPE=bge
 * export BGE_MODEL_NAME=BAAI/bge-small-zh
 *
 * // 或使用 OpenAI
 * export EMBEDDING_TYPE=openai
 * export OPENAI_API_KEY=sk-xxx
 * }
 * <p>
 * 核心方法
 * ----------
 *   - chunkText(): 文本分块
 *   - generateEmbedding(): 生成向量
 *   - generateEmbeddings(): 批量生成向量
 *   - normalizeVector(): 向量归一化
 *   - cosineSimilarity(): 计算相似度
 *
 * @author Shuai
 * @version 1.0
 * @since 2026-01-17
 * @see com.shuai.elasticsearch.embedding.EmbeddingService
 */
public class VectorEmbeddingUtil {

    private static final int CHUNK_SIZE = 500; // 每个文本块的最大字符数
    private static final int CHUNK_OVERLAP = 50; // 文本块重叠大小
    private static EmbeddingService embeddingService;

    /**
     * 获取当前向量维度
     */
    public static int getVectorDimension() {
        if (embeddingService == null) {
            embeddingService = EmbeddingServiceFactory.getInstance();
        }
        return embeddingService.getDimension();
    }

    /**
     * 文本分块
     *
     * 将长文本分割成多个小块，每个小块包含指定的重叠部分
     * 以保持上下文的连续性。
     *
     * @param text 原始文本
     * @return 文本块列表
     */
    public static List<TextChunk> chunkText(String text) {
        List<TextChunk> chunks = new ArrayList<TextChunk>();
        if (text == null || text.isEmpty()) {
            return chunks;
        }

        // 移除多余空白
        text = text.replaceAll("\\s+", " ").trim();

        int start = 0;
        int chunkIndex = 0;

        while (start < text.length()) {
            int end = Math.min(start + CHUNK_SIZE, text.length());

            // 如果不是最后一块，尝试在句子边界处截断
            if (end < text.length()) {
                int lastPeriod = text.lastIndexOf("。", end);
                int lastComma = text.lastIndexOf("，", end);
                int lastNewline = text.lastIndexOf("\n", end);
                int cutPoint = Math.max(Math.max(lastPeriod, lastComma), lastNewline);

                if (cutPoint > start + CHUNK_SIZE / 2) {
                    end = cutPoint + 1;
                }
            }

            String chunkContent = text.substring(start, end);
            chunks.add(new TextChunk(
                String.valueOf(chunkIndex),
                chunkContent,
                start,
                end,
                text.length()
            ));

            // 下一块的起始位置 (考虑重叠)
            start = end - CHUNK_OVERLAP;
            if (start < 0) start = 0;
            chunkIndex++;

            // 防止无限循环
            if (start >= end) {
                start = end;
            }
        }

        return chunks;
    }

    /**
     * 生成文本向量
     *
     * 使用配置的 Embedding 服务生成向量，
     * 默认使用 Mock 服务，可通过环境变量切换。
     *
     * @param text 输入文本
     * @return 归一化的向量数组
     */
    public static float[] generateEmbedding(String text) {
        if (text == null || text.isEmpty()) {
            return new float[getVectorDimension()];
        }

        // 使用 Embedding 服务生成向量
        if (embeddingService == null) {
            embeddingService = EmbeddingServiceFactory.getInstance();
        }

        return embeddingService.embed(text);
    }

    /**
     * 批量生成向量
     *
     * @param texts 文本列表
     * @return 向量列表
     */
    public static List<float[]> generateEmbeddings(List<String> texts) {
        List<float[]> embeddings = new ArrayList<float[]>();
        if (texts == null) {
            return embeddings;
        }
        for (String text : texts) {
            embeddings.add(generateEmbedding(text));
        }
        return embeddings;
    }

    /**
     * 归一化向量
     *
     * 将向量归一化到单位长度，用于余弦相似度计算
     *
     * @param vector 原始向量
     * @return 归一化后的向量
     */
    public static float[] normalizeVector(float[] vector) {
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

    /**
     * 计算余弦相似度
     *
     * @param vecA 向量A
     * @param vecB 向量B
     * @return 相似度分数 (-1 到 1)
     */
    public static float cosineSimilarity(float[] vecA, float[] vecB) {
        if (vecA == null || vecB == null || vecA.length != vecB.length) {
            return 0f;
        }

        float dotProduct = 0f;
        float normA = 0f;
        float normB = 0f;

        for (int i = 0; i < vecA.length; i++) {
            dotProduct += vecA[i] * vecB[i];
            normA += vecA[i] * vecA[i];
            normB += vecB[i] * vecB[i];
        }

        float denominator = (float) (Math.sqrt(normA) * Math.sqrt(normB));
        if (denominator == 0) {
            return 0f;
        }

        return dotProduct / denominator;
    }

    /**
     * 生成查询向量
     *
     * 对用户查询进行向量化处理
     *
     * @param query 用户查询
     * @return 查询向量
     */
    public static float[] generateQueryEmbedding(String query) {
        // 添加查询前缀 (BGE模型建议)
        String enhancedQuery = "为检索任务生成表示: " + query;
        return generateEmbedding(enhancedQuery);
    }

    /**
     * 文本块类
     */
    public static class TextChunk {
        private final String chunkId;
        private final String content;
        private final int start;
        private final int end;
        private final int totalLength;

        public TextChunk(String chunkId, String content, int start, int end, int totalLength) {
            this.chunkId = chunkId;
            this.content = content;
            this.start = start;
            this.end = end;
            this.totalLength = totalLength;
        }

        public String getChunkId() { return chunkId; }
        public String getContent() { return content; }
        public int getStart() { return start; }
        public int getEnd() { return end; }
        public int getTotalLength() { return totalLength; }
        public int getLength() { return content.length(); }

        @Override
        public String toString() {
            return String.format("[%s] (%d-%d/%d): %s...",
                chunkId, start, end, totalLength,
                content.substring(0, Math.min(50, content.length())));
        }
    }
}
