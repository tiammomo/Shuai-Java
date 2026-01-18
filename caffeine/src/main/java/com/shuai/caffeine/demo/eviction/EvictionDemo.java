package com.shuai.caffeine.demo.eviction;

import com.shuai.caffeine.demo.eviction.impl.ReferenceEviction;
import com.shuai.caffeine.demo.eviction.impl.RemovalListenerDemo;
import com.shuai.caffeine.demo.eviction.impl.SizeBasedEviction;
import com.shuai.caffeine.demo.eviction.impl.TimeBasedEviction;
import com.shuai.caffeine.demo.eviction.impl.WeightBasedEviction;

/**
 * Caffeine 淘汰策略演示类
 *
 * 核心内容
 * ----------
 *   - 基于大小的淘汰: maximumSize, maximumWeight
 *   - 基于时间的淘汰: expireAfterAccess, expireAfterWrite
 *   - 基于引用的淘汰: weakKeys, weakValues, softValues
 *   - 淘汰监听器: removalListener
 *
 * @author Shuai
 * @version 1.0
 */
public class EvictionDemo {

    public void runAllDemos() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("       Caffeine 淘汰策略");
        System.out.println("=".repeat(50));

        new SizeBasedEviction().runDemo();
        new WeightBasedEviction().runDemo();
        new TimeBasedEviction().runDemo();
        new ReferenceEviction().runDemo();
        new RemovalListenerDemo().runDemo();
    }
}
