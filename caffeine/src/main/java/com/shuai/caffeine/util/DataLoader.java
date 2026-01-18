package com.shuai.caffeine.util;

import com.shuai.caffeine.model.UserData;

/**
 * 数据加载工具类
 * 提供模拟数据库加载等方法
 */
public final class DataLoader {

    private static final String[] NAMES = {"Alice", "Bob", "Charlie", "David", "Eve"};

    private DataLoader() {
        // 工具类，禁止实例化
    }

    /**
     * 加载用户数据
     * @param key 用户ID
     * @return UserData
     */
    public static UserData loadUserData(String key) {
        String[] names = {"Alice", "Bob", "Charlie"};
        int hash = Math.abs(key.hashCode() % names.length);
        return new UserData(key, names[hash], 20 + hash);
    }

    /**
     * 加载用户数据（带延迟模拟）
     * @param key 用户ID
     * @param delayMs 延迟毫秒数
     * @return UserData
     */
    public static UserData loadUserData(String key, long delayMs) {
        SleepUtils.sleep(delayMs);
        return loadUserData(key);
    }

    /**
     * 模拟从数据库加载数据
     * @param key 缓存键
     * @return 加载的数据
     */
    public static String loadStringData(String key) {
        return "Loaded-" + key + "-" + System.currentTimeMillis();
    }

    /**
     * 模拟从数据库加载数据（带延迟）
     * @param key 缓存键
     * @param delayMs 延迟毫秒数
     * @return 加载的数据
     */
    public static String loadStringData(String key, long delayMs) {
        SleepUtils.sleep(delayMs);
        return loadStringData(key);
    }

    /**
     * 根据索引获取名称
     * @param index 索引
     * @return 名称
     */
    public static String getNameByIndex(int index) {
        return NAMES[index % NAMES.length];
    }
}
