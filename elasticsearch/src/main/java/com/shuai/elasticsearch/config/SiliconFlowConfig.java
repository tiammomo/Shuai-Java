package com.shuai.elasticsearch.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * SiliconFlow API 配置类
 * <p>
 * 模块概述
 * ----------
 * 本模块管理 SiliconFlow API 的配置信息，支持环境变量和配置文件两种方式。
 * <p>
 * 支持的配置项
 * ----------
 *   - API Key: SiliconFlow 访问凭证
 *   - Embedding 模型: 召回阶段使用的 Embedding 模型
 *   - Reranker 模型: 重排序阶段使用的 Reranker 模型
 *   - API URL: API 服务地址
 * <p>
 * 配置方式 (优先级从高到低)
 * ----------
 *   1. 环境变量
 *   2. 配置文件 (application-siliconflow.properties)
 *   3. 默认值
 * <p>
 * 配置示例
 * ----------
 * {@code
 * // 环境变量方式
 * export SF_API_KEY=sk-xxx
 * export SF_EMBEDDING_MODEL=BAAI/bge-large-zh-v1.5
 * export SF_RERANKER_MODEL=BAAI/bge-reranker-v2-m3
 *
 * // 配置文件方式 (application-siliconflow.properties)
 * sf.api.key=sk-xxx
 * sf.embedding.model=BAAI/bge-large-zh-v1.5
 * sf.reranker.model=BAAI/bge-reranker-v2-m3
 * }
 * <p>
 * 模型配置
 * ----------
 *   - Embedding: BAAI/bge-large-zh-v1.5 (推荐中文)
 *   - Reranker: BAAI/bge-reranker-v2-m3
 *
 * @author Shuai
 * @version 1.0
 * @since 2026-01-17
 * @see <a href="https://cloud.siliconflow.cn">SiliconFlow</a>
 */
public class SiliconFlowConfig {

    // 默认配置
    private static final String DEFAULT_API_URL = "https://api.siliconflow.cn/v1";
    private static final String DEFAULT_EMBEDDING_MODEL = "BAAI/bge-large-zh-v1.5";
    private static final String DEFAULT_RERANKER_MODEL = "BAAI/bge-reranker-v2-m3";

    // 环境变量名
    private static final String ENV_API_KEY = "SF_API_KEY";
    private static final String ENV_EMBEDDING_MODEL = "SF_EMBEDDING_MODEL";
    private static final String ENV_RERANKER_MODEL = "SF_RERANKER_MODEL";
    private static final String ENV_API_URL = "SF_API_URL";

    // 配置文件名
    private static final String CONFIG_FILE = "application-siliconflow.properties";
    private static final String PROP_API_KEY = "sf.api.key";
    private static final String PROP_EMBEDDING_MODEL = "sf.embedding.model";
    private static final String PROP_RERANKER_MODEL = "sf.reranker.model";
    private static final String PROP_API_URL = "sf.api.url";

    // 配置值
    private final String apiKey;
    private final String embeddingModel;
    private final String rerankerModel;
    private final String apiUrl;

    private static SiliconFlowConfig instance;

    /**
     * 私有构造函数，单例模式
     */
    private SiliconFlowConfig() {
        this.apiKey = getConfigValue(ENV_API_KEY, PROP_API_KEY, null);
        this.embeddingModel = getConfigValue(ENV_EMBEDDING_MODEL, PROP_EMBEDDING_MODEL, DEFAULT_EMBEDDING_MODEL);
        this.rerankerModel = getConfigValue(ENV_RERANKER_MODEL, PROP_RERANKER_MODEL, DEFAULT_RERANKER_MODEL);
        this.apiUrl = getConfigValue(ENV_API_URL, PROP_API_URL, DEFAULT_API_URL);
    }

    /**
     * 获取单例实例
     */
    public static synchronized SiliconFlowConfig getInstance() {
        if (instance == null) {
            instance = new SiliconFlowConfig();
        }
        return instance;
    }

    /**
     * 获取 API Key
     */
    public String getApiKey() {
        return apiKey;
    }

    /**
     * 获取 Embedding 模型名称
     */
    public String getEmbeddingModel() {
        return embeddingModel;
    }

    /**
     * 获取 Reranker 模型名称
     */
    public String getRerankerModel() {
        return rerankerModel;
    }

    /**
     * 获取 API URL
     */
    public String getApiUrl() {
        return apiUrl;
    }

    /**
     * 获取 Embedding API 完整地址
     */
    public String getEmbeddingUrl() {
        return apiUrl + "/embeddings";
    }

    /**
     * 获取 Reranker API 完整地址
     */
    public String getRerankerUrl() {
        return apiUrl + "/rerank";
    }

    /**
     * 检查 API Key 是否已配置
     */
    public boolean isConfigured() {
        return apiKey != null && !apiKey.isEmpty();
    }

    /**
     * 获取配置值 (环境变量 > 配置文件 > 默认值)
     */
    private String getConfigValue(String envName, String propName, String defaultValue) {
        // 1. 先检查环境变量
        String envValue = System.getenv(envName);
        if (envValue != null && !envValue.isEmpty()) {
            return envValue;
        }

        // 2. 再检查配置文件
        String propValue = getFromProperties(propName);
        if (propValue != null && !propValue.isEmpty()) {
            return propValue;
        }

        // 3. 返回默认值
        return defaultValue;
    }

    /**
     * 从配置文件读取值
     */
    private String getFromProperties(String propName) {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (is == null) {
                return null;
            }

            Properties props = new Properties();
            props.load(is);
            return props.getProperty(propName);
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * 打印当前配置信息
     */
    public void printConfig() {
        System.out.println("  SiliconFlow 配置:");
        System.out.println("    API URL: " + apiUrl);
        System.out.println("    Embedding 模型: " + embeddingModel);
        System.out.println("    Reranker 模型: " + rerankerModel);
        System.out.println("    API Key: " + (isConfigured() ? "****" : "未配置"));
    }

    @Override
    public String toString() {
        return String.format("SiliconFlowConfig{apiUrl='%s', embeddingModel='%s', rerankerModel='%s', configured=%s}",
            apiUrl, embeddingModel, rerankerModel, isConfigured());
    }
}
