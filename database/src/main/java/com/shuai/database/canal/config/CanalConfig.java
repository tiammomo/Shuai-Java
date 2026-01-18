package com.shuai.database.canal.config;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetSocketAddress;

/**
 * Canal 配置类
 *
 * Canal 工作流程：
 * 1. MySQL 产生 binlog
 * 2. Canal 模拟 MySQL slave 订阅 binlog
 * 3. Canal Server 解析 binlog 并存储
 * 4. Canal Client 连接到 Server 订阅数据变更
 *
 * @author Shuai
 */
@Configuration
public class CanalConfig {

    @Value("${canal.host:localhost}")
    private String canalHost;

    @Value("${canal.port:11111}")
    private int canalPort;

    @Value("${canal.destination:example}")
    private String destination;

    @Value("${canal.username:canal}")
    private String username;

    @Value("${canal.password:canal}")
    private String password;

    @Value("${canal.batchSize:1000}")
    private int batchSize;

    @Value("${canal.timeout:60000}")
    private long timeout;

    /**
     * 创建 Canal 连接器（单例模式）
     */
    @Bean
    public CanalConnector canalConnector() {
        // 单机模式：直接连接 Canal Server
        CanalConnector connector = CanalConnectors.newSingleConnector(
            new InetSocketAddress(canalHost, canalPort),
            destination,
            username,
            password
        );

        // 集群模式：使用 Zookeeper 注册中心
        // CanalConnector connector = CanalConnectors.newClusterConnector(
        //     "zookeeper://localhost:2181",
        //     destination,
        //     username,
        //     password
        // );

        connector.connect();
        connector.subscribe();  // 订阅所有表
        // connector.subscribe("db_name.table_name");  // 订阅指定表

        return connector;
    }

    /**
     * 获取连接参数
     */
    public String getCanalHost() {
        return canalHost;
    }

    public int getCanalPort() {
        return canalPort;
    }

    public String getDestination() {
        return destination;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public long getTimeout() {
        return timeout;
    }
}
