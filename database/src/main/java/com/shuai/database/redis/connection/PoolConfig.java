package com.shuai.database.redis.connection;

import redis.clients.jedis.JedisPoolConfig;

import java.time.Duration;

/**
 * Redis 连接池配置类
 *
 * 配置说明
 * ----------
 * JedisPoolConfig 提供了丰富的连接池参数配置，用于控制连接池的行为。
 *
 * @author Shuai
 * @see JedisPoolConfig
 */
public final class PoolConfig {

    private PoolConfig() {}

    /**
     * 创建默认连接池配置
     */
    public static JedisPoolConfig createDefault() {
        JedisPoolConfig config = new JedisPoolConfig();

        config.setMaxTotal(20);
        config.setMaxIdle(10);
        config.setMinIdle(5);
        config.setTestOnBorrow(true);
        config.setTestOnReturn(true);
        config.setTestWhileIdle(true);
        config.setBlockWhenExhausted(true);
        config.setMaxWait(Duration.ofMillis(3000));

        return config;
    }

    /**
     * 创建高性能连接池配置
     */
    public static JedisPoolConfig createHighPerformance() {
        JedisPoolConfig config = new JedisPoolConfig();

        config.setMaxTotal(100);
        config.setMaxIdle(50);
        config.setMinIdle(10);
        config.setTestOnBorrow(false);
        config.setTestOnReturn(true);
        config.setTestWhileIdle(true);
        config.setBlockWhenExhausted(true);
        config.setMaxWait(Duration.ofMillis(5000));

        return config;
    }

    /**
     * 创建测试环境配置
     */
    public static JedisPoolConfig createForTesting() {
        JedisPoolConfig config = new JedisPoolConfig();

        config.setMaxTotal(5);
        config.setMaxIdle(1);
        config.setMinIdle(1);
        config.setTestOnBorrow(true);
        config.setTestOnReturn(true);
        config.setTestWhileIdle(true);
        config.setBlockWhenExhausted(true);
        config.setMaxWait(Duration.ofMillis(1000));

        return config;
    }
}
