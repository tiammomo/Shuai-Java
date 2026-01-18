package com.shuai.database.redis.demo;

import com.shuai.database.redis.connection.ConnectionManager;
import com.shuai.database.redis.data.OrderDataGenerator;
import com.shuai.database.redis.data.ProductDataGenerator;
import com.shuai.database.redis.data.UserDataGenerator;
import com.shuai.database.redis.script.LuaScriptTemplates;
import com.shuai.database.redis.util.RedisKeys;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Transaction;

import java.math.BigDecimal;
import java.util.*;

/**
 * Jedis 客户端高级演示类
 *
 * 使用真实数据演示 Jedis 客户端的高级用法，包括连接池、管道、事务、Lua 脚本等。
 *
 * @author Shuai
 */
public class JedisDemo {

    private static final String KEY_PREFIX = "test:";
    private final JedisPool jedisPool;

    public JedisDemo(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    /**
     * 执行所有 Jedis 演示
     */
    public void runAllDemos() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("       Jedis 高级功能演示");
        System.out.println("=".repeat(50));

        try {
            basicOperationDemo();
            pipelineDemo();
            transactionDemo();
            luaScriptDemo();

            System.out.println("\n[成功] Jedis 演示完成！");
        } finally {
            cleanup();
        }
    }

    /**
     * 基础数据结构操作演示 - 使用真实用户数据
     */
    public void basicOperationDemo() {
        System.out.println("\n--- 基础数据结构操作 ---");

        try (Jedis jedis = jedisPool.getResource()) {
            // 使用真实用户数据
            UserDataGenerator.User user = UserDataGenerator.generate();

            // String 操作 - 用户会话
            String sessionKey = KEY_PREFIX + "session:" + user.getId();
            jedis.setex(sessionKey, 1800, user.getUsername() + ":" + user.getEmail());
            String sessionValue = jedis.get(sessionKey);
            System.out.println("  [String] 用户会话: " + sessionValue);
            System.out.println("    过期时间: " + jedis.ttl(sessionKey) + "秒");

            // Hash 操作 - 用户信息
            String userKey = KEY_PREFIX + "user:profile:" + user.getId();
            Map<String, String> userHash = new HashMap<>();
            userHash.put("id", String.valueOf(user.getId()));
            userHash.put("username", user.getUsername());
            userHash.put("email", user.getEmail());
            userHash.put("phone", user.getPhone());
            userHash.put("city", user.getCity());
            userHash.put("balance", user.getBalance().toString());
            jedis.hset(userKey, userHash);
            Map<String, String> userInfo = jedis.hgetAll(userKey);
            System.out.println("  [Hash] 用户信息: " + userInfo.size() + " 个字段");

            // List 操作 - 用户订单队列
            List<OrderDataGenerator.Order> orders = OrderDataGenerator.generateListForUser(user.getId(), 5);
            String orderQueueKey = KEY_PREFIX + "user:" + user.getId() + ":orders";
            for (OrderDataGenerator.Order order : orders) {
                jedis.rpush(orderQueueKey, order.getOrderNo());
            }
            Long queueSize = jedis.llen(orderQueueKey);
            System.out.println("  [List] 订单队列: " + queueSize + " 个订单");

            // Set 操作 - 用户标签
            String userTagsKey = KEY_PREFIX + "user:" + user.getId() + ":tags";
            jedis.sadd(userTagsKey, user.getOccupation(), user.getCity(), "VIP");
            Set<String> tags = jedis.smembers(userTagsKey);
            System.out.println("  [Set] 用户标签: " + tags);

            // ZSet 操作 - 用户消费排行
            String spendingRankKey = KEY_PREFIX + "user:spending:rank";
            UserDataGenerator.User user2 = UserDataGenerator.generate();
            UserDataGenerator.User user3 = UserDataGenerator.generate();
            jedis.zadd(spendingRankKey, user.getBalance().doubleValue(), user.getUsername());
            jedis.zadd(spendingRankKey, user2.getBalance().doubleValue(), user2.getUsername());
            jedis.zadd(spendingRankKey, user3.getBalance().doubleValue(), user3.getUsername());
            List<String> topUsers = jedis.zrevrange(spendingRankKey, 0, 2);
            System.out.println("  [ZSet] 消费TOP3: " + topUsers);

            // 清理
            jedis.del(sessionKey, userKey, orderQueueKey, userTagsKey, spendingRankKey);
        }
    }

