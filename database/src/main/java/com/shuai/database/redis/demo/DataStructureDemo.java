package com.shuai.database.redis.demo;

import com.shuai.database.redis.data.DataGenerator;
import com.shuai.database.redis.data.UserDataGenerator;
import com.shuai.database.redis.data.OrderDataGenerator;
import com.shuai.database.redis.data.ProductDataGenerator;
import com.shuai.database.redis.util.RedisKeys;
import redis.clients.jedis.Jedis;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Redis 数据结构演示类
 *
 * 使用真实数据演示 Redis 六种核心数据类型的操作方法。
 *
 * @author Shuai
 */
public class DataStructureDemo {

    private final Jedis jedis;
    private static final String KEY_PREFIX = "demo:";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public DataStructureDemo(Jedis jedis) {
        this.jedis = jedis;
    }

    /**
     * 执行所有数据结构演示
     */
    public void runAllDemos() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("       Redis 数据结构演示");
        System.out.println("=".repeat(50));

        // 先生成真实测试数据
        System.out.println("\n--- 生成真实测试数据 ---");
        DataGenerator dataGenerator = new DataGenerator(jedis);
        dataGenerator.generateAll();

        // 演示数据结构操作
        stringOperations();
        listOperations();
        setOperations();
        hashOperations();
        zsetOperations();
        streamOperations();

        // 清理演示数据
        cleanup();

