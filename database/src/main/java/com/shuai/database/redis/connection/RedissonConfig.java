package com.shuai.database.redis.connection;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

/**
 * Redisson 客户端配置
 *
 * Redisson 是 Redis 的 Java 客户端，提供了丰富的分布式数据结构
 *
 * @author Shuai
 */
public class RedissonConfig {

    private static final String REDIS_ADDRESS = "redis://localhost:6379";
    private static final String PASSWORD = "redis123";

    private static RedissonClient redissonClient;

    /**
     * 创建 Redisson 客户端（单例模式）
     */
    public static synchronized RedissonClient createClient() {
        if (redissonClient == null) {
            Config config = new Config();
            config.useSingleServer()
                .setAddress(REDIS_ADDRESS)
                .setPassword(PASSWORD)
                .setDatabase(0)
                .setConnectionMinimumIdleSize(5)
                .setConnectionPoolSize(10)
                .setRetryAttempts(3)
                .setRetryInterval(1500)
                .setTimeout(3000)
                .setConnectTimeout(10000);

            redissonClient = Redisson.create(config);
        }
        return redissonClient;
    }

    /**
     * 获取 Redisson 客户端
     */
    public static RedissonClient getClient() {
        if (redissonClient == null) {
            return createClient();
        }
        return redissonClient;
    }

    /**
     * 关闭 Redisson 客户端
     */
    public static synchronized void shutdown() {
        if (redissonClient != null) {
            redissonClient.shutdown();
            redissonClient = null;
        }
    }

    /**
     * 测试连接
     */
    public static boolean testConnection() {
        try {
            RedissonClient client = createClient();
            return !client.isShuttingDown();
        } catch (Exception e) {
            return false;
        }
    }
}
