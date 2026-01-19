package com.shuai.database.redis.demo;

import com.shuai.database.redis.connection.RedissonConfig;
import com.shuai.database.redis.data.OrderDataGenerator;
import com.shuai.database.redis.data.ProductDataGenerator;
import com.shuai.database.redis.data.UserDataGenerator;
import com.shuai.database.redis.lock.JedisLockService;
import com.shuai.database.redis.script.LuaScriptTemplates;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.params.SetParams;

import java.util.concurrent.TimeUnit;

/**
 * 分布式锁演示类
 *
 * ============================================================
 * 什么是分布式锁？
 * ------------------------------------------------------------
 * 在分布式系统中，多个进程/服务可能同时访问共享资源，
 * 需要一种机制来保证资源的互斥访问，这就是分布式锁。
 *
 * 分布式锁的典型应用场景：
 * - 防止超卖（库存扣减）
 * - 订单状态更新
 * - 用户余额修改
 * - 缓存更新/删除
 * - 定时任务防重
 *
 * ============================================================
 * 分布式锁的实现方式
 * ------------------------------------------------------------
 * 1. Redis SET NX EX        : SET key value NX EX (SETNX + EXPIRE 原子操作)
 * 2. Redisson               : 开源 Redis 客户端，提供丰富分布式锁支持
 * 3. Zookeeper              : 基于临时顺序节点
 * 4. 数据库                 : 基于行锁或唯一索引
 *
 * ============================================================
 * Redis 分布式锁的核心问题
 * ------------------------------------------------------------
 * 1. 原子性：获取锁和设置过期时间必须原子操作
 *    - 正确: SET key value NX EX 30
 *    - 错误: SETNX key value + EXPIRE key 30 (非原子，可能死锁)
 *
 * 2. 锁误删：只能由持有者释放自己的锁
 *    - 使用唯一标识（UUID/线程ID）作为 value
 *    - 释放时先检查再删除（Lua 脚本保证原子性）
 *
 * 3. 锁过期：业务执行时间超过锁过期时间
 *    - 看门狗自动续期（Redisson）
 *    - 合理设置过期时间
 *    - 业务逻辑尽量轻量
 *
 * 4. 可重入性：同一线程可重复获取同一把锁
 *    - Redisson 支持可重入
 *    - Jedis 需自行实现
 *
 * ============================================================
 * 演示内容
 * ------------------------------------------------------------
 * 1. Jedis 原生分布式锁实现
 * 2. Lua 脚本安全释放锁
 * 3. JedisLockService 锁服务封装
 * 4. Redisson 分布式锁（可重入锁、公平锁、读写锁、看门狗）
 *
 * @author Shuai
 */
public class DistributedLockDemo {

    private static final String KEY_PREFIX = "lock:";
    private final Jedis jedis;
    private final JedisLockService lockService;

    public DistributedLockDemo(Jedis jedis, JedisPool jedisPool) {
        this.jedis = jedis;
        this.lockService = new JedisLockService(jedisPool);
    }

    /**
     * 执行所有分布式锁演示
     */
    public void runAllDemos() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("       分布式锁功能演示");
        System.out.println("=".repeat(50));

        jedisNativeLockDemo();
        jedisLuaUnlockDemo();
        jedisLockServiceDemo();
        redissonLockDemo();