        System.out.println("\n[成功] 数据结构演示完成！");
    }

    /**
     * String 数据结构演示 - 使用真实用户数据
     */
    public void stringOperations() {
        System.out.println("\n--- String 数据结构 ---");

        // 使用真实用户数据演示
        UserDataGenerator.User user = UserDataGenerator.generate();

        // 存储用户会话
        String sessionKey = KEY_PREFIX + "session:user:" + user.getId();
        jedis.setex(sessionKey, 1800, user.getUsername() + ":" + user.getEmail());
        Long ttl = jedis.ttl(sessionKey);
        System.out.println("  会话存储: " + sessionKey);
        System.out.println("  会话值: " + jedis.get(sessionKey));
        System.out.println("  过期时间: " + ttl + "秒");

        // 存储用户计数器
        String viewCountKey = KEY_PREFIX + "user:" + user.getId() + ":view_count";
        jedis.set(viewCountKey, "0");
        Long incr = jedis.incr(viewCountKey);
        Long incrBy = jedis.incrBy(viewCountKey, 100);
        System.out.println("  访问计数: " + jedis.get(viewCountKey));

        // 存储订单号生成器
        String orderNoKey = KEY_PREFIX + "order:no:generator";
        Long orderId = jedis.incr(orderNoKey);
        System.out.println("  生成订单号: ORD" + System.currentTimeMillis() + String.format("%04d", orderId));

        // 清理
        jedis.del(sessionKey, viewCountKey, orderNoKey);
    }

    /**
     * List 数据结构演示 - 使用真实订单数据
     */
    public void listOperations() {
        System.out.println("\n--- List 数据结构 ---");

        // 使用真实订单数据演示消息队列
        List<OrderDataGenerator.Order> orders = OrderDataGenerator.generateListForUser(1001L, 5);

        String queueKey = KEY_PREFIX + "order:queue";

        // 入队
        for (OrderDataGenerator.Order order : orders) {
            jedis.lpush(queueKey, order.getOrderNo());
        }

        Long queueSize = jedis.llen(queueKey);
        System.out.println("  队列长度: " + queueSize);

        // 出队
        for (int i = 0; i < 3; i++) {
            String orderNo = jedis.lpop(queueKey);
            System.out.println("  处理订单: " + orderNo);
        }

        // 查看队列剩余
        List<String> remainingOrders = jedis.lrange(queueKey, 0, -1);
        System.out.println("  剩余订单数: " + remainingOrders.size());

        // 清理
        jedis.del(queueKey);
    }

    /**
     * Set 数据结构演示 - 使用真实商品标签
     */
    public void setOperations() {
        System.out.println("\n--- Set 数据结构 ---");

        // 商品标签
        String productTagsKey = KEY_PREFIX + "product:tags:" + System.currentTimeMillis();
        jedis.sadd(productTagsKey,
            "智能手机", "5G", "高清摄像", "快充", "大屏", "游戏手机", "拍照手机", "续航强"
        );

        Set<String> tags = jedis.smembers(productTagsKey);
        System.out.println("  商品标签: " + tags);

        Boolean has5G = jedis.sismember(productTagsKey, "5G");
        System.out.println("  是否支持5G: " + (has5G ? "是" : "否"));

        Long tagCount = jedis.scard(productTagsKey);
        System.out.println("  标签数量: " + tagCount);

        // 用户兴趣标签（模拟）
        String userInterestKey = KEY_PREFIX + "user:interests:1001";
        jedis.sadd(userInterestKey, "电子产品", "运动", "旅游", "美食");
        String interestTagsKey = KEY_PREFIX + "product:tags:tech";
        jedis.sadd(interestTagsKey, "电子产品", "智能家居", "可穿戴设备");

        // 交集 - 推荐商品
        Set<String> recommendations = jedis.sinter(userInterestKey, interestTagsKey);
        System.out.println("  兴趣推荐: " + recommendations);

        // 清理
        jedis.del(productTagsKey, userInterestKey, interestTagsKey);
    }

    /**
     * Hash 数据结构演示 - 使用真实用户数据
     */
    public void hashOperations() {
        System.out.println("\n--- Hash 数据结构 ---");

        // 使用真实用户数据
        UserDataGenerator.User user = UserDataGenerator.generate();

        String userKey = KEY_PREFIX + "user:profile:" + user.getId();

        // 存储用户信息
        java.util.HashMap<String, String> userHash = new java.util.HashMap<>();
        userHash.put("id", String.valueOf(user.getId()));
        userHash.put("username", user.getUsername());
        userHash.put("email", user.getEmail());
        userHash.put("phone", user.getPhone());
        userHash.put("status", user.getStatus());
        userHash.put("city", user.getCity());
        userHash.put("balance", user.getBalance().toString());
        jedis.hset(userKey, userHash);

        String username = jedis.hget(userKey, "username");
        System.out.println("  用户名: " + username);

        Map<String, String> userInfo = jedis.hgetAll(userKey);
        System.out.println("  用户信息: " + userInfo.size() + " 个字段");

        // 更新余额
        jedis.hincrBy(userKey, "age", 1);
        System.out.println("  年龄+1后: " + jedis.hget(userKey, "age"));

        // 清理
        jedis.del(userKey);
    }

    /**
     * ZSet 数据结构演示 - 使用真实排行榜数据
     */
    public void zsetOperations() {
        System.out.println("\n--- ZSet 数据结构 ---");

        String rankingKey = KEY_PREFIX + "product:sales:ranking";

        // 使用真实商品数据
        List<ProductDataGenerator.Product> products = ProductDataGenerator.generateList(10);

        for (ProductDataGenerator.Product product : products) {
            jedis.zadd(rankingKey, product.getSalesCount(), product.getName());
        }

        // 销量排行（从高到低）
        List<String> topProducts = jedis.zrevrange(rankingKey, 0, 4);
        System.out.println("  销量TOP5:");
        for (int i = 0; i < topProducts.size(); i++) {
            Double score = jedis.zscore(rankingKey, topProducts.get(i));
            System.out.println("    " + (i + 1) + ". " + topProducts.get(i) + " (销量: " + score.intValue() + ")");
        }

        // 查询排名
        Long rank = jedis.zrevrank(rankingKey, topProducts.get(0));
        System.out.println("  " + topProducts.get(0) + " 排名: " + (rank + 1));

        // 清理
        jedis.del(rankingKey);
    }

    /**
     * Stream 数据结构演示
     * 注意: Stream API 需要 Jedis 4.x+, 当前版本使用简化演示
     */
    public void streamOperations() {
        System.out.println("\n--- Stream 数据结构 ---");
        System.out.println("  Stream 是 Redis 5.0+ 新增的数据类型");
        System.out.println("  用于实现消息队列、事件流等场景");
        System.out.println("  ");
        System.out.println("  示例命令:");
        System.out.println("    XADD mystream * event ORDER_CREATED order_no ORD123");
        System.out.println("    XRANGE mystream - + COUNT 10");
        System.out.println("    XREAD COUNT 5 STREAMS mystream $");
        System.out.println("  ");
        System.out.println("  当前 Jedis 版本可能不完全支持 Stream API");
        System.out.println("  请使用 redis-cli 测试: docker exec redis8 redis-cli XADD demo:events * msg 'Hello'");
    }

    private void cleanup() {
        Set<String> keys = jedis.keys(KEY_PREFIX + "*");
        if (!keys.isEmpty()) {
            jedis.del(keys.toArray(new String[0]));
        }
    }
}
