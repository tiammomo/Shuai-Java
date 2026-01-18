package com.shuai.elasticsearch.config;

import co.elastic.clients.elasticsearch.ElasticsearchAsyncClient;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Elasticsearch Java Client 配置类
 * <p>
 * 模块概述
 * ----------
 * 本模块提供 Elasticsearch 客户端的连接管理功能，
 * 支持同步和异步两种客户端模式，采用单例模式管理实例。
 * <p>
 * 核心内容
 * ----------
 *   - 同步客户端: getClient() 获取 ElasticsearchClient 实例
 *   - 异步客户端: getAsyncClient() 获取 ElasticsearchAsyncClient 实例
 *   - 配置优先级: 环境变量 > 配置文件 > 默认值
 *   - 连接管理: close() 关闭连接释放资源
 * <p>
 * 配置说明
 * ----------
 * 支持以下配置方式（优先级从高到低）：
 *   1. 环境变量: ES_HOST, ES_PORT, ES_SCHEME
 *   2. 配置文件: application.properties 中的 es.host, es.port, es.scheme
 *   3. 默认值: localhost:9200/http
 * <p>
 * 配置示例
 * ----------
 * {@code
 * // 环境变量方式
 * export ES_HOST=localhost
 * export ES_PORT=9200
 * export ES_SCHEME=http
 *
 * // 配置文件方式 (application.properties)
 * es.host=localhost
 * es.port=9200
 * es.scheme=http
 * }
 * <p>
 * 依赖服务
 * ----------
 *   - Elasticsearch 8.18.0 (默认端口: 9200)
 *   - IK 分词器插件 (可选，用于中文分词)
 *
 * @author Shuai
 * @version 1.0
 * @since 2026-01-17
 * @see <a href="https://www.elastic.co/guide/en/elasticsearch/client/java-api-client/current/index.html">Elasticsearch Java Client</a>
 */
public class ElasticsearchConfig {

    private static ElasticsearchClient client;
    private static ElasticsearchAsyncClient asyncClient;
    private static RestClient restClient;
    private static final String CONFIG_FILE = "application.properties";

    /**
     * 获取 ElasticsearchAsyncClient 单例实例
     *
     * @return ElasticsearchAsyncClient 实例
     */
    public static synchronized ElasticsearchAsyncClient getAsyncClient() {
        if (asyncClient == null) {
            asyncClient = createAsyncClient();
        }
        return asyncClient;
    }

    /**
     * 创建 ElasticsearchAsyncClient 实例
     */
    private static ElasticsearchAsyncClient createAsyncClient() {
        if (restClient == null) {
            getClient(); // 先创建同步客户端（会初始化 restClient）
        }
        ElasticsearchTransport transport = new RestClientTransport(
            restClient, createJsonMapper()
        );
        return new ElasticsearchAsyncClient(transport);
    }

    /**
     * 获取 ElasticsearchClient 单例实例
     *
     * @return ElasticsearchClient 实例
     */
    public static synchronized ElasticsearchClient getClient() {
        if (client == null) {
            client = createClient();
        }
        return client;
    }

    /**
     * 创建 ElasticsearchClient 实例
     */
    private static ElasticsearchClient createClient() {
        // 获取配置
        String host = getConfig("es.host", "localhost");
        int port = Integer.parseInt(getConfig("es.port", "9200"));
        String scheme = getConfig("es.scheme", "http");

        // 创建 RestClient
        restClient = RestClient.builder(
            new HttpHost(host, port, scheme)
        ).build();

        // 创建 Transport
        ElasticsearchTransport transport = new RestClientTransport(
            restClient, createJsonMapper()
        );

        return new ElasticsearchClient(transport);
    }

    /**
     * 创建 JSON 映射器
     */
    private static JacksonJsonpMapper createJsonMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return new JacksonJsonpMapper(objectMapper);
    }

    /**
     * 从配置文件或环境变量获取配置
     *
     * @param key          配置键
     * @param defaultValue 默认值
     * @return 配置值
     */
    private static String getConfig(String key, String defaultValue) {
        // 1. 优先从环境变量获取
        String envKey = key.replace(".", "_").toUpperCase();
        String envValue = System.getenv(envKey);
        if (envValue != null && !envValue.isEmpty()) {
            return envValue;
        }

        // 2. 从配置文件获取
        try (InputStream input = ElasticsearchConfig.class.getClassLoader()
                .getResourceAsStream(CONFIG_FILE)) {
            if (input != null) {
                Properties props = new Properties();
                props.load(input);
                String value = props.getProperty(key);
                if (value != null && !value.isEmpty()) {
                    return value;
                }
            }
        } catch (IOException e) {
            // 忽略配置文件读取错误
        }

        // 3. 返回默认值
        return defaultValue;
    }

    /**
     * 检查客户端是否已初始化
     *
     * @return 是否已初始化
     */
    public static boolean isInitialized() {
        return client != null;
    }

    /**
     * 关闭客户端连接
     *
     * @throws IOException IO异常
     */
    public static synchronized void close() throws IOException {
        if (restClient != null) {
            restClient.close();
            restClient = null;
        }
        client = null;
    }
}
