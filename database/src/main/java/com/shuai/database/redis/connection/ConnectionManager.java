package com.shuai.database.redis.connection;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Redis 连接管理器
 *
 * 模块概述
 * ----------
 * 提供 JedisPool 连接池的初始化、获取和关闭功能。
 * 支持密码认证和连接超时配置。
 *
 * @author Shuai
 * @see JedisPool
 */
public final class ConnectionManager {

    private static JedisPool jedisPool;
    private static JedisPoolConfig poolConfig;

    // Redis 服务器配置
    private static final String HOST = "localhost";
    private static final int PORT = 6379;
    private static final String PASSWORD = "redis123";  // Docker Redis 8 密码
    private static final int TIMEOUT = 3000;    // 连接超时 3秒

    private ConnectionManager() {}

    /**
     * 使用默认配置初始化连接池
     */
    public static synchronized void init() {
        if (jedisPool == null) {
            poolConfig = PoolConfig.createDefault();
            jedisPool = createJedisPool(HOST, PORT, PASSWORD, TIMEOUT);
        }
    }

    /**
     * 使用自定义配置初始化连接池
     *
     * @param config 连接池配置
     * @param host Redis 服务器地址
     * @param port Redis 服务器端口
     */
    public static synchronized void init(JedisPoolConfig config, String host, int port) {
        if (jedisPool == null) {
            poolConfig = config;
            jedisPool = createJedisPool(host, port, PASSWORD, TIMEOUT);
        }
    }

    /**
     * 使用自定义配置初始化连接池（支持密码）
     *
     * @param config 连接池配置
     * @param host Redis 服务器地址
     * @param port Redis 服务器端口
     * @param password Redis 密码（为空表示无需认证）
     */
    public static synchronized void init(JedisPoolConfig config, String host, int port, String password) {
        if (jedisPool == null) {
            poolConfig = config;
            jedisPool = createJedisPool(host, port, password, TIMEOUT);
        }
    }

    /**
     * 使用自定义配置初始化连接池（支持密码和超时）
     *
     * @param config 连接池配置
     * @param host Redis 服务器地址
     * @param port Redis 服务器端口
     * @param password Redis 密码（为空表示无需认证）
     * @param timeout 连接超时时间（毫秒）
     */
    public static synchronized void init(JedisPoolConfig config, String host, int port, String password, int timeout) {
        if (jedisPool == null) {
            poolConfig = config;
            jedisPool = createJedisPool(host, port, password, timeout);
        }
    }

    /**
     * 创建 JedisPool 实例
     */
    private static JedisPool createJedisPool(String host, int port, String password, int timeout) {
        return new JedisPool(poolConfig, host, port, timeout, password);
    }

    /**
     * 获取连接池实例
     *
     * @return JedisPool 实例
     */
    public static JedisPool getJedisPool() {
        if (jedisPool == null) {
            init();
        }
        return jedisPool;
    }

    /**
     * 获取 Jedis 连接
     *
     * @return Jedis 连接（需手动关闭或使用 try-with-resources）
     */
    public static Jedis getConnection() {
        return getJedisPool().getResource();
    }

    /**
     * 获取连接池配置
     *
     * @return JedisPoolConfig 实例
     */
    public static JedisPoolConfig getPoolConfig() {
        return poolConfig;
    }

    /**
     * 测试 Redis 连接是否可用
     *
     * @return true 连接正常，false 连接失败
     */
    public static boolean testConnection() {
        try (Jedis jedis = getConnection()) {
            String pong = jedis.ping();
            return "PONG".equals(pong);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 关闭连接池
     */
    public static synchronized void close() {
        if (jedisPool != null) {
            jedisPool.close();
            jedisPool = null;
        }
    }
}
