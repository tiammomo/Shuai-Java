package com.shuai.caffeine.util;

/**
 * 睡眠工具类
 * 统一 Thread.sleep 调用
 */
public final class SleepUtils {

    private SleepUtils() {
        // 工具类，禁止实例化
    }

    /**
     * 睡眠指定毫秒
     * @param millis 毫秒数
     */
    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 睡眠指定秒
     * @param seconds 秒数
     */
    public static void sleepSeconds(long seconds) {
        sleep(seconds * 1000);
    }
}
