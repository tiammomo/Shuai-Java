package com.shuai.rabbitmq.config;

import java.util.Map;
import java.util.HashMap;

/**
 * RabbitMQ 连接配置
 *
 * 代码位置: [RabbitMqConnectionConfig.java](src/main/java/com/shuai/rabbitmq/config/RabbitMqConnectionConfig.java)
 *
 * @author Shuai
 */
public class RabbitMqConnectionConfig {

    /**
     * 创建连接工厂配置
     *
     * 【代码示例】
     *   ConnectionFactory factory = new ConnectionFactory();
     *   factory = createConnectionFactory();
     *   Connection connection = factory.newConnection();
     *
     * @return 配置好的 ConnectionFactory
     */
    public ConnectionFactory createConnectionFactory() {
        /*
         * [RabbitMQ] 连接工厂配置
         *   ConnectionFactory factory = new ConnectionFactory();
         *   factory.setHost("localhost");
         *   factory.setPort(5672);
         *   factory.setUsername("guest");
         *   factory.setPassword("guest");
         *   factory.setVirtualHost("/");
         *   factory.setConnectionTimeout(30000);
         *   factory.setRequestedHeartbeat(60);
         */
        return null;
    }

    /**
     * 创建可靠连接配置
     *
     * @return 连接配置
     */
    public Map<String, Object> createReliableConfig() {
        Map<String, Object> config = new HashMap<>();

        // 自动重连
        config.put("automaticRecoveryEnabled", true);
        config.put("networkRecoveryInterval", 5000);

        // 心跳
        config.put("requestedHeartbeat", 60);

        // 连接超时
        config.put("connectionTimeout", 30000);

        return config;
    }

    /**
     * 创建高可用集群配置
     *
     * @return 集群地址列表
     */
    public String[] createClusterAddresses() {
        return new String[]{
            "node1:5672",
            "node2:5672",
            "node3:5672"
        };
    }

    // ========== 模拟类 ==========

    static class ConnectionFactory {
        public void setHost(String host) {}
        public void setPort(int port) {}
        public void setUsername(String username) {}
        public void setPassword(String password) {}
        public void setVirtualHost(String vhost) {}
        public void setConnectionTimeout(int timeout) {}
        public void setRequestedHeartbeat(int heartbeat) {}
        public void setAutomaticRecoveryEnabled(boolean enabled) {}
    }
}
