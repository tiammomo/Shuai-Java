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
 * ============================================================
 * Jedis 是什么？
 * ------------------------------------------------------------
 * Jedis 是 Redis 官方推荐的 Java 客户端库，
 * 提供了对 Redis 所有命令的简单封装。
 *
 * 与其他客户端对比：
 * - Jedis      : 轻量、简单、功能全（推荐入门）
 * - Lettuce    : 基于 Netty，支持异步，性能高
 * - Redisson   : 分布式数据结构丰富，分布式锁
 *
 * ============================================================
 * Redis 数据结构概览
 * ------------------------------------------------------------
 * Redis 不仅仅是缓存，它支持多种数据结构：
 *
 * 1. String（字符串）
 *    - 最基本的数据类型
 *    - 可以存储字符串、JSON、序列化对象
 *    - 场景：缓存、会话、计数器
 *
 * 2. Hash（哈希）
 *    - 键值对的集合，适合存储对象
 *    - 场景：用户信息、商品信息、配置
 *
 * 3. List（列表）
 *    - 有序的字符串列表
 *    - 支持两端操作（LPUSH、RPOP）
 *    - 场景：消息队列、最新N条、栈/队列
 *
 * 4. Set（集合）
 *    - 无序、唯一元素的集合
 *    - 支持交并差运算
 *    - 场景：标签、好友关系、去重
 *
 * 5. ZSet（有序集合）
 *    - 带分数的有序集合
 *    - 按分数排序，支持范围查询
 *    - 场景：排行榜、延时队列、权重任务
 *
 * 6. 其他：Bitmap、HyperLogLog、Geo、Stream
 *
 * ============================================================
 * Jedis 核心概念
 * ------------------------------------------------------------
 * 1. 连接池（JedisPool）
 *    - 管理 Jedis 连接
 *    - 避免频繁创建/销毁连接
 *    - 配置最大连接数、空闲连接等
 *
 * 2. 管道（Pipeline）
 *    - 批量执行多条命令
 *    - 减少网络往返次数
 *    - 提升批量操作性能 10-100 倍
 *
 * 3. 事务（Transaction）
 *    - MULTI/EXEC 打包多条命令
 *    - WATCH 实现乐观锁
 *    - 保证原子性
 *
 * 4. Lua 脚本
 *    - 在 Redis 服务器端执行脚本
 *    - 原子性操作
 *    - 减少网络请求
 *
 * @author Shuai
 */
public class JedisDemo {

    // ==================== 配置常量 ====================
    /** Redis 键前缀，用于演示数据隔离 */
    private static final String KEY_PREFIX = "test:";

    /** Redis 连接池 */
    private final JedisPool jedisPool;

    // ==================== 构造方法 ====================

    /**
     * 构造函数
     *
     * @param jedisPool Redis 连接池
     */
    public JedisDemo(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    // ==================== 主入口方法 ====================

    /**
     * 执行所有 Jedis 演示
     *
     * 演示流程：
     * 1. basicOperationDemo()  -> 五大基本数据结构
     * 2. pipelineDemo()        -> 管道批量操作
     * 3. transactionDemo()     -> 事务和乐观锁
     * 4. luaScriptDemo()       -> Lua 脚本秒杀
     */
    public void runAllDemos() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("       Jedis 高级功能演示");
        System.out.println("=".repeat(50));

        try {
            // 演示 1：Redis 五种基本数据结构的操作
            basicOperationDemo();

            // 演示 2：管道批量操作（性能优化）
            pipelineDemo();

            // 演示 3：事务和乐观锁（数据一致性）
            transactionDemo();

            // 演示 4：Lua 脚本（原子性操作）
            luaScriptDemo();

            System.out.println("\n[成功] Jedis 演示完成！");
        } finally {
            // 清理测试数据
            cleanup();
        }
    }

    // ==================== 基础数据结构演示 ====================

