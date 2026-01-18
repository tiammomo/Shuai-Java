package com.shuai.database.redis;

import com.shuai.database.redis.connection.ConnectionManager;
import com.shuai.database.redis.demo.DataStructureDemo;
import com.shuai.database.redis.demo.DistributedLockDemo;
import com.shuai.database.redis.demo.JedisDemo;
import redis.clients.jedis.Jedis;

/**
 * Redis 缓存模块入口类
 *
 * 模块概述
 * ----------
 * 本模块演示 Redis 缓存、分布式锁与数据结构的完整实践。
 *
 * 核心内容
 * ----------
 * - 数据结构：String、List、Set、Hash、ZSet、Stream
 * - 分布式锁：Jedis 锁方案、Redisson 分布式锁
 * - 缓存策略：Cache Aside、延迟双删
 * - Jedis 客户端：连接池、管道、事务、Lua 脚本
 *
 * @author Shuai
 * @version 1.0
 */
public class RedisDemo {

    public void runAllDemos() {
        System.out.println("=".repeat(50));
        System.out.println("       Redis 缓存模块");
        System.out.println("=".repeat(50));

        // 初始化连接池
        ConnectionManager.init();

        try {
            // 数据结构演示
            try (Jedis jedis = ConnectionManager.getConnection()) {
                DataStructureDemo dataDemo = new DataStructureDemo(jedis);
                dataDemo.runAllDemos();
            }

            // 分布式锁演示
            try (Jedis jedis = ConnectionManager.getConnection()) {
                DistributedLockDemo lockDemo = new DistributedLockDemo(
                    jedis,
                    ConnectionManager.getJedisPool()
                );
                lockDemo.runAllDemos();
            }

            // Jedis 客户端演示
            JedisDemo jedisDemo = new JedisDemo(ConnectionManager.getJedisPool());
            jedisDemo.runAllDemos();

        } catch (Exception e) {
            System.err.println("[错误] Redis 演示异常: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // 关闭连接池
            ConnectionManager.close();
        }
    }

    public static void main(String[] args) {
        new RedisDemo().runAllDemos();
    }
}
