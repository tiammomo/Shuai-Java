package com.shuai.elasticsearch.embedding;

import com.shuai.elasticsearch.util.ResponsePrinter;

/**
 * Embedding 服务工厂类
 * <p>
 * 模块概述
 * ----------
 * 本模块提供 Embedding 服务的统一获取入口，根据配置自动选择合适的后端实现。
 * <p>
 * 配置优先级
 * ----------
 *   1. 环境变量: EMBEDDING_TYPE
 *   2. 系统属性: embedding.type
 *   3. 默认值: mock
 * <p>
 * 支持的服务类型
 * ----------
 * | 类型 | 说明 | 适用场景 |
 * |------|------|----------|
 * | mock | 模拟向量 | 开发测试 |
 * | bge | BGE 本地模型 | 生产环境 (推荐) |
 * | openai | OpenAI API | 快速接入 |
 * | custom | 自定义实现 | 扩展接入 |
 * <p>
 * 使用示例
 * ----------
 * {@code
 * // 获取默认服务 (根据配置)
 * EmbeddingService service = EmbeddingServiceFactory.getInstance();
 *
 * // 指定类型获取
 * EmbeddingService bgeService = EmbeddingServiceFactory.getInstance("bge");
 *
 * // 检查服务类型
 * String type = EmbeddingServiceFactory.getCurrentType();
 * }
 * <p>
 * 环境变量配置
 * ----------
 * {@code
 * # 选择服务类型: mock, bge, openai
 * export EMBEDDING_TYPE=bge
 *
 * # BGE 模型名称 (可选)
 * export BGE_MODEL_NAME=BAAI/bge-small-zh
 *
 * # OpenAI API Key (openai 类型必需)
 * export OPENAI_API_KEY=sk-xxx
 *
 * # 向量维度 (可选, 默认 384)
 * export EMBEDDING_DIMENSION=768
 * }
 *
 * @author Shuai
 * @version 1.0
 * @since 2026-01-17
 */
public class EmbeddingServiceFactory {

    private static final String CONFIG_TYPE = "EMBEDDING_TYPE";
    private static final String CONFIG_MODEL = "BGE_MODEL_NAME";
    private static final String CONFIG_DIM = "EMBEDDING_DIMENSION";

    private static final String DEFAULT_TYPE = "mock";
    private static final int DEFAULT_DIM = 384;

    private static EmbeddingService instance;
    private static String currentType;

    /**
     * 获取默认 Embedding 服务实例
     *
     * @return EmbeddingService 实现
     */
    public static synchronized EmbeddingService getInstance() {
        String type = getTypeFromConfig();
        return getInstance(type);
    }

    /**
     * 获取指定类型的 Embedding 服务实例
     *
     * @param type 服务类型 (mock, bge, openai)
     * @return EmbeddingService 实现
     */
    public static synchronized EmbeddingService getInstance(String type) {
        if (instance != null && type.equals(currentType)) {
            return instance;
        }

        currentType = type;
        instance = createService(type);

        return instance;
    }

    /**
     * 获取当前服务类型
     *
     * @return 类型名称
     */
    public static String getCurrentType() {
        if (currentType == null) {
            currentType = getTypeFromConfig();
        }
        return currentType;
    }

    /**
     * 重置实例 (用于切换配置)
     */
    public static synchronized void reset() {
        if (instance instanceof BgeEmbeddingService) {
            // BGE 模型可能占用大量内存，确保正确释放
        }
        instance = null;
        currentType = null;
    }

    /**
     * 根据配置类型创建服务
     */
    private static EmbeddingService createService(String type) {
        ResponsePrinter.printMethodInfo("EmbeddingServiceFactory", "创建 Embedding 服务: " + type);

        try {
            switch (type.toLowerCase()) {
                case "bge":
                    String modelName = System.getenv(CONFIG_MODEL);
                    return new BgeEmbeddingService(modelName);

                case "openai":
                    // OpenAI 实现
                    return createOpenAIService();

                case "mock":
                default:
                    int dim = getDimensionFromConfig();
                    return new MockEmbeddingService(dim);
            }
        } catch (Exception e) {
            ResponsePrinter.printResponse("Embedding 服务", "创建失败，回退到 Mock: " + e.getMessage());
            return new MockEmbeddingService(getDimensionFromConfig());
        }
    }

    /**
     * 创建 OpenAI Embedding 服务
     */
    private static EmbeddingService createOpenAIService() {
        String apiKey = System.getenv("OPENAI_API_KEY");
        String model = System.getenv("OPENAI_EMBEDDING_MODEL");
        model = model != null ? model : "text-embedding-3-small";

        if (apiKey == null || apiKey.isEmpty()) {
            ResponsePrinter.printResponse("OpenAI", "API Key 未配置，回退到 Mock");
            return new MockEmbeddingService();
        }

        // 返回 OpenAI 服务实例
        return new OpenAIEmbeddingService(apiKey, model);
    }

    /**
     * 从环境变量获取服务类型
     */
    private static String getTypeFromConfig() {
        String type = System.getenv(CONFIG_TYPE);
        if (type == null || type.isEmpty()) {
            type = System.getProperty("embedding.type");
        }
        return type != null && !type.isEmpty() ? type : DEFAULT_TYPE;
    }

    /**
     * 从环境变量获取向量维度
     */
    private static int getDimensionFromConfig() {
        String dimStr = System.getenv(CONFIG_DIM);
        if (dimStr == null || dimStr.isEmpty()) {
            dimStr = System.getProperty("embedding.dimension");
        }
        if (dimStr != null && !dimStr.isEmpty()) {
            try {
                return Integer.parseInt(dimStr);
            } catch (NumberFormatException e) {
                // 忽略，使用默认值
            }
        }
        return DEFAULT_DIM;
    }
}
