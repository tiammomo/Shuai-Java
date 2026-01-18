package com.shuai.elasticsearch.embedding;

import java.util.List;

/**
 * 向量嵌入服务接口
 * <p>
 * 模块概述
 * ----------
 * 本模块定义文本向量化的统一接口，支持多种 Embedding 模型后端。
 * <p>
 * 支持的后端
 * ----------
 *   - BGE (智源): 国产开源中文模型，高质量向量
 *   - OpenAI: text-embedding-ada-002, text-embedding-3-small/large
 *   - 阿里云 NLP: 中文优化，适合国内环境
 *   - M3E (Moss): 国产开源中英文模型
 * <p>
 * 使用示例
 * ----------
 * {@code
 * // 获取服务实例 (支持配置切换)
 * EmbeddingService service = EmbeddingServiceFactory.getService();
 *
 * // 单条文本向量化
 * float[] vector = service.embed("你好，世界");
 *
 * // 批量向量化
 * List<float[]> vectors = service.embedBatch(List.of("文本1", "文本2"));
 *
 * // 获取向量维度
 * int dim = service.getDimension();
 * }
 * <p>
 * 配置说明
 * ----------
 * 通过环境变量切换后端:
 *   - EMBEDDING_TYPE: bge, openai, mock (默认: mock)
 *   - EMBEDDING_MODEL: 模型名称 (如: BAAI/bge-small-zh)
 *   - EMBEDDING_API_KEY: API 密钥 (OpenAI/阿里云)
 *
 * @author Shuai
 * @version 1.0
 * @since 2026-01-17
 * @see <a href="https://github.com/FlagOpen/FlagEmbedding">FlagEmbedding</a>
 */
public interface EmbeddingService {

    /**
     * 单条文本向量化
     *
     * @param text 输入文本
     * @return 向量数组 (归一化)
     */
    float[] embed(String text);

    /**
     * 批量文本向量化
     *
     * @param texts 文本列表
     * @return 向量列表
     */
    List<float[]> embedBatch(List<String> texts);

    /**
     * 获取向量维度
     *
     * @return 维度数
     */
    int getDimension();

    /**
     * 获取模型名称
     *
     * @return 模型标识
     */
    String getModelName();

    /**
     * 检查服务是否可用
     *
     * @return 是否可用
     */
    boolean isAvailable();
}
