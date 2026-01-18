package com.shuai.caffeine.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.LoadingCache;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * CacheConfigs 单元测试
 */
class CacheConfigsTest {

    @Test
    void testSmallCache() {
        Cache<String, String> cache = CacheConfigs.smallCache();
        assertNotNull(cache);
        cache.put("key", "value");
        assertEquals("value", cache.getIfPresent("key"));
    }

    @Test
    void testMediumCache() {
        Cache<String, String> cache = CacheConfigs.mediumCache();
        assertNotNull(cache);
        cache.put("key", "value");
        assertEquals("value", cache.getIfPresent("key"));
    }

    @Test
    void testLargeCache() {
        Cache<String, String> cache = CacheConfigs.largeCache();
        assertNotNull(cache);
        cache.put("key", "value");
        assertEquals("value", cache.getIfPresent("key"));
    }

    @Test
    void testUnboundedCache() {
        Cache<String, String> cache = CacheConfigs.unboundedCache();
        assertNotNull(cache);
        cache.put("key", "value");
        assertEquals("value", cache.getIfPresent("key"));
    }

    @Test
    void testSmallLoadingCache() {
        LoadingCache<String, String> cache = CacheConfigs.smallLoadingCache();
        assertNotNull(cache);
        assertEquals("Loaded-testKey", cache.get("testKey"));
    }

    @Test
    void testMediumLoadingCacheWithRefresh() {
        LoadingCache<String, String> cache = CacheConfigs.mediumLoadingCacheWithRefresh();
        assertNotNull(cache);
        assertEquals("Loaded-testKey", cache.get("testKey"));
    }

    @Test
    void testWeightedCache() {
        Cache<String, byte[]> cache = CacheConfigs.weightedCache();
        assertNotNull(cache);
        byte[] data = "test data".getBytes();
        cache.put("key", data);
        assertArrayEquals(data, cache.getIfPresent("key"));
    }

    @Test
    void testCacheSizesAreDifferent() {
        Cache<String, String> small = CacheConfigs.smallCache();
        Cache<String, String> medium = CacheConfigs.mediumCache();
        Cache<String, String> large = CacheConfigs.largeCache();
        assertNotSame(small, medium);
        assertNotSame(medium, large);
        assertNotSame(small, large);
    }
}
