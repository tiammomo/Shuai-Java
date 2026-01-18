package com.shuai.database.redis.script;

import java.util.List;

/**
 * Lua 脚本模板集合
 *
 * Redis Lua 脚本用于实现原子性的复杂操作，避免网络往返开销。
 *
 * @author Shuai
 */
public final class LuaScriptTemplates {

    private LuaScriptTemplates() {}

    // ==================== 分布式锁脚本 ====================

    public static final String UNLOCK_LOCK =
        "if redis.call('get', KEYS[1]) == ARGV[1] then " +
        "    return redis.call('del', KEYS[1]) " +
        "else " +
        "    return 0 " +
        "end";

    public static final String UNLOCK_LOCK_WITH_TTL =
        "if redis.call('get', KEYS[1]) == ARGV[1] then " +
        "    local ttl = redis.call('ttl', KEYS[1]) " +
        "    if ttl and ttl > 0 then " +
        "        return redis.call('del', KEYS[1]) " +
        "    end " +
        "end " +
        "return 0";

    // ==================== 库存扣减脚本 ====================

    public static final String DECR_STOCK =
        "local stock = redis.call('GET', KEYS[1]) " +
        "if stock and tonumber(stock) > 0 then " +
        "    local newStock = redis.call('DECR', KEYS[1]) " +
        "    return newStock " +
        "else " +
        "    return -1 " +
        "end";

    public static String decrStockBy(int quantity) {
        return
            "local stock = redis.call('GET', KEYS[1]) " +
            "if stock and tonumber(stock) >= " + quantity + " then " +
            "    local newStock = redis.call('DECRBY', KEYS[1], " + quantity + ") " +
            "    return newStock " +
            "else " +
            "    return -1 " +
            "end";
    }

    public static final String DECR_STOCK_WITH_SALES =
        "local stock = redis.call('GET', KEYS[1]) " +
        "if stock and tonumber(stock) >= tonumber(ARGV[1]) then " +
        "    redis.call('DECRBY', KEYS[1], ARGV[1]) " +
        "    redis.call('INCRBY', KEYS[2], ARGV[1]) " +
        "    return redis.call('GET', KEYS[1]) " +
        "else " +
        "    return -1 " +
        "end";

    // ==================== 限流脚本 ====================

    public static String slidingWindowRateLimit(int maxRequests, int windowSeconds) {
        String script =
            "local current = redis.call('INCR', KEYS[1]) " +
            "if current == 1 then " +
            "    redis.call('EXPIRE', KEYS[1], " + windowSeconds + ") " +
            "end " +
            "if current > " + maxRequests + " then " +
            "    return -1 " +
            "end " +
            "return " + maxRequests + " - current + 1";
        return script;
    }

    public static String tokenBucketRateLimit(int capacity, int refillRate) {
        String script =
            "local tokens = redis.call('GET', KEYS[1]) " +
            "if tokens then " +
            "    local now = tonumber(ARGV[1]) " +
            "    local last = tonumber(ARGV[2]) " +
            "    local rate = " + refillRate + " " +
            "    local delta = math.floor((now - last) * rate) " +
            "    tokens = math.min(" + capacity + ", tokens + delta) " +
            "else " +
            "    tokens = " + capacity + " " +
            "end " +
            "if tokens > 0 then " +
            "    redis.call('SET', KEYS[1], tokens - 1) " +
            "    redis.call('PEXPIRE', KEYS[1], 3600000) " +
            "    return tokens - 1 " +
            "else " +
            "    return -1 " +
            "end";
        return script;
    }

    // ==================== Hash 操作脚本 ====================

    public static final String HASH_MSET =
        "local count = 0 " +
        "local i = 1 " +
        "while i <= #ARGV do " +
        "    redis.call('HSET', KEYS[1], ARGV[i], ARGV[i + 1]) " +
        "    count = count + 1 " +
        "    i = i + 2 " +
        "end " +
        "return count";

    public static final String HASH_INCR =
        "local value = redis.call('HGET', KEYS[1], ARGV[1]) " +
        "if value then " +
        "    return redis.call('HINCRBY', KEYS[1], ARGV[1], ARGV[2]) " +
        "else " +
        "    redis.call('HSET', KEYS[1], ARGV[1], ARGV[2]) " +
        "    return tonumber(ARGV[2]) " +
        "end";

    // ==================== 脚本执行工具方法 ====================

    public static String loadScript(redis.clients.jedis.Jedis jedis, String script) {
        return jedis.scriptLoad(script);
    }

    public static String[] loadCommonScripts(redis.clients.jedis.Jedis jedis) {
        String[] scripts = {
            UNLOCK_LOCK,
            DECR_STOCK,
            DECR_STOCK_WITH_SALES
        };
        String[] shas = new String[scripts.length];
        for (int i = 0; i < scripts.length; i++) {
            shas[i] = jedis.scriptLoad(scripts[i]);
        }
        return shas;
    }
}
