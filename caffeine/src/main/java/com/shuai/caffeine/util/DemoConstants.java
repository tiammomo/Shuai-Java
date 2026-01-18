package com.shuai.caffeine.util;

/**
 * 演示常量类
 * 统一缓存键前缀、默认配置等
 */
public final class DemoConstants {

    // 缓存键前缀
    public static final String KEY_PREFIX = "key:";
    public static final String USER_KEY_PREFIX = "user:";
    public static final String BATCH_KEY_PREFIX = "batch";
    public static final String ASYNC_KEY_PREFIX = "async:key:";
    public static final String TASK_KEY_PREFIX = "task:";

    // 默认缓存大小
    public static final int SMALL_SIZE = 10;
    public static final int MEDIUM_SIZE = 100;
    public static final int LARGE_SIZE = 1000;
    public static final int EXTRA_LARGE_SIZE = 10000;

    // 默认权重
    public static final int MAX_WEIGHT_BYTES = 1024;          // 1KB
    public static final int MAX_WEIGHT_MB = 1024 * 1024;      // 1MB

    // 默认时间配置（毫秒）
    public static final long SHORT_DELAY_MS = 100;
    public static final long MEDIUM_DELAY_MS = 500;
    public static final long LONG_DELAY_MS = 1000;

    // 默认过期时间
    public static final long EXPIRE_SHORT_SECONDS = 1;
    public static final long EXPIRE_MEDIUM_MINUTES = 5;
    public static final long EXPIRE_LONG_HOURS = 1;

    private DemoConstants() {
        // 常量类，禁止实例化
    }
}
