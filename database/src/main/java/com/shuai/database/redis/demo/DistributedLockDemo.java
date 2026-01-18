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
 * 演示 Redis 分布式锁的实现，包括 Jedis 原生锁和 Redisson 锁。
 * 使用真实数据进行演示。
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
     */
    public void jedisNativeLockDemo() {
        System.out.println("\n--- Jedis 原生分布式锁 ---");

        // 使用真实用户数据
        UserDataGenerator.User user = UserDataGenerator.generate();
        String lockKey = KEY_PREFIX + "user:update:" + user.getId();
        String lockValue = "user-service-" + Thread.currentThread().threadId();
        int expireSeconds = 30;

        System.out.println("  [锁] 用户: " + user.getUsername() + " (ID: " + user.getId() + ")");
        System.out.println("  [锁] 锁键: " + lockKey);

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

        // Lua 解锁脚本
        String unlockScript =
            "if redis.call('get', KEYS[1]) == ARGV[1] then " +
            "    return redis.call('del', KEYS[1]) " +
            "else " +
            "    return 0 " +
            "end";

        Object unlockResult = jedis.eval(unlockScript, 1, lockKey, lockValue);
        System.out.println("  [Lua] 解锁结果: " + (unlockResult.equals(1L) ? "成功" : "失败"));

        // 库存扣减 Lua 脚本
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
     */
    public void redissonLockDemo() {
        System.out.println("\n--- Redisson 分布式锁 ---");

        RedissonClient redisson = null;
        try {
            // 创建 Redisson 客户端
            redisson = RedissonConfig.getClient();
            System.out.println("  [Redisson] 客户端创建成功");

            // 1. 可重入锁 - 用户信息更新
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

            // 2. 公平锁 - 订单处理（按请求顺序）
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

            // 3. 读写锁 - 商品信息读写分离
            ProductDataGenerator.Product product = ProductDataGenerator.generate();
            RReadWriteLock rwLock = redisson.getReadWriteLock(KEY_PREFIX + "product:rw:" + product.getId());
            System.out.println("\n  [读写锁] 商品: " + product.getName());

            // 写锁（独占）
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

            // 读锁（共享）
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

            // 4. 看门狗自动续期演示
            System.out.println("\n  [看门狗] 演示自动续期机制");
            RLock watchdogLock = redisson.getLock(KEY_PREFIX + "watchdog:" + System.currentTimeMillis());
            System.out.println("  [看门狗] 获取锁（未指定超时，使用看门狗自动续期）");
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
