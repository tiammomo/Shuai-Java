package com.shuai.caffeine.demo.eviction.impl;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.lang.ref.WeakReference;
import java.util.WeakHashMap;

/**
 * 基于引用的淘汰演示
 *
 * 引用类型说明：
 * - weakKeys/weakValues: 被 GC 立即回收
 * - softValues: 内存不足时优先回收
 *
 * 使用场景：内存敏感、需要与 GC 协作的缓存
 */
public class ReferenceEviction {

    public void runDemo() {
        System.out.println("\n--- 基于引用的淘汰 (Reference Eviction) ---");

        demoWeakKeysAndValues();
        demoSoftValues();
        demoWeakReferenceValue();
    }

    /**
     * 演示 weakKeys 和 weakValues
     * 当 key 没有强引用时，会被 GC 立即回收
     */
    private void demoWeakKeysAndValues() {
        System.out.println("\n[1] weakKeys + weakValues 示例");
        System.out.println("  weakKeys/weakValues: 被 GC 立即回收");
        System.out.println("  适用场景: 需要与 GC 协作的缓存");

        // 使用 WeakHashMap 演示弱引用行为
        WeakHashMap<String, String> demoMap = new WeakHashMap<>();
        String weakKey = new String("demo-key");
        demoMap.put(weakKey, "value");
        System.out.println("  before GC: " + demoMap.containsKey(weakKey));

        weakKey = null;
        System.gc();
        System.out.println("  weakKey 会被 GC 回收");
    }

    /**
     * 演示 softValues
     * JVM 内存不足时才会回收，适用于内存敏感但需要保留较久的缓存
     */
    private void demoSoftValues() {
        System.out.println("\n[2] softValues 示例");

        Cache<String, byte[]> cache = Caffeine.newBuilder()
            .softValues()
            .maximumSize(100)
            .build();

        // 放入一些数据
        for (int i = 0; i < 5; i++) {
            cache.put("key-" + i, ("value-" + i).getBytes());
        }

        System.out.println("  softValues: " + cache.estimatedSize() + " items");
        System.out.println("  内存不足时优先被 GC 回收");
    }

    /**
     * 演示使用 WeakReference 包装 value
     * 适合缓存大对象，让 GC 在需要时回收
     */
    private void demoWeakReferenceValue() {
        System.out.println("\n[3] WeakReference 包装值示例");

        Cache<String, WeakReference<String>> cache = Caffeine.newBuilder()
            .maximumSize(100)
            .build();

        String key = "weak-key";
        String value = new String("CacheValue");

        // 用 WeakReference 包装值
        cache.put(key, new WeakReference<>(value));

        // value 不再被强引用后，GC 会回收
        value = null;
        System.gc();

        WeakReference<String> ref = cache.getIfPresent(key);
        String result = (ref != null) ? ref.get() : "collected by GC";
        System.out.println("  value after GC: " + result);
    }
}