    /**
     * 管道（Pipeline）演示 - 使用真实商品数据
     */
    public void pipelineDemo() {
        System.out.println("\n--- Pipeline 批量操作 ---");

        try (Jedis jedis = jedisPool.getResource()) {
            // 生成真实商品数据
            List<ProductDataGenerator.Product> products = ProductDataGenerator.generateList(20);
            String productPrefix = KEY_PREFIX + "product:batch:";

            long startTime = System.currentTimeMillis();

            // 使用管道批量插入
            Pipeline pipeline = jedis.pipelined();
            for (ProductDataGenerator.Product product : products) {
                Map<String, String> productHash = new HashMap<>();
                productHash.put("id", String.valueOf(product.getId()));
                productHash.put("name", product.getName());
                productHash.put("category", product.getCategory());
                productHash.put("brand", product.getBrand());
                productHash.put("price", product.getPrice().toString());
                productHash.put("stock", String.valueOf(product.getStock()));
                pipeline.hset(productPrefix + product.getId(), productHash);
            }
            pipeline.sync();

            long pipelineTime = System.currentTimeMillis() - startTime;

            // 验证数据
            String firstProductKey = productPrefix + products.get(0).getId();
            Map<String, String> firstProduct = jedis.hgetAll(firstProductKey);

            System.out.println("  [Pipeline] 批量插入 " + products.size() + " 个商品");
            System.out.println("  [Pipeline] 耗时: " + pipelineTime + "ms");
            System.out.println("  [Pipeline] 验证: " + firstProduct.get("name"));

            // 清理
            List<String> keys = new ArrayList<>();
            for (ProductDataGenerator.Product product : products) {
                keys.add(productPrefix + product.getId());
            }
            jedis.del(keys.toArray(new String[0]));
        }
    }

    /**
     * 事务（Transaction）演示 - 使用真实库存扣减场景
     */
    public void transactionDemo() {
        System.out.println("\n--- 事务操作 ---");

        try (Jedis jedis = jedisPool.getResource()) {
            // 使用真实商品数据
            ProductDataGenerator.Product product = ProductDataGenerator.generate();
            String stockKey = KEY_PREFIX + "product:" + product.getId() + ":stock";
            String soldKey = KEY_PREFIX + "product:" + product.getId() + ":sold";

            // 初始化库存
            jedis.set(stockKey, "100");
            jedis.set(soldKey, "0");

            System.out.println("  [事务] 商品: " + product.getName());
            System.out.println("  [事务] 初始库存: " + jedis.get(stockKey));

            // 演示事务扣减库存
            String balanceKey = KEY_PREFIX + "user:balance:" + product.getId();

            jedis.watch(stockKey, balanceKey);

            Long currentStock = Long.parseLong(jedis.get(stockKey));
            if (currentStock > 0) {
                Transaction tx = jedis.multi();

                tx.decr(stockKey);
                tx.incr(soldKey);

                List<Object> results = tx.exec();

                if (results != null) {
                    System.out.println("  [事务] 扣减后库存: " + jedis.get(stockKey));
                    System.out.println("  [事务] 销售数量: " + jedis.get(soldKey));
                } else {
                    System.out.println("  [事务] 乐观锁冲突，扣减失败");
                }
            }

            // 清理
            jedis.del(stockKey, soldKey);
        }
    }

    /**
     * Lua 脚本演示 - 秒杀场景
     */
    public void luaScriptDemo() {
        System.out.println("\n--- Lua 脚本秒杀场景 ---");

        try (Jedis jedis = jedisPool.getResource()) {
            // 使用真实商品数据
            ProductDataGenerator.Product product = ProductDataGenerator.generate();
            String stockKey = KEY_PREFIX + "seckill:product:" + product.getId();
            String orderKey = KEY_PREFIX + "seckill:orders:" + product.getId();

            // 初始化秒杀库存
            jedis.set(stockKey, "10");

            System.out.println("  [Lua] 商品: " + product.getName());
            System.out.println("  [Lua] 初始库存: " + jedis.get(stockKey));

            // 秒杀 Lua 脚本
            String seckillScript =
                "local stock = tonumber(redis.call('GET', KEYS[1])) " +
                "if stock and stock > 0 then " +
                "    local newStock = redis.call('DECR', KEYS[1]) " +
                "    if newStock >= 0 then " +
                "        return newStock " +
                "    else " +
                "        redis.call('INCR', KEYS[1]) " +
                "        return -1 " +
                "    end " +
                "else " +
                "    return -1 " +
                "end";

            // 模拟多次秒杀请求
            System.out.println("  [Lua] 秒杀请求:");
            int successCount = 0;
            for (int i = 1; i <= 12; i++) {
                Long result = (Long) jedis.eval(seckillScript, 1, stockKey);
                if (result >= 0) {
                    System.out.println("    请求" + i + ": 成功! 剩余库存: " + result);
                    successCount++;
                } else {
                    System.out.println("    请求" + i + ": 失败，库存不足");
                }
            }

            System.out.println("  [Lua] 秒杀成功率: " + successCount + "/12");

            // 清理
            jedis.del(stockKey);
        }
    }

    /**
     * 清理测试数据
     */
    private void cleanup() {
        try (Jedis jedis = jedisPool.getResource()) {
            Set<String> keys = jedis.keys(KEY_PREFIX + "*");
            if (!keys.isEmpty()) {
                jedis.del(keys.toArray(new String[0]));
            }
        }
    }
}
