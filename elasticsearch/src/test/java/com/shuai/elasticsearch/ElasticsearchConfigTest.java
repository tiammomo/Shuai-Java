package com.shuai.elasticsearch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.ElasticsearchAsyncClient;
import com.shuai.elasticsearch.config.ElasticsearchConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.api.DisplayName;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Elasticsearch 客户端配置测试类
 *
 * 测试说明
 * ----------
 * 本测试类用于验证 Elasticsearch 客户端配置是否正确。
 *
 * 前置条件
 * ----------
 * 需要设置环境变量：
 *   - ES_HOST: Elasticsearch 主机地址 (默认: localhost)
 *
 * 运行方式
 * ----------
 *   ES_HOST=localhost mvn test -pl elasticsearch
 */
public class ElasticsearchConfigTest {

    /**
     * 测试同步客户端初始化
     */
    @Test
    @DisplayName("同步客户端初始化测试")
    @EnabledIfEnvironmentVariable(named = "ES_HOST", matches = ".+")
    void testSyncClientInitialization() {
        ElasticsearchClient client = ElasticsearchConfig.getClient();

        assertNotNull(client, "客户端不应为空");
        System.out.println("同步客户端初始化成功");
    }

    /**
     * 测试异步客户端初始化
     */
    @Test
    @DisplayName("异步客户端初始化测试")
    @EnabledIfEnvironmentVariable(named = "ES_HOST", matches = ".+")
    void testAsyncClientInitialization() {
        ElasticsearchAsyncClient asyncClient = ElasticsearchConfig.getAsyncClient();

        assertNotNull(asyncClient, "异步客户端不应为空");
        System.out.println("异步客户端初始化成功");
    }

    /**
     * 测试单例模式
     */
    @Test
    @DisplayName("客户端单例测试")
    @EnabledIfEnvironmentVariable(named = "ES_HOST", matches = ".+")
    void testSingletonPattern() {
        ElasticsearchClient client1 = ElasticsearchConfig.getClient();
        ElasticsearchClient client2 = ElasticsearchConfig.getClient();

        assertSame(client1, client2, "多次调用应返回同一实例");
        System.out.println("单例模式测试通过");
    }

    /**
     * 测试配置加载
     */
    @Test
    @DisplayName("配置加载测试")
    void testConfigLoading() {
        String host = System.getenv("ES_HOST");
        if (host != null) {
            System.out.println("环境变量配置 - Host: " + host);
        } else {
            System.out.println("使用默认配置 - Host: localhost");
        }
    }

    /**
     * 测试客户端连接状态
     */
    @Test
    @DisplayName("客户端连接测试")
    @EnabledIfEnvironmentVariable(named = "ES_HOST", matches = ".+")
    void testClientConnection() throws IOException {
        ElasticsearchClient client = ElasticsearchConfig.getClient();
        assertNotNull(client, "客户端不应为空");

        // 测试基本连接
        var infoResponse = client.info();
        assertNotNull(infoResponse, "响应不应为空");

        System.out.println("连接测试通过");
    }
}
