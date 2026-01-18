package com.shuai.caffeine.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SleepUtils 单元测试
 */
class SleepUtilsTest {

    @Test
    void testSleepMilliseconds() {
        long start = System.currentTimeMillis();
        SleepUtils.sleep(100);
        long elapsed = System.currentTimeMillis() - start;
        assertTrue(elapsed >= 90, "Sleep should take at least 90ms");
    }

    @Test
    void testSleepSeconds() {
        long start = System.currentTimeMillis();
        SleepUtils.sleepSeconds(1);
        long elapsed = System.currentTimeMillis() - start;
        assertTrue(elapsed >= 900, "Sleep should take at least 900ms");
    }

    @Test
    void testSleepZero() {
        assertDoesNotThrow(() -> SleepUtils.sleep(0));
    }
}