    /**
     * 基础数据结构操作演示
     *
     * 演示 Redis 五种核心数据类型的常用操作：
     * 1. String  - 用户会话存储
     * 2. Hash    - 用户信息存储
     * 3. List    - 订单队列
     * 4. Set     - 用户标签
     * 5. ZSet    - 消费排行榜
     *
     * 每种数据结构都有其特定的使用场景，
     * 选择合适的数据结构可以：
     * - 提升操作效率
     * - 简化业务逻辑
     * - 节省内存空间
     */
    public void basicOperationDemo() {
        System.out.println("\n--- 基础数据结构操作 ---");

        try (Jedis jedis = jedisPool.getResource()) {
            // 生成真实用户数据
            UserDataGenerator.User user = UserDataGenerator.generate();

            // ==================== String 操作 ====================
            // String 是 Redis 最基本的数据类型
            // 可以存储任何二进制数据（字符串、JSON、序列化对象）
            //
            // 常用命令：
            // - SET key value          : 设置值
            // - GET key                : 获取值
            // - SETEX key seconds value: 设置值并指定过期时间
            // - TTL key                : 查看剩余过期时间
            // - INCR/DECR             : 原子自增/自减
            //
            // 场景：缓存、会话、计数器、分布式锁

            String sessionKey = KEY_PREFIX + "session:" + user.getId();

            // SETEX = SET + EXPIRE，设置值并指定过期时间（秒）
            // 这里设置 1800 秒（30分钟）的会话过期时间
            jedis.setex(sessionKey, 1800, user.getUsername() + ":" + user.getEmail());

            String sessionValue = jedis.get(sessionKey);
            System.out.println("  [String] 用户会话: " + sessionValue);

            // TTL 返回剩余生存时间（秒），-1 表示永不过期，-2 表示键不存在
            System.out.println("    过期时间: " + jedis.ttl(sessionKey) + "秒");

            // ==================== Hash 操作 ====================
            // Hash 是键值对的集合，适合存储对象
            // 每个 Hash 可以存储数十亿个字段
            //
            // 常用命令：
            // - HSET key field value   : 设置单个字段
            // - HSET key map          : 设置多个字段
            // - HGET key field        : 获取单个字段
            // - HGETALL key           : 获取所有字段
            // - HINCRBY key field n   : 原子自增
            //
            // 场景：用户信息、商品信息、配置缓存

            String userKey = KEY_PREFIX + "user:profile:" + user.getId();
            Map<String, String> userHash = new HashMap<>();
            userHash.put("id", String.valueOf(user.getId()));
            userHash.put("username", user.getUsername());
            userHash.put("email", user.getEmail());
            userHash.put("phone", user.getPhone());
            userHash.put("city", user.getCity());
            userHash.put("balance", user.getBalance().toString());

            // HSET 批量设置 Hash 字段
            jedis.hset(userKey, userHash);

            // HGETALL 获取 Hash 所有字段和值
            Map<String, String> userInfo = jedis.hgetAll(userKey);
            System.out.println("  [Hash] 用户信息: " + userInfo.size() + " 个字段");

            // ==================== List 操作 ====================
            // List 是有序的字符串列表
            // 支持从两端插入和获取元素
            //
            // 常用命令：
            // - LPUSH key value       : 从左侧插入
            // - RPUSH key value       : 从右侧插入
            // - LPOP key              : 从左侧弹出
            // - RPOP key              : 从右侧弹出
            // - LRANGE key start stop : 范围查询
            // - LLEN key              : 列表长度
            //
            // 场景：消息队列、最新N条评论、栈/队列

            // 生成用户订单列表
            List<OrderDataGenerator.Order> orders = OrderDataGenerator.generateListForUser(user.getId(), 5);
            String orderQueueKey = KEY_PREFIX + "user:" + user.getId() + ":orders";

            // RPUSH 从右侧插入（追加到列表末尾）
            for (OrderDataGenerator.Order order : orders) {
                jedis.rpush(orderQueueKey, order.getOrderNo());
            }

            // LLEN 获取列表长度
            Long queueSize = jedis.llen(orderQueueKey);
            System.out.println("  [List] 订单队列: " + queueSize + " 个订单");

            // ==================== Set 操作 ====================
            // Set 是无序、唯一的字符串集合
            // 不允许重复元素，支持集合运算
            //
            // 常用命令：
            // - SADD key member       : 添加元素（自动去重）
            // - SMEMBERS key          : 获取所有元素
            // - SISMEMBER key member  : 判断元素是否存在
            // - SCARD key             : 集合大小
            // - SINTER/SUNION/SDIFF  : 交集/并集/差集
            //
            // 场景：标签系统、共同好友、去重

            String userTagsKey = KEY_PREFIX + "user:" + user.getId() + ":tags";

            // SADD 自动去重，如果元素已存在则忽略
            jedis.sadd(userTagsKey, user.getOccupation(), user.getCity(), "VIP");

            // SMEMBERS 获取集合所有元素（无序）
            Set<String> tags = jedis.smembers(userTagsKey);
            System.out.println("  [Set] 用户标签: " + tags);

            // ==================== ZSet 操作 ====================
            // ZSet（有序集合）是带分数的有序集合
            // 每个元素都关联一个分数，按分数排序
            //
            // 常用命令：
            // - ZADD key score member : 添加元素（带分数）
            // - ZRANGE key start stop : 按分数升序查询
            // - ZREVRANGE key start stop : 按分数降序查询
            // - ZINCRBY key n member   : 原子增加分数
            // - ZRANK key member       : 获取元素排名
            // - ZSCORE key member      : 获取元素分数
            //
            // 场景：排行榜、延时队列、权重任务

            String spendingRankKey = KEY_PREFIX + "user:spending:rank";

            // 生成多个用户用于排行
            UserDataGenerator.User user2 = UserDataGenerator.generate();
            UserDataGenerator.User user3 = UserDataGenerator.generate();

            // ZADD 添加元素，分数为用户余额
            jedis.zadd(spendingRankKey, user.getBalance().doubleValue(), user.getUsername());
            jedis.zadd(spendingRankKey, user2.getBalance().doubleValue(), user2.getUsername());
            jedis.zadd(spendingRankKey, user3.getBalance().doubleValue(), user3.getUsername());

            // ZREVRANGE 按分数降序查询（0到2，获取前3名）
            List<String> topUsers = jedis.zrevrange(spendingRankKey, 0, 2);
            System.out.println("  [ZSet] 消费TOP3: " + topUsers);

            // 清理演示数据
            jedis.del(sessionKey, userKey, orderQueueKey, userTagsKey, spendingRankKey);
        }
    }

