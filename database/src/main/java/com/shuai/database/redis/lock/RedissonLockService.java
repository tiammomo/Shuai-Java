package com.shuai.database.redis.lock;

import com.shuai.database.redis.util.RedisKeys;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;

import java.util.concurrent.TimeUnit;

/**
 * Redisson 分布式锁服务封装
 *
 * @author Shuai
 * @see RLock
 */
public class RedissonLockService {

    private final RedissonClient redisson;

    public RedissonLockService(RedissonClient redisson) {
        this.redisson = redisson;
    }

    public RLock getLock(String resource) {
        return redisson.getLock(RedisKeys.lockKey(resource));
    }

    public boolean executeWithLock(String resource, long timeout, TimeUnit unit, Runnable operation) {
        RLock lock = redisson.getLock(RedisKeys.lockKey(resource));
        try {
            if (lock.tryLock(timeout, unit)) {
                try {
                    operation.run();
                    return true;
                } finally {
                    lock.unlock();
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return false;
    }

    public void executeWithLock(String resource, Runnable operation) {
        RLock lock = redisson.getLock(RedisKeys.lockKey(resource));
        try {
            lock.lock();
            operation.run();
        } finally {
            lock.unlock();
        }
    }

    public RLock getFairLock(String resource) {
        return redisson.getFairLock(RedisKeys.lockKey(resource));
    }

    public RReadWriteLock getReadWriteLock(String resource) {
        return redisson.getReadWriteLock(RedisKeys.lockKey(resource));
    }

    public RLock readLock(String resource) {
        return getReadWriteLock(resource).readLock();
    }

    public RLock writeLock(String resource) {
        return getReadWriteLock(resource).writeLock();
    }

    public RLock getMultiLock(String... resources) {
        RLock[] locks = new RLock[resources.length];
        for (int i = 0; i < resources.length; i++) {
            locks[i] = redisson.getLock(RedisKeys.lockKey(resources[i]));
        }
        return redisson.getMultiLock(locks);
    }

    public org.redisson.api.RSemaphore getSemaphore(String name) {
        return redisson.getSemaphore(RedisKeys.lockPrefix() + name);
    }

    public org.redisson.api.RCountDownLatch getCountDownLatch(String name, int count) {
        org.redisson.api.RCountDownLatch latch = redisson.getCountDownLatch(name);
        latch.trySetCount(count);
        return latch;
    }

    public RLock[] batchLock(String resourcePrefix, int count) {
        RLock[] locks = new RLock[count];
        for (int i = 0; i < count; i++) {
            locks[i] = redisson.getLock(RedisKeys.lockKey(resourcePrefix + i));
        }
        return locks;
    }

    public void batchUnlock(RLock... locks) {
        for (RLock lock : locks) {
            lock.unlock();
        }
    }
}
