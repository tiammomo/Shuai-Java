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
 * ============================================================
 * Redis 核心数据结构详解
 * ------------------------------------------------------------
 * Redis 之所以强大，在于它提供了丰富的数据结构。
 * 每种数据结构都有其特定的使用场景和优势。
 *
 * 本演示涵盖：
 * 1. String    - 字符串/缓存/计数器
 * 2. List      - 列表/消息队列/栈
 * 3. Set       - 集合/标签/去重
 * 4. Hash      - 哈希/对象存储
 * 5. ZSet      - 有序集合/排行榜
 * 6. Stream    - 流/消息队列（5.0+）
 *
 * ============================================================
 * 选择数据结构的依据
 * ------------------------------------------------------------
 * 1. 是否需要排序？
 *    - 需要：ZSet
 *    - 不需要：List/Set
 *
 * 2. 是否需要唯一性？
 *    - 需要：Set
 *    - 不需要：List
 *
 * 3. 是否需要按字段访问？
 *    - 需要：Hash
 *    - 不需要：String
 *
 * 4. 是否需要范围查询？
 *    - 需要：ZSet（按分数范围）
 *    - List（按索引范围）
 *
 * @author Shuai
 */
public class DataStructureDemo {

    // ==================== 配置常量 ====================
    /** Redis 键前缀，用于演示数据隔离 */
    private final Jedis jedis;

    /** Redis 键前缀 */
    private static final String KEY_PREFIX = "demo:";

    /** 日期格式 */
    private static final DateTimeFormatter DATE_FORMAT =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // ==================== 构造方法 ====================

    /**
     * 构造函数
     *
     * @param jedis Redis 客户端实例
     */
    public DataStructureDemo(Jedis jedis) {
        this.jedis = jedis;
    }

    // ==================== 主入口方法 ====================

    /**
     * 执行所有数据结构演示
     *
     * 演示流程：
     * 1. DataGenerator      -> 生成真实测试数据
     * 2. stringOperations() -> String 类型操作
     * 3. listOperations()   -> List 类型操作
     * 4. setOperations()    -> Set 类型操作
     * 5. hashOperations()   -> Hash 类型操作
     * 6. zsetOperations()   -> ZSet 类型操作
     * 7. streamOperations() -> Stream 类型介绍
     */
    public void runAllDemos() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("       Redis 数据结构演示");
        System.out.println("=".repeat(50));

        // 生成真实测试数据
        System.out.println("\n--- 生成真实测试数据 ---");
        DataGenerator dataGenerator = new DataGenerator(jedis);
        dataGenerator.generateAll();

        // 演示各种数据结构
        stringOperations();   // String: 字符串、计数器
        listOperations();     // List: 消息队列、栈
        setOperations();      // Set: 标签、集合运算
        hashOperations();     // Hash: 对象存储
        zsetOperations();     // ZSet: 排行榜
        streamOperations();   // Stream: 消息流

        // 清理测试数据
        cleanup();

