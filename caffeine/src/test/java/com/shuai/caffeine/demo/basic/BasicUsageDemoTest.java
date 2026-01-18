package com.shuai.caffeine.demo.basic;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;

/**
 * BasicUsageDemo 单元测试
 * 测试 Cache 基本操作
 */
class BasicUsageDemoTest {

    private Cache<String, String> cache;

    @BeforeEach
    void setUp() {
        cache = Caffeine.newBuilder()
                .maximumSize(100)
                .build();
    }

    @Test
    void testCachePutAndGet() {
        cache.put("key1", "value1");
        assertEquals("value1", cache.getIfPresent("key1"));
    }

    @Test
    void testCacheGetIfPresentNull() {
        assertNull(cache.getIfPresent("nonexistent"));
    }

    @Test
    void testCacheGetWithLoader() {
        String value = cache.get("key", k -> "loaded-" + k);
        assertEquals("loaded-key", value);
        // Second call should use cached value
        String value2 = cache.get("key", k -> "different-" + k);
        assertEquals("loaded-key", value2);
    }

    @Test
    void testCacheInvalidate() {
        cache.put("key", "value");
        assertEquals("value", cache.getIfPresent("key"));
        cache.invalidate("key");
        assertNull(cache.getIfPresent("key"));
    }

    @Test
    void testCacheInvalidateAll() {
        cache.put("a", "1");
        cache.put("b", "2");
        cache.put("c", "3");
        assertEquals(3, cache.estimatedSize());
        cache.invalidateAll(Arrays.asList("a", "b"));
        assertEquals(1, cache.estimatedSize());
    }

    @Test
    void testCacheInvalidateAllClear() {
        cache.put("a", "1");
        cache.put("b", "2");
        cache.put("c", "3");
        cache.invalidateAll();
        assertEquals(0, cache.estimatedSize());
    }

    @Test
    void testCachePutAll() {
        Map<String, String> map = new ConcurrentHashMap<>();
        map.put("k1", "v1");
        map.put("k2", "v2");
        map.put("k3", "v3");
        cache.putAll(map);
        assertEquals(3, cache.estimatedSize());
        assertEquals("v1", cache.getIfPresent("k1"));
        assertEquals("v2", cache.getIfPresent("k2"));
        assertEquals("v3", cache.getIfPresent("k3"));
    }

    @Test
    void testCacheGetAllPresent() {
        cache.put("a", "1");
        cache.put("b", "2");
        cache.put("c", "3");
        Map<String, String> result = cache.getAllPresent(Arrays.asList("a", "b", "missing"));
        assertEquals(2, result.size());
        assertEquals("1", result.get("a"));
        assertEquals("2", result.get("b"));
        assertFalse(result.containsKey("missing"));
    }

    @Test
    void testCacheEstimatedSize() {
        assertEquals(0, cache.estimatedSize());
        for (int i = 0; i < 50; i++) {
            cache.put("key" + i, "value" + i);
        }
        assertEquals(50, cache.estimatedSize());
    }
}
