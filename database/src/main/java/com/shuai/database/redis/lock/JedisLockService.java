package com.shuai.database.redis.lock;

import com.shuai.database.redis.script.LuaScriptTemplates;
import com.shuai.database.redis.util.RedisKeys;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.params.SetParams;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Jedis 分布式锁服务
 *
 * 锁的实现原理：
 * 1. 使用 SET key value NX EX 原子性地获取锁
 * 2. value 使用 UUID 确保释放锁时只释放自己持有的锁
 * 3. 释放锁时使用 Lua 脚本验证 value 后再删除
 *
 * @author Shuai
 */
public class JedisLockService {

    private final JedisPool jedisPool;
    private final int defaultExpireSeconds;

    public JedisLockService(JedisPool jedisPool) {
        this(jedisPool, 30);
    }

    public JedisLockService(JedisPool jedisPool, int defaultExpireSeconds) {
        this.jedisPool = jedisPool;
        this.defaultExpireSeconds = defaultExpireSeconds;
    }

    public String tryLock(String resource) {
        return tryLock(resource, defaultExpireSeconds);
    }

    public String tryLock(String resource, int expireSeconds) {
        String lockKey = RedisKeys.lockKey(resource);
        String lockValue = generateLockValue();

        SetParams params = SetParams.setParams().nx().ex(expireSeconds);

        try (Jedis jedis = jedisPool.getResource()) {
            String result = jedis.set(lockKey, lockValue, params);
            if ("OK".equals(result)) {
                return lockValue;
            }
        }
        return null;
    }

    public String tryLock(String resource, long timeout, TimeUnit unit) {
        String lockKey = RedisKeys.lockKey(resource);
        String lockValue = generateLockValue();

        SetParams params = SetParams.setParams().nx().ex(unit.toSeconds(timeout));

        try (Jedis jedis = jedisPool.getResource()) {
            String result = jedis.set(lockKey, lockValue, params);
            if ("OK".equals(result)) {
                return lockValue;
            }
        }
        return null;
    }

    public boolean unlock(String resource, String lockValue) {
        String lockKey = RedisKeys.lockKey(resource);

        try (Jedis jedis = jedisPool.getResource()) {
            Object result = jedis.eval(
                LuaScriptTemplates.UNLOCK_LOCK,
                1,
                lockKey,
                lockValue
            );
            return Objects.equals(1L, result);
        }
    }

    public boolean unlock(Lock lock) {
        return unlock(lock.getResource(), lock.getLockValue());
    }

    public boolean extendLock(String resource, String lockValue, int expireSeconds) {
        String lockKey = RedisKeys.lockKey(resource);

        try (Jedis jedis = jedisPool.getResource()) {
            String currentValue = jedis.get(lockKey);
            if (lockValue.equals(currentValue)) {
                long result = jedis.expire(lockKey, expireSeconds);
                return result > 0;
            }
        }
        return false;
    }

    public boolean isLocked(String resource) {
        String lockKey = RedisKeys.lockKey(resource);
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.exists(lockKey);
        }
    }

    public long getLockTTL(String resource) {
        String lockKey = RedisKeys.lockKey(resource);
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.ttl(lockKey);
        }
    }

    private String generateLockValue() {
        return UUID.randomUUID().toString() + "-" + Thread.currentThread().threadId();
    }

    public static class Lock {
        private final String resource;
        private final String lockValue;

        public Lock(String resource, String lockValue) {
            this.resource = resource;
            this.lockValue = lockValue;
        }

        public String getResource() {
            return resource;
        }

        public String getLockValue() {
            return lockValue;
        }
    }
}