        System.out.println("\n[成功] 分布式锁演示完成！");
    }

    /**
     * Jedis 原生分布式锁实现 - 使用真实用户更新场景
     *
     * ============================================================
     * SET 命令的参数说明
     * ------------------------------------------------------------
     * SET key value [NX|XX] [EX seconds|PX milliseconds]
     *
     * NX  : 键不存在时才设置（Not eXists）
     * XX  : 键存在时才设置
     * EX  : 设置过期时间（秒）
     * PX  : 设置过期时间（毫秒）
     *
     * ============================================================
     * SET vs SETNX + EXPIRE
     * ------------------------------------------------------------
     * 使用 SET ... NX EX（推荐）:
     *   - 原子操作：获取锁 + 设置过期时间一步完成
     *   - 避免死锁：即使客户端崩溃，锁也会自动过期
     *
     * 错误写法（不推荐）:
     *   SETNX lockkey value    -> 获取锁
     *   EXPIRE lockkey 30     -> 设置过期时间
     *   问题：两步操作非原子，可能在 EXPIRE 执行前崩溃导致死锁
     *
     * ============================================================
     * 锁值（lockValue）的设计
     * ------------------------------------------------------------
     * 锁值应该包含：
     * - 客户端标识：如 UUID、服务名、线程ID
     * - 唯一性：确保不同客户端的锁值不同
     *
     * 作用：释放锁时检查锁值，确保只有持有者能释放
     */
    public void jedisNativeLockDemo() {
        System.out.println("\n--- Jedis 原生分布式锁 ---");

        // 使用真实用户数据
        UserDataGenerator.User user = UserDataGenerator.generate();
        String lockKey = KEY_PREFIX + "user:update:" + user.getId();
        // 锁值包含客户端标识，用于安全释放
        String lockValue = "user-service-" + Thread.currentThread().threadId();
        int expireSeconds = 30;  // 锁过期时间（秒）

        System.out.println("  [锁] 用户: " + user.getUsername() + " (ID: " + user.getId() + ")");
        System.out.println("  [锁] 锁键: " + lockKey);
        System.out.println("  [锁] 过期时间: " + expireSeconds + "秒");

        // NX: 键不存在时才设置, EX: 设置过期时间（秒）
        String result = jedis.set(lockKey, lockValue, SetParams.setParams().nx().ex(expireSeconds));

        if ("OK".equals(result)) {
            System.out.println("  [锁] 获取锁成功");
            try {
                // 模拟用户余额更新业务逻辑
                String balanceKey = "user:balance:" + user.getId();
                jedis.set(balanceKey, user.getBalance().toString());
                System.out.println("  [业务] 用户余额: " + user.getBalance());
                System.out.println("  [业务] 状态: " + user.getStatus());
            } finally {
                // 使用 Lua 脚本安全释放锁
                Object unlockResult = jedis.eval(
                    LuaScriptTemplates.UNLOCK_LOCK,
                    1,
                    lockKey,
                    lockValue
                );
                System.out.println("  [锁] 释放锁结果: " + (unlockResult.equals(1L) ? "成功" : "失败"));
            }
        } else {
            System.out.println("  [锁] 获取锁失败");
        }

        // 清理
        jedis.del(lockKey, "user:balance:" + user.getId());
    }

    /**
     * Lua 脚本释放锁详解 - 使用真实订单处理场景
     *
     * ============================================================
     * 为什么需要 Lua 脚本释放锁？
     * ------------------------------------------------------------
     * 错误释放锁的流程：
     *   1. GET lockkey                    -> 检查锁是否是自己的
     *   2. if (value == myValue) DEL lockkey
     *
     * 问题：如果在 GET 之后 DEL 之前，锁过期被其他客户端获取，
     *       此时 DEL 会误删别人的锁。
     *
     * 解决方案：使用 Lua 脚本，将 GET 和 DEL 合并为原子操作
     *
     * ============================================================
     * Lua 脚本参数说明
     * ------------------------------------------------------------
     * EVAL script number_of_keys [key1, key2, ...] [arg1, arg2, ...]
     *
     * KEYS: 要操作的 Redis 键（数组）
     * ARGV: 传递给脚本的参数（数组）
     *
     * 示例: EVAL "..." 1 lockkey myvalue
     *       - 1 表示有 1 个 KEY
     *       - KEYS[1] = "lockkey"
     *       - ARGV[1] = "myvalue"
     *
     * ============================================================
     * 库存扣减的原子性
     * ------------------------------------------------------------
     * 秒杀场景中，库存扣减需要保证原子性：
     *   - GET stock -> 检查库存
     *   - DECR stock -> 扣减库存
     *
     * 如果中间有并发请求，可能导致超卖。
     * 使用 Lua 脚本可以原子地完成检查和扣减。
     */
    public void jedisLuaUnlockDemo() {
        System.out.println("\n--- Lua 脚本释放锁详解 ---");

        // 使用真实订单数据
        OrderDataGenerator.Order order = OrderDataGenerator.generate();
        String lockKey = KEY_PREFIX + "order:process:" + order.getOrderNo();
        String lockValue = "order-service-" + System.currentTimeMillis();

        // 获取锁
        jedis.set(lockKey, lockValue, SetParams.setParams().nx().ex(30));
        System.out.println("  [订单] 订单号: " + order.getOrderNo());
        System.out.println("  [订单] 金额: " + order.getAmount());
        System.out.println("  [锁] 获取锁: " + lockKey);

        // ============================================================
        // Lua 解锁脚本（原子操作）
        // ============================================================
        // 逻辑: if GET(key) == value then DEL(key) else return 0
        //
        // 返回值说明：
        //   1  -> 成功释放
        //   0  -> 锁不存在或不是自己的锁
        String unlockScript =
            "if redis.call('get', KEYS[1]) == ARGV[1] then " +
            "    return redis.call('del', KEYS[1]) " +
            "else " +
            "    return 0 " +
            "end";

        // 执行 Lua 脚本
        Object unlockResult = jedis.eval(unlockScript, 1, lockKey, lockValue);
        System.out.println("  [Lua] 解锁结果: " + (unlockResult.equals(1L) ? "成功" : "失败"));

        // ============================================================
        // 库存扣减 Lua 脚本（原子操作，防止超卖）
        // ============================================================
        // 逻辑: GET stock -> 检查 > 0 -> DECR -> 返回新值
        //
        // 正确处理了边界情况：
        //   - 库存为 null（不存在）
        //   - 库存 <= 0（已售罄）
        //   - 正常扣减
        String stockScript =
            "local stock = redis.call('GET', KEYS[1]) " +
            "if stock and tonumber(stock) > 0 then " +
            "    local newStock = redis.call('DECR', KEYS[1]) " +
            "    return newStock " +
            "else " +
            "    return -1 " +
            "end";

        // 使用真实商品数据
        ProductDataGenerator.Product product = ProductDataGenerator.generate();
        String stockKey = "stock:product:" + product.getId();
        jedis.set(stockKey, "100");

        System.out.println("  [库存] 商品: " + product.getName());
        System.out.println("  [库存] 初始库存: 100");

        Long newStock = (Long) jedis.eval(stockScript, 1, stockKey);
        System.out.println("  [库存] 扣减后: " + newStock);

        // 清理
        jedis.del(stockKey);
    }

    /**
     * JedisLockService 使用示例 - 使用真实秒杀场景
     */
    public void jedisLockServiceDemo() {
        System.out.println("\n--- JedisLockService 锁服务 ---");

        // 使用真实商品数据
        ProductDataGenerator.Product product = ProductDataGenerator.generate();
        String lockKey = "seckill:product:" + product.getId();

        System.out.println("  [秒杀] 商品: " + product.getName());
        System.out.println("  [秒杀] 价格: " + product.getPrice());

        // 尝试获取锁（30秒过期）
        String lockValue = lockService.tryLock(lockKey, 30);

        if (lockValue != null) {
            System.out.println("  [锁] 获取锁成功: " + lockValue.substring(0, 20) + "...");
            try {
                // 模拟秒杀业务逻辑
                String stockKey = "seckill:stock:" + product.getId();
                if (jedis.get(stockKey) == null) {
                    jedis.set(stockKey, "50");
                }
                Long stock = jedis.decr(stockKey);
                System.out.println("  [业务] 秒杀库存: " + stock);

                // 模拟业务处理，延长锁时间
                lockService.extendLock(lockKey, lockValue, 60);
                System.out.println("  [锁] 续期成功");
            } finally {
                lockService.unlock(lockKey, lockValue);
                System.out.println("  [锁] 释放锁成功");
            }
        } else {
            System.out.println("  [锁] 获取锁失败，商品可能被其他人正在秒杀");
        }

        // 测试带超时的锁获取
        String lockValue2 = lockService.tryLock(lockKey, 1000, TimeUnit.MILLISECONDS);
        if (lockValue2 != null) {
            try {
                System.out.println("  [锁] 快速获取锁成功");
            } finally {
                lockService.unlock(lockKey, lockValue2);
            }
        }

        // 查询锁状态
        System.out.println("  [锁] 是否锁定: " + lockService.isLocked(lockKey));
        System.out.println("  [锁] TTL: " + lockService.getLockTTL(lockKey));

        // 清理
        jedis.del("seckill:stock:" + product.getId());
    }

    /**
     * Redisson 分布式锁演示 - 使用真实业务场景
     *
     * ============================================================
     * Redisson 简介
     * ------------------------------------------------------------
     * Redisson 是 Redis 的 Java 客户端，提供了丰富的分布式数据结构
     * 和分布式锁支持，是 Java 领域最流行的 Redis 分布式锁方案。
     *
     * 优势：
     * - 丰富的锁类型：可重入锁、公平锁、读写锁、联锁、红锁等
     * - 看门狗自动续期：无需担心业务时间超过锁过期时间
     * - 完善的可重入机制：同一线程可重复获取同一把锁
     * - 支持异步和响应式编程
     *
     * ============================================================
     * 锁类型说明
     * ------------------------------------------------------------
     * 1. 可重入锁（RLock）
     *    - 同一线程可多次获取同一把锁
     *    - 获取次数与释放次数必须相等
     *    - 使用场景：递归调用、嵌套方法
     *
     * 2. 公平锁（RFairLock）
     *    - 按照请求顺序获取锁
     *    - 避免线程饥饿
     *    - 使用场景：需要按顺序执行的业务
     *
     * 3. 读写锁（RReadWriteLock）
     *    - 写锁是独占锁（同时只能一个写）
     *    - 读锁是共享锁（多个读可并发）
     *    - 写锁优先级高于读锁
     *    - 使用场景：读多写少的缓存场景
     *
     * 4. 看门狗（Watchdog）
     *    - 自动续期机制
     *    - 默认每 10 秒检查一次，延长过期时间
     *    - 锁未手动释放时会自动续期
     *    - 手动释放后停止续期
     *
     * ============================================================
     * 锁 API 说明
     * ------------------------------------------------------------
     * tryLock(waitTime, leaseTime, unit)
     *   - waitTime  : 等待获取锁的最长时间
     *   - leaseTime : 锁的最大持有时间
     *   - unit      : 时间单位
     *   - 返回值    : 是否获取成功
     *
     * lock(leaseTime, unit)
     *   - 如果锁可用，立即获取
     *   - 如果锁不可用，等待直到获取或超时
     *   - leaseTime = -1 表示使用看门狗自动续期
     *
     * unlock()
     *   - 释放锁
     *   - 如果锁被重入，计数器减 1
     *   - 计数器为 0 时真正删除锁
     */
    public void redissonLockDemo() {
        System.out.println("\n--- Redisson 分布式锁 ---");

        RedissonClient redisson = null;
        try {
            // 创建 Redisson 客户端
            redisson = RedissonConfig.getClient();
            System.out.println("  [Redisson] 客户端创建成功");

            // ============================================================
            // 1. 可重入锁（Reentrant Lock）- 用户信息更新
            // ============================================================
            // 可重入锁的特点：
            // - 同一线程可多次获取，锁计数器 +1
            // - 释放时计数器 -1，计数器为 0 时真正释放锁
            // - 防止递归调用时的死锁问题
            //
            // tryLock 参数说明：
            //   waitTime    = 100 毫秒（等待获取锁的最长时间）
            //   leaseTime   = 30   毫秒（锁的最大持有时间）
            //   TimeUnit    = 毫秒
            //
            // 注意：leaseTime 较短时应使用看门狗自动续期
            UserDataGenerator.User user = UserDataGenerator.generate();
            RLock userLock = redisson.getLock(KEY_PREFIX + "user:update:" + user.getId());
            System.out.println("  [可重入锁] 用户: " + user.getUsername());
            System.out.println("  [可重入锁] 状态: " + user.getStatus());

            boolean locked = userLock.tryLock(100, 30, TimeUnit.MILLISECONDS);
            if (locked) {
                try {
                    System.out.println("  [可重入锁] 获取成功");
                    // 模拟用户信息更新
                    String userInfoKey = "user:info:" + user.getId();
                    jedis.hset(userInfoKey, "username", user.getUsername());
                    jedis.hset(userInfoKey, "city", user.getCity());
                    System.out.println("  [业务] 用户信息已更新");
                } finally {
                    userLock.unlock();
                    System.out.println("  [可重入锁] 释放成功");
                }
            }

            // ============================================================
            // 2. 公平锁（Fair Lock）- 订单处理（按请求顺序）
            // ============================================================
            // 公平锁的特点：
            // - 严格按照请求顺序获取锁（FIFO 队列）
            // - 避免某些线程长期等待（线程饥饿）
            // - 性能略低于非公平锁（需维护队列）
            //
            // 使用场景：
            // - 秒杀活动（防止请求积压后集中放行）
            // - 顺序处理的消息队列
            // - 公平的优惠券发放
            //
            // lock 参数说明：
            //   不带 waitTime，表示无限等待直到获取锁
            //   leaseTime = 30 秒（锁的最大持有时间）
            RLock fairLock = redisson.getFairLock(KEY_PREFIX + "order:fair:" + System.currentTimeMillis());
            System.out.println("\n  [公平锁] 等待获取锁...");
            fairLock.lock(30, TimeUnit.SECONDS);
            try {
                System.out.println("  [公平锁] 获取成功（按请求顺序）");
                OrderDataGenerator.Order order = OrderDataGenerator.generate();
                System.out.println("  [业务] 订单号: " + order.getOrderNo());
                System.out.println("  [业务] 金额: " + order.getAmount());
            } finally {
                fairLock.unlock();
                System.out.println("  [公平锁] 释放成功");
            }

            // ============================================================
            // 3. 读写锁（ReadWrite Lock）- 商品信息读写分离
            // ============================================================
            // 读写锁的核心思想：
            // - 读操作共享：多个线程可同时读取（不加锁）
            // - 写操作独占：同时只能一个线程写入
            // - 写锁优先级：写请求会阻塞后续的读请求
            //
            // 锁的兼容性矩阵：
            //            | 读锁   | 写锁
            //   --------|--------|--------
            //   读锁    | 兼容   | 互斥
            //   写锁    | 互斥   | 互斥
            //
            // 使用场景：
            // - 缓存系统（读多写少）
            // - 配置中心
            // - 商品信息查询
            //
            // 写锁（WriteLock）：
            //   - 独占锁，同时只能有一个写锁
            //   - 写锁和读锁互斥
            //
            // 读锁（ReadLock）：
            //   - 共享锁，多个读锁可同时存在
            //   - 写锁阻塞读锁，读锁阻塞写锁
            ProductDataGenerator.Product product = ProductDataGenerator.generate();
            RReadWriteLock rwLock = redisson.getReadWriteLock(KEY_PREFIX + "product:rw:" + product.getId());
            System.out.println("\n  [读写锁] 商品: " + product.getName());

            // ============================================================
            // 获取写锁（独占模式）
            // ============================================================
            System.out.println("  [写锁] 等待获取写锁...");
            rwLock.writeLock().lock(30, TimeUnit.SECONDS);
            try {
                System.out.println("  [写锁] 获取成功");
                String productInfoKey = "product:info:" + product.getId();
                jedis.hset(productInfoKey, "name", product.getName());
                jedis.hset(productInfoKey, "price", product.getPrice().toString());
                jedis.hset(productInfoKey, "brand", product.getBrand());
                System.out.println("  [业务] 商品信息已写入");
            } finally {
                rwLock.writeLock().unlock();
                System.out.println("  [写锁] 释放成功");
            }

            // ============================================================
            // 获取读锁（共享模式）
            // ============================================================
            System.out.println("  [读锁] 等待获取读锁...");
            rwLock.readLock().lock(30, TimeUnit.SECONDS);
            try {
                System.out.println("  [读锁] 获取成功");
                String productInfoKey = "product:info:" + product.getId();
                System.out.println("  [业务] 商品信息: " + jedis.hgetAll(productInfoKey));
            } finally {
                rwLock.readLock().unlock();
                System.out.println("  [读锁] 释放成功");
            }

            // ============================================================
            // 4. 看门狗自动续期机制（Watchdog）
            // ============================================================
            // 看门狗的作用：
            // - 自动延长锁的过期时间
            // - 防止业务执行时间超过锁过期时间
            // - 无需手动续期
            //
            // 工作原理：
            // - lock() 不指定 leaseTime 时启用看门狗
            // - 默认每 10 秒检查一次锁状态
            // - 如果锁仍被持有，延长过期时间（默认 30 秒）
            // - 锁释放后停止续期
            //
            // 使用场景：
            // - 业务执行时间不确定
            // - 不想手动管理锁续期
            // - 长时间运行的业务逻辑
            //
            // 配置说明：
            // - lockWatchdogTimeout = 30000 毫秒（默认续期间隔）
            // - 可通过 Config.lockWatchdogTimeout 修改
            System.out.println("\n  [看门狗] 演示自动续期机制");
            RLock watchdogLock = redisson.getLock(KEY_PREFIX + "watchdog:" + System.currentTimeMillis());
            System.out.println("  [看门狗] 获取锁（未指定 leaseTime，使用看门狗自动续期）");
            // 不指定 leaseTime，自动使用看门狗
            watchdogLock.lock();
            try {
                System.out.println("  [看门狗] 锁已获取，30秒后自动续期");
                System.out.println("  [看门狗] 剩余TTL: " + watchdogLock.remainTimeToLive());
            } finally {
                watchdogLock.unlock();
                System.out.println("  [看门狗] 释放成功");
            }

            // 清理
            jedis.del("user:info:" + user.getId());
            jedis.del("product:info:" + product.getId());

        } catch (InterruptedException e) {
            System.out.println("  [错误] 获取锁被中断: " + e.getMessage());
            Thread.currentThread().interrupt();
        } finally {
            if (redisson != null) {
                // 不关闭全局 Redisson 客户端
                System.out.println("  [Redisson] 客户端保持运行");
            }
        }
    }
}
