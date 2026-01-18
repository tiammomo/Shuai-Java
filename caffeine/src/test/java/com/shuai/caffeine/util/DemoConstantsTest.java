package com.shuai.caffeine.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * DemoConstants 单元测试
 */
class DemoConstantsTest {

    @Test
    void testKeyPrefixes() {
        assertEquals("key:", DemoConstants.KEY_PREFIX);
        assertEquals("user:", DemoConstants.USER_KEY_PREFIX);
    }

    @Test
    void testCacheSizes() {
        assertEquals(10, DemoConstants.SMALL_SIZE);
        assertEquals(100, DemoConstants.MEDIUM_SIZE);
        assertEquals(1000, DemoConstants.LARGE_SIZE);
        assertEquals(10000, DemoConstants.EXTRA_LARGE_SIZE);
    }

    @Test
    void testWeightConstants() {
        assertEquals(1024, DemoConstants.MAX_WEIGHT_BYTES);
        assertEquals(1024 * 1024, DemoConstants.MAX_WEIGHT_MB);
    }

    @Test
    void testDelayConstants() {
        assertEquals(100, DemoConstants.SHORT_DELAY_MS);
        assertEquals(500, DemoConstants.MEDIUM_DELAY_MS);
        assertEquals(1000, DemoConstants.LONG_DELAY_MS);
    }

    @Test
    void testExpireConstants() {
        assertEquals(1, DemoConstants.EXPIRE_SHORT_SECONDS);
        assertEquals(5, DemoConstants.EXPIRE_MEDIUM_MINUTES);
        assertEquals(1, DemoConstants.EXPIRE_LONG_HOURS);
    }
}
