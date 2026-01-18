package com.shuai.database.redis.util;

/**
 * Redis Key 命名规范工具类
 *
 * 命名约定：使用 ":" 作为命名空间分隔符，采用 "业务:类型:标识" 格式。
 *
 * @author Shuai
 */
public final class RedisKeys {

    private RedisKeys() {}

    // ==================== 用户相关 ====================

    public static String userKey(Long userId) {
        return "user:" + userId;
    }

    public static String sessionKey(String sessionId) {
        return "session:" + sessionId;
    }

    public static String userTagsKey(Long userId) {
        return "user:tags:" + userId;
    }

    // ==================== 订单相关 ====================

    public static String orderKey(Long orderId) {
        return "order:" + orderId;
    }

    public static String orderNoKey() {
        return "order:no";
    }

    // ==================== 商品相关 ====================

    public static String stockKey(Long productId) {
        return "stock:" + productId;
    }

    public static String productKey(Long productId) {
        return "product:" + productId;
    }

    public static String productLockKey(Long productId) {
        return "product:lock:" + productId;
    }

    // ==================== 排行榜相关 ====================

    public static String leaderboardKey(String name) {
        return "leaderboard:" + name;
    }

    // ==================== 消息队列相关 ====================

    public static String queueKey(String name) {
        return "queue:" + name;
    }

    public static String streamKey(String name) {
        return "stream:" + name;
    }

    public static String consumerGroup(String name, String groupName) {
        return groupName;
    }

    // ==================== 分布式锁相关 ====================

    public static String lockKey(String resource) {
        return "lock:" + resource;
    }

    public static String lockPrefix() {
        return "lock:";
    }

    // ==================== 缓存相关 ====================

    public static String cacheKey(String key) {
        return "cache:" + key;
    }

    public static String idGeneratorKey(String name) {
        return "id:" + name;
    }

    // ==================== 工具方法 ====================

    public static String keyWithPrefix(String prefix, String identifier) {
        return prefix + identifier;
    }

    public static String keyWithPrefix(String prefix, String separator, String identifier) {
        return prefix + separator + identifier;
    }

    public static int deleteByPattern(redis.clients.jedis.Jedis jedis, String pattern) {
        java.util.Set<String> keys = jedis.keys(pattern);
        if (!keys.isEmpty()) {
            Long deleted = jedis.del(keys.toArray(new String[0]));
            return deleted != null ? deleted.intValue() : 0;
        }
        return 0;
    }
}