        System.out.println("\n[成功] 数据结构演示完成！");
    }

    // ==================== String 数据结构 ====================

    /**
     * String 数据结构演示
     *
     * ============================================================
     * String 类型特性
     * ------------------------------------------------------------
     * String 是 Redis 最基本、最常用的数据类型。
     * 它可以存储任何二进制安全的数据（最大 512MB）。
     *
     * 底层实现：
     * - 小字符串（< 44字节）：使用 embstr 编码（连续内存）
     * - 大字符串：使用 raw 编码（独立内存）
     * - 数字：使用 int 编码（紧凑存储）
     *
     * ============================================================
     * 常用命令及使用场景
     * ------------------------------------------------------------
     * 基础操作：
     * - SET key value           : 设置值
     * - GET key                 : 获取值
     * - DEL key                 : 删除键
     * - EXISTS key              : 检查键是否存在
     *
     * 过期操作：
     * - SETEX key seconds value : 设置值并指定过期时间
     * - TTL key                 : 查看剩余生存时间
     * - PERSIST key             : 移除过期时间
     *
     * 数值操作：
     * - INCR key               : 原子自增 1
     * - INCRBY key n           : 原子自增 n
     * - DECR key               : 原子自减 1
     * - DECRBY key n           : 原子自减 n
     *
     * ============================================================
     * 业务场景
     * ------------------------------------------------------------
     * 1. 缓存
     *    - 缓存数据库查询结果
     *    - 缓存页面 HTML
     *    - 缓存 Session
     *
     * 2. 计数器
     *    - 页面访问量（PV）
     *    - 用户访问次数
     *    - 商品销量
     *
     * 3. 分布式锁
     *    - SETNX + SETEX 组合
     *    - 或 SET key value NX EX seconds
     *
     * 4. ID 生成器
     *    - INCR 生成唯一 ID
     *    - 订单号、消息 ID
     */
    public void stringOperations() {
        System.out.println("\n--- String 数据结构 ---");

        // 使用真实用户数据
        UserDataGenerator.User user = UserDataGenerator.generate();

        // ==================== 场景1：会话存储 ====================
        // 使用 SETEX 设置带过期时间的值
        // 场景：Web 会话存储，30分钟后自动过期
        String sessionKey = KEY_PREFIX + "session:user:" + user.getId();
        jedis.setex(sessionKey, 1800, user.getUsername() + ":" + user.getEmail());

        Long ttl = jedis.ttl(sessionKey);
        System.out.println("  [会话] 键: " + sessionKey);
        System.out.println("  [会话] 值: " + jedis.get(sessionKey));
        System.out.println("  [会话] 剩余时间: " + ttl + "秒");

        // ==================== 场景2：访问计数器 ====================
        // 使用 INCR 实现原子自增
        // 场景：用户访问次数统计
        String viewCountKey = KEY_PREFIX + "user:" + user.getId() + ":view_count";
        jedis.set(viewCountKey, "0");

        // INCR 是原子操作，线程安全
        Long incr = jedis.incr(viewCountKey);           // 0 -> 1
        Long incrBy = jedis.incrBy(viewCountKey, 100);  // 1 -> 101
        System.out.println("  [计数器] 当前值: " + jedis.get(viewCountKey));

        // ==================== 场景3：订单号生成器 ====================
        // 使用 INCR 生成唯一序号
        // 场景：订单号、流水号生成
        String orderNoKey = KEY_PREFIX + "order:no:generator";
        Long orderId = jedis.incr(orderNoKey);

        // 组合生成订单号：时间戳 + 序号
        String orderNo = "ORD" + System.currentTimeMillis() +
                        String.format("%04d", orderId);
        System.out.println("  [生成器] 订单号: " + orderNo);

        // 清理
        jedis.del(sessionKey, viewCountKey, orderNoKey);
    }

    // ==================== List 数据结构 ====================

    /**
     * List 数据结构演示
     *
     * ============================================================
     * List 类型特性
     * ------------------------------------------------------------
     * List 是有序的字符串列表，支持从两端操作。
     * 元素按插入顺序排列，可以重复。
     *
     * 底层实现：
     * - 短列表（< 512元素）：使用 ziplist（压缩列表）
     * - 长列表：使用 quicklist（快速列表，是 ziplist 的双向链表）
     *
     * ============================================================
     * 常用命令
     * ------------------------------------------------------------
     * 插入操作：
     * - LPUSH key value [value...] : 从左侧插入
     * - RPUSH key value [value...] : 从右侧插入
     * - LINSERT key BEFORE|AFTER pivot value : 在指定元素前后插入
     *
     * 获取操作：
     * - LRANGE key start stop      : 范围查询（支持负索引）
     * - LINDEX key index           : 按索引获取
     * - LLEN key                   : 列表长度
     *
     * 删除操作：
     * - LPOP key                   : 从左侧弹出
     * - RPOP key                   : 从右侧弹出
     * - LREM key count value       : 移除指定元素
     * - LTRIM key start stop       : 裁剪列表
     *
     * ============================================================
     * 业务场景
     * ------------------------------------------------------------
     * 1. 消息队列
     *    - LPUSH 生产消息
     *    - RPOP 消费消息
     *    - 简单队列场景
     *
     * 2. 最新 N 条数据
     *    - LPUSH 新数据
     *    - LTRIM 保持固定长度
     *    - LRANGE 获取最新数据
     *
     * 3. 栈（后进先出）
     *    - LPUSH + LPOP
     *
     * 4. 队列（先进先出）
     *    - LPUSH + RPOP
     *    - 或 RPUSH + LPOP
     */
    public void listOperations() {
        System.out.println("\n--- List 数据结构 ---");

        // 使用真实订单数据
        List<OrderDataGenerator.Order> orders =
            OrderDataGenerator.generateListForUser(1001L, 5);

        String queueKey = KEY_PREFIX + "order:queue";

        // ==================== 场景：订单消息队列 ====================
        // LPUSH / RPUSH 从左侧/右侧插入
        System.out.println("  [队列] 生产订单消息:");
        for (OrderDataGenerator.Order order : orders) {
            // LPUSH 从左侧插入，新订单在最前面
            jedis.lpush(queueKey, order.getOrderNo());
        }

        Long queueSize = jedis.llen(queueKey);
        System.out.println("  [队列] 当前长度: " + queueSize);

        // ==================== 场景：消费订单 ====================
        // LPOP / RPOP 从左侧/右侧弹出
        System.out.println("  [队列] 消费订单:");
        for (int i = 0; i < 3; i++) {
            // LPOP 从左侧弹出（先入先出）
            String orderNo = jedis.lpop(queueKey);
            System.out.println("    处理: " + orderNo);
        }

        // ==================== 查看剩余 ====================
        // LRANGE 按范围获取元素
        // 0 到 -1 表示获取全部
        List<String> remainingOrders = jedis.lrange(queueKey, 0, -1);
        System.out.println("  [队列] 剩余: " + remainingOrders.size() + " 个");

        // 清理
        jedis.del(queueKey);
    }

    // ==================== Set 数据结构 ====================

    /**
     * Set 数据结构演示
     *
     * ============================================================
     * Set 类型特性
     * ------------------------------------------------------------
     * Set 是无序、唯一的字符串集合。
     * 不允许重复元素，查找时间复杂度 O(1)。
     *
     * 底层实现：
     * - 短集合（< 512元素）：使用 intset（整数集合）
     * - 长集合：使用 hashtable（哈希表）
     *
     * ============================================================
     * 常用命令
     * ------------------------------------------------------------
     * 基本操作：
     * - SADD key member [member...] : 添加元素（自动去重）
     * - SREM key member [member...] : 移除元素
     * - SMEMBERS key                : 获取所有元素
     * - SISMEMBER key member        : 判断元素是否存在
     * - SCARD key                   : 集合大小
     *
     * 集合运算：
     * - SINTER key [key...]         : 交集
     * - SUNION key [key...]         : 并集
     * - SDIFF key [key...]          : 差集
     * - SINTERSTORE dest key [key...] : 交集并存储
     *
     * ============================================================
     * 业务场景
     * ------------------------------------------------------------
     * 1. 标签系统
     *    - 商品标签
     *    - 用户兴趣
     *
     * 2. 好友关系
     *    - 共同好友
     *    - 关注/粉丝
     *
     * 3. 去重
     *    - 页面 UV 统计
     *    - 投票去重
     *
     * 4. 推荐系统
     *    - 交集推荐
     *    - 协同过滤
     */
    public void setOperations() {
        System.out.println("\n--- Set 数据结构 ---");

        // ==================== 场景1：商品标签 ====================
        // 使用 SADD 添加标签，自动去重
        String productTagsKey = KEY_PREFIX + "product:tags:" + System.currentTimeMillis();
        jedis.sadd(productTagsKey,
            "智能手机", "5G", "高清摄像", "快充", "大屏",
            "游戏手机", "拍照手机", "续航强"
        );

        // SMEMBERS 获取所有元素（无序）
        Set<String> tags = jedis.smembers(productTagsKey);
        System.out.println("  [标签] 商品标签集合: " + tags.size() + " 个");

        // SISMEMBER 判断元素是否存在
        Boolean has5G = jedis.sismember(productTagsKey, "5G");
        System.out.println("  [判断] 是否支持5G: " + (has5G ? "是" : "否"));

        // SCARD 获取集合大小
        Long tagCount = jedis.scard(productTagsKey);
        System.out.println("  [统计] 标签数量: " + tagCount);

        // ==================== 场景2：兴趣推荐（交集） ====================
        // 用户兴趣标签
        String userInterestKey = KEY_PREFIX + "user:interests:1001";
        jedis.sadd(userInterestKey, "电子产品", "运动", "旅游", "美食");

        // 商品标签（科技类）
        String interestTagsKey = KEY_PREFIX + "product:tags:tech";
        jedis.sadd(interestTagsKey, "电子产品", "智能家居", "可穿戴设备");

        // SINTER 计算交集 - 用户兴趣与科技商品的交集
        Set<String> recommendations = jedis.sinter(userInterestKey, interestTagsKey);
        System.out.println("  [推荐] 基于兴趣的推荐: " + recommendations);

        // 清理
        jedis.del(productTagsKey, userInterestKey, interestTagsKey);
    }

    // ==================== Hash 数据结构 ====================

    /**
     * Hash 数据结构演示
     *
     * ============================================================
     * Hash 类型特性
     * ------------------------------------------------------------
     * Hash 是键值对的集合，适合存储对象。
     * 每个 Hash 可以存储数十亿个字段。
     *
     * 底层实现：
     * - 短哈希（< 512字段）：使用 ziplist
     * - 长哈希：使用 hashtable
     *
     * ============================================================
     * 常用命令
     * ------------------------------------------------------------
     * 单字段操作：
     * - HSET key field value      : 设置单个字段
     * - HGET key field            : 获取单个字段
     * - HDEL key field [field...] : 删除字段
     * - HEXISTS key field         : 检查字段是否存在
     *
     * 多字段操作：
     * - HMSET key field value [field value...] : 设置多个字段
     * - HMGET key field [field...]             : 获取多个字段
     * - HGETALL key                            : 获取所有字段值
     *
     * 数字操作：
     * - HINCRBY key field increment  : 原子自增
     * - HINCRBYFLOAT key field increment : 浮点自增
     *
     * ============================================================
     * 业务场景
     * ------------------------------------------------------------
     * 1. 对象存储
     *    - 用户信息
     *    - 商品信息
     *    - 配置信息
     *
     * 2. 计数器
     *    - 用户访问次数
     *    - 商品点击量
     *
     * 3. 购物车
     *    - 商品 ID -> 数量
     */
    public void hashOperations() {
        System.out.println("\n--- Hash 数据结构 ---");

        // 使用真实用户数据
        UserDataGenerator.User user = UserDataGenerator.generate();

        String userKey = KEY_PREFIX + "user:profile:" + user.getId();

        // ==================== 场景：用户信息存储 ====================
        // HMSET / HSET 设置多个字段
        java.util.HashMap<String, String> userHash = new java.util.HashMap<>();
        userHash.put("id", String.valueOf(user.getId()));
        userHash.put("username", user.getUsername());
        userHash.put("email", user.getEmail());
        userHash.put("phone", user.getPhone());
        userHash.put("status", user.getStatus());
        userHash.put("city", user.getCity());
        userHash.put("balance", user.getBalance().toString());

        jedis.hset(userKey, userHash);

        // HGET 获取单个字段
        String username = jedis.hget(userKey, "username");
        System.out.println("  [获取] 用户名: " + username);

        // HGETALL 获取所有字段值
        Map<String, String> userInfo = jedis.hgetAll(userKey);
        System.out.println("  [获取] 完整信息: " + userInfo.size() + " 个字段");

        // ==================== 场景：计数器 ====================
        // HINCRBY 原子自增
        jedis.hset(userKey, "age", String.valueOf(user.getAge()));
        jedis.hincrBy(userKey, "age", 1);  // 年龄 + 1
        System.out.println("  [自增] 年龄+1: " + jedis.hget(userKey, "age"));

        // 清理
        jedis.del(userKey);
    }

    // ==================== ZSet 数据结构 ====================

    /**
     * ZSet 数据结构演示
     *
     * ============================================================
     * ZSet 类型特性
     * ------------------------------------------------------------
     * ZSet（有序集合）是带分数的有序集合。
     * 每个元素都关联一个分数，按分数排序。
     *
     * 底层实现：
     * - 短 ZSet（< 128元素）：使用 ziplist
     * - 长 ZSet：使用 skiplist（跳表）
     *
     * ============================================================
     * 常用命令
     * ------------------------------------------------------------
     * 基本操作：
     * - ZADD key score member [score member...] : 添加元素
     * - ZSCORE key member                       : 获取元素分数
     * - ZRANK key member                        : 获取元素排名（升序）
     * - ZREVRANK key member                     : 获取元素排名（降序）
     * - ZREM key member [member...]             : 移除元素
     *
     * 范围查询：
     * - ZRANGE key start stop [WITHSCORES]     : 按排名范围（升序）
     * - ZREVRANGE key start stop [WITHSCORES]  : 按排名范围（降序）
     * - ZRANGEBYSCORE key min max [WITHSCORES] : 按分数范围
     * - ZCOUNT key min max                      : 统计分数范围内的元素
     *
     * ============================================================
     * 业务场景
     * ------------------------------------------------------------
     * 1. 排行榜
     *    - 销售排行榜
     *    - 积分排行榜
     *    - 游戏排名
     *
     * 2. 延时队列
     *    - 分数为执行时间戳
     *    - ZRANGEBYSCORE 获取待执行任务
     *
     * 3. 权重任务
     *    - 分数为优先级
     *    - ZPOPMAX 获取最高优先级
     */
    public void zsetOperations() {
        System.out.println("\n--- ZSet 数据结构 ---");

        String rankingKey = KEY_PREFIX + "product:sales:ranking";

        // 使用真实商品数据
        List<ProductDataGenerator.Product> products =
            ProductDataGenerator.generateList(10);

        // ==================== 场景：销量排行榜 ====================
        // ZADD 添加元素（分数，成员）
        System.out.println("  [排行] 添加商品:");
        for (ProductDataGenerator.Product product : products) {
            jedis.zadd(rankingKey, product.getSalesCount(), product.getName());
        }

        // ==================== 获取 TOP 排名 ====================
        // ZREVRANGE 按分数降序查询
        // 0 到 4 获取前 5 名
        List<String> topProducts = jedis.zrevrange(rankingKey, 0, 4);
        System.out.println("  [排行] 销量 TOP5:");
        for (int i = 0; i < topProducts.size(); i++) {
            // ZSCORE 获取元素分数
            Double score = jedis.zscore(rankingKey, topProducts.get(i));
            System.out.println("    " + (i + 1) + ". " + topProducts.get(i) +
                             " (销量: " + score.intValue() + ")");
        }

        // ==================== 查询特定商品排名 ====================
        // ZREVRANK 获取元素排名（降序，0 为最高）
        Long rank = jedis.zrevrank(rankingKey, topProducts.get(0));
        System.out.println("  [排行] " + topProducts.get(0) + " 排名: #" + (rank + 1));

        // 清理
        jedis.del(rankingKey);
    }

    // ==================== Stream 数据结构 ====================

    /**
     * Stream 数据结构演示
     *
     * ============================================================
     * Stream 类型特性
     * ------------------------------------------------------------
     * Stream 是 Redis 5.0 引入的新数据类型。
     * 专门用于实现消息队列和事件流。
     *
     * 特性：
     * - 消息持久化（自动持久化到 RDB/AOF）
     * - 消息 ID（自动生成，时间戳 + 序号）
     * - 消费者组（支持多个消费者并行消费）
     * - 消息确认（ACK 机制）
     * - 范围查询（基于消息 ID）
     *
     * ============================================================
     * 与 List 消息队列对比
     * ------------------------------------------------------------
     *              List              Stream
     * -------------------------------------------------
     * 持久化      需手动            自动
     * 消费者组    不支持            支持
     * 消息确认    不支持            支持
     * 消息 ID     无                自动生成
     * 范围查询    索引              ID 范围
     *
     * ============================================================
     * 常用命令
     * ------------------------------------------------------------
     * 生产消息：
     * - XADD key [NOMKSTREAM] [MAXLEN ~ count] [LIMIT count] * field value ...
     *   * 表示自动生成 ID
     *
     * 消费消息：
     * - XREAD [COUNT count] [BLOCK milliseconds] STREAMS key [key...] ID [ID...]
     *   $ 表示最新消息之后
     *
     * 消费者组：
     * - XGROUP CREATE key groupname ID [MKSTREAM]
     * - XREADGROUP GROUP groupname consumer COUNT count STREAMS key >
     *
     * ============================================================
     * 业务场景
     * ------------------------------------------------------------
     * 1. 消息队列
     *    - 订单事件
     *    - 日志收集
     *    - 异步通知
     *
     * 2. 事件溯源
     *    - 状态变更记录
     *    - 操作审计
     *
     * 3. 实时流处理
     *    - 传感器数据
     *    - 用户行为追踪
     */
    public void streamOperations() {
        System.out.println("\n--- Stream 数据结构 ---");
        System.out.println("  Stream 是 Redis 5.0+ 新增的消息流数据类型");
        System.out.println("  ");
        System.out.println("  [特性]");
        System.out.println("    - 消息持久化存储");
        System.out.println("    - 支持消费者组");
        System.out.println("    - 消息确认机制");
        System.out.println("    - 按 ID 范围查询");
        System.out.println("  ");
        System.out.println("  [示例命令]");
        System.out.println("    # 添加消息");
        System.out.println("    XADD mystream * event ORDER_CREATED order_no ORD123");
        System.out.println("    ");
        System.out.println("    # 读取消息（阻塞）");
        System.out.println("    XREAD COUNT 5 BLOCK 5000 STREAMS mystream $");
        System.out.println("    ");
        System.out.println("    # 创建消费者组");
        System.out.println("    XGROUP CREATE mystream order-group 0");
        System.out.println("    ");
        System.out.println("    # 消费者组消费");
        System.out.println("    XREADGROUP GROUP order-group worker1 COUNT 10 STREAMS mystream >");
        System.out.println("  ");
        System.out.println("  [使用建议]");
        System.out.println("    - 需要消息持久化和 ACK 使用 Stream");
        System.out.println("    - 简单队列场景可以用 List");
        System.out.println("    - 生产环境建议使用专业 MQ（Kafka/RabbitMQ）");
    }

    // ==================== 清理方法 ====================

    /**
     * 清理测试数据
     */
    private void cleanup() {
        Set<String> keys = jedis.keys(KEY_PREFIX + "*");
        if (!keys.isEmpty()) {
            jedis.del(keys.toArray(new String[0]));
        }
    }
}