    // ==================== Pipeline 管道演示 ====================

    /**
     * 管道（Pipeline）演示
     *
     * ============================================================
     * 为什么需要 Pipeline？
     * ------------------------------------------------------------
     * 默认情况下，每条 Redis 命令都需要一次网络往返。
     * 执行 100 条命令需要 100 次网络往返，这是巨大的开销。
     *
     * Pipeline 将多条命令打包，一次性发送到 Redis 服务器，
     * 然后一次性接收所有响应，将网络往返从 100 次减少到 1 次。
     *
     * ============================================================
     * 性能对比（示例数据）
     * ------------------------------------------------------------
     * 场景：插入 10000 条数据
     *
     * 普通方式：  10000 次网络往返 × 10ms = 100秒
     * Pipeline： 1 次网络往返 × 50ms = 0.05秒
     *              提升 2000 倍！
     *
     * ============================================================
     * 使用场景
     * ------------------------------------------------------------
     * - 批量写入/更新/删除
     * - 数据迁移
     * - 日志收集
     * - 统计计数
     *
     * ============================================================
     * 注意事项
     * ------------------------------------------------------------
     * - 不要一次堆积太多命令（内存压力）
     * - 建议每 1000-10000 条同步一次
     * - Pipeline 是部分原子性的
     *   （中间某条失败不会影响其他命令）
     */
    public void pipelineDemo() {
        System.out.println("\n--- Pipeline 批量操作 ---");

        try (Jedis jedis = jedisPool.getResource()) {
            // 生成 20 个真实商品数据
            List<ProductDataGenerator.Product> products = ProductDataGenerator.generateList(20);
            String productPrefix = KEY_PREFIX + "product:batch:";

            long startTime = System.currentTimeMillis();

            // 获取管道对象
            Pipeline pipeline = jedis.pipelined();

            // 批量添加命令到管道（不执行）
            for (ProductDataGenerator.Product product : products) {
                Map<String, String> productHash = new HashMap<>();
                productHash.put("id", String.valueOf(product.getId()));
                productHash.put("name", product.getName());
                productHash.put("category", product.getCategory());
                productHash.put("brand", product.getBrand());
                productHash.put("price", product.getPrice().toString());
                productHash.put("stock", String.valueOf(product.getStock()));

                // HSET 命令添加到管道
                pipeline.hset(productPrefix + product.getId(), productHash);
            }

            // sync() 一次性发送所有命令并接收响应
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

    // ==================== 事务演示 ====================

    /**
     * 事务（Transaction）演示
     *
     * ============================================================
     * Redis 事务特性
     * ------------------------------------------------------------
     * Redis 事务是一组命令的集合，有以下特点：
     *
     * 1. 批量执行：MULTI 开始，EXEC 执行中间所有命令
     * 2. 原子性：事务中的命令要么全部执行，要么全部不执行
     * 3. 隔离性：执行期间不会执行其他客户端命令
     *
     * 但 Redis 事务不支持回滚！
     * 如果某条命令执行失败，其他命令仍会继续执行。
     *
     * ============================================================
     * WATCH 实现乐观锁
     * ------------------------------------------------------------
     * WATCH 监听一个或多个键，如果事务执行前这些键被修改，
     * 则事务执行失败（返回 nil）。
     *
     * 使用流程：
     * 1. WATCH key              // 监听键
     * 2. GET key               // 读取值
     * 3. MULTI                 // 开始事务
     * 4. ... commands ...      // 打包命令
     * 5. EXEC                  // 执行事务
     *
     * ============================================================
     * 场景：库存扣减
     * ------------------------------------------------------------
     * 常见问题：并发超卖
     *
     * 错误方式：
     * GET stock      -> 100
     * DECR stock     -> 99
     * （两个请求同时读取 100，都扣到 99，超卖！）
     *
     * 正确方式：
     * WATCH stock
     * GET stock      -> 100
     * MULTI
     * DECR stock
     * EXEC           -> 原子扣减
     *
     * ============================================================
     * 注意事项
     * ------------------------------------------------------------
     * - WATCH 只监听一次，事务执行后自动 unwatch
     * - 事务执行失败返回 nil，需要在代码中处理
     * - 高并发场景建议使用 Lua 脚本替代事务
     */
    public void transactionDemo() {
        System.out.println("\n--- 事务操作 ---");

        try (Jedis jedis = jedisPool.getResource()) {
            // 使用真实商品数据
            ProductDataGenerator.Product product = ProductDataGenerator.generate();
            String stockKey = KEY_PREFIX + "product:" + product.getId() + ":stock";
            String soldKey = KEY_PREFIX + "product:" + product.getId() + ":sold";

            // 初始化库存和销量
            jedis.set(stockKey, "100");
            jedis.set(soldKey, "0");

            System.out.println("  [事务] 商品: " + product.getName());
            System.out.println("  [事务] 初始库存: " + jedis.get(stockKey));

            // 使用乐观锁
            // WATCH 监听库存键，如果被其他客户端修改，事务会失败
            jedis.watch(stockKey);

            // 读取当前库存
            Long currentStock = Long.parseLong(jedis.get(stockKey));

            // 检查库存是否充足
            if (currentStock > 0) {
                // 开始事务
                Transaction tx = jedis.multi();

                // DECR 原子自减库存
                tx.decr(stockKey);
                // INCR 原子自增销量
                tx.incr(soldKey);

                // EXEC 执行事务
                // 如果期间 stockKey 被修改，exec 返回 null
                List<Object> results = tx.exec();

                if (results != null) {
                    // 事务执行成功
                    System.out.println("  [事务] 扣减后库存: " + jedis.get(stockKey));
                    System.out.println("  [事务] 销售数量: " + jedis.get(soldKey));
                } else {
                    // 乐观锁冲突，事务失败
                    System.out.println("  [事务] 乐观锁冲突，扣减失败");
                }
            }

            // 清理
            jedis.del(stockKey, soldKey);
        }
    }

    // ==================== Lua 脚本演示 ====================

    /**
     * Lua 脚本演示 - 秒杀场景
     *
     * ============================================================
     * 为什么使用 Lua 脚本？
     * ------------------------------------------------------------
     * Lua 脚本在 Redis 服务器端执行，具有以下优势：
     *
     * 1. 原子性：脚本执行期间不会执行其他命令
     * 2. 减少网络往返：多条命令打包执行
     * 3. 复用性：脚本可以缓存和复用
     *
     * ============================================================
     * 秒杀场景分析
     * ------------------------------------------------------------
     * 秒杀的核心问题是保证原子性：
     * - 检查库存 > 0
     * - 扣减库存
     * - 生成订单
     *
     * 如果分步执行：
     * 1. GET stock   -> 1
     * 2. DECR stock -> 0
     * 3. DECR stock -> -1 （超卖！）
     *
     * 使用 Lua 脚本：
     * - 原子检查和扣减
     * - 不会超卖
     *
     * ============================================================
     * EVAL 命令
     * ------------------------------------------------------------
     * EVAL script numkeys key [key ...] arg [arg ...]
     *
     * - script   : Lua 脚本内容
     * - numkeys  : key 参数的数量
     * - key      : 在脚本中用 KEYS[1], KEYS[2] 访问
     * - arg      : 在脚本中用 ARGV[1], ARGV[2] 访问
     *
     * ============================================================
     * 秒杀 Lua 脚本解析
     * ------------------------------------------------------------
     * local stock = tonumber(redis.call('GET', KEYS[1]))
     *     -- 从 Redis 获取库存，转为数字
     *
     * if stock and stock > 0 then
     *     -- 库存存在且大于 0
     *     local newStock = redis.call('DECR', KEYS[1])
     *         -- 原子扣减库存
     *     if newStock >= 0 then
     *         return newStock  -- 返回剩余库存
     *     else
     *         redis.call('INCR', KEYS[1])
     *         -- 扣成负数了，加回去
     *         return -1  -- 返回失败
     *     end
     * else
     *     return -1  -- 库存不足，返回失败
     * end
     */
    public void luaScriptDemo() {
        System.out.println("\n--- Lua 脚本秒杀场景 ---");

        try (Jedis jedis = jedisPool.getResource()) {
            // 使用真实商品数据
            ProductDataGenerator.Product product = ProductDataGenerator.generate();
            String stockKey = KEY_PREFIX + "seckill:product:" + product.getId();
            String orderKey = KEY_PREFIX + "seckill:orders:" + product.getId();

            // 初始化秒杀库存（10件）
            jedis.set(stockKey, "10");

            System.out.println("  [Lua] 商品: " + product.getName());
            System.out.println("  [Lua] 初始库存: " + jedis.get(stockKey));

            // 秒杀 Lua 脚本
            // 脚本逻辑：原子检查库存并扣减
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

            // 模拟 12 次秒杀请求（库存只有 10 件）
            System.out.println("  [Lua] 秒杀请求:");
            int successCount = 0;
            for (int i = 1; i <= 12; i++) {
                // EVAL 执行 Lua 脚本
                // 参数：1 个 key，脚本，key 名
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

    // ==================== 清理方法 ====================

    /**
     * 清理测试数据
     *
     * 使用 KEYS 命令查找所有测试键并删除
     * 注意：生产环境应避免使用 KEYS *（会阻塞）
     * 建议使用 SCAN 命令替代
     */
    private void cleanup() {
        try (Jedis jedis = jedisPool.getResource()) {
            // KEYS 返回匹配的所有键
            // * 匹配任意字符
            Set<String> keys = jedis.keys(KEY_PREFIX + "*");

            if (!keys.isEmpty()) {
                // DEL 支持批量删除
                jedis.del(keys.toArray(new String[0]));
            }
        }
    }
}
