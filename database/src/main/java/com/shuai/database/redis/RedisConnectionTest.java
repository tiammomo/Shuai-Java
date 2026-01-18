package com.shuai.database.redis;

import com.shuai.database.redis.connection.ConnectionManager;
import redis.clients.jedis.Jedis;

/**
 * Redis 连接测试
 */
public class RedisConnectionTest {

    public static void main(String[] args) {
        System.out.println("=".repeat(50));
        System.out.println("       Redis 连接测试");
        System.out.println("=".repeat(50));

        // 初始化连接池
        System.out.println("\n[1] 初始化连接池...");
        ConnectionManager.init();
        System.out.println("    连接池初始化完成");

        // 测试连接
        System.out.println("\n[2] 测试 Redis 连接...");
        boolean connected = ConnectionManager.testConnection();
        System.out.println("    连接状态: " + (connected ? "成功" : "失败"));

        if (connected) {
            // 测试基本操作
            try (Jedis jedis = ConnectionManager.getConnection()) {
                System.out.println("\n[3] 测试基本操作...");

                // String 操作
                jedis.set("test:key", "Hello Redis 8!");
                String value = jedis.get("test:key");
                System.out.println("    String 测试: test:key = " + value);

                // Hash 操作
                jedis.hset("test:hash", "field1", "value1");
                String hashValue = jedis.hget("test:hash", "field1");
                System.out.println("    Hash 测试: test:hash.field1 = " + hashValue);

                // List 操作
                jedis.lpush("test:list", "item1", "item2", "item3");
                String listValue = jedis.lpop("test:list");
                System.out.println("    List 测试: test:list = " + listValue);

                // 删除测试数据
                jedis.del("test:key", "test:hash", "test:list");
                System.out.println("    清理测试数据完成");
            }
        }

        // 关闭连接池
        ConnectionManager.close();
        System.out.println("\n[4] 连接池已关闭");

        System.out.println("\n" + "=".repeat(50));
        System.out.println("       测试完成！");
        System.out.println("=".repeat(50));
    }
}
