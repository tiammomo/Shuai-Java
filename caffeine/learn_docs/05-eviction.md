# 05 - 淘汰策略

Caffeine 提供多种淘汰策略：基于大小、权重、时间和引用。

## 5.1 基于大小的淘汰 (maximumSize)

当缓存条目达到上限时，淘汰最近最少使用的条目。

```java
Cache<String, String> cache = Caffeine.newBuilder()
    .maximumSize(100)
    .build();
```

详见 [SizeBasedEviction.java](../src/main/java/com/shuai/caffeine/demo/eviction/impl/SizeBasedEviction.java)

### 淘汰行为

- 采用 Window TinyLFU 算法
- 适应热点数据变化
- 保留热点数据，淘汰冷门数据

---

## 5.2 基于权重的淘汰 (maximumWeight)

根据自定义权重淘汰数据，适合条目大小不均匀的场景。

```java
Cache<String, byte[]> cache = Caffeine.newBuilder()
    .maximumWeight(1024 * 1024)  // 1MB
    .weigher((key, value) -> ((byte[]) value).length)
    .build();
```

详见 [WeightBasedEviction.java](../src/main/java/com/shuai/caffeine/demo/eviction/impl/WeightBasedEviction.java)

### Weigher 接口

```java
// 自定义权重计算
weigher((key, value) -> value.length())  // 按长度计算
weigher((key, value) -> value.size())    // 按集合大小计算
```

---

## 5.3 基于时间的淘汰

### expireAfterWrite

写入后经过指定时间过期

```java
Cache<String, String> cache = Caffeine.newBuilder()
    .expireAfterWrite(Duration.ofSeconds(10))
    .build();
```

详见 [TimeBasedEviction.java](../src/main/java/com/shuai/caffeine/demo/eviction/impl/TimeBasedEviction.java)

### expireAfterAccess

最后访问后经过指定时间过期

```java
Cache<String, String> cache = Caffeine.newBuilder()
    .expireAfterAccess(Duration.ofMinutes(5))
    .build();
```

详见 [ExpiryConfigs.java](../src/main/java/com/shuai/caffeine/config/ExpiryConfigs.java)

### expireAfter (自定义)

```java
Cache<String, String> cache = Caffeine.newBuilder()
    .expireAfter(new Expiry<>() {
        @Override
        public long expireAfterCreate(String key, String value, long currentTime) {
            return Duration.ofMinutes(5).toNanos();
        }
        // ...
    })
    .build();
```

详见 [ExpiryConfigs.java](../src/main/java/com/shuai/caffeine/config/ExpiryConfigs.java)

---

## 5.4 基于引用的淘汰

### weakKeys/weakValues

使用弱引用，允许 GC 回收

```java
Cache<Object, String> cache = Caffeine.newBuilder()
    .weakKeys()
    .weakValues()
    .build();
```

详见 [ReferenceEviction.java](../src/main/java/com/shuai/caffeine/demo/eviction/impl/ReferenceEviction.java)

### softValues

使用软引用，内存不足时回收

```java
Cache<String, Object> cache = Caffeine.newBuilder()
    .softValues()
    .build();
```

详见 [ReferenceEviction.java](../src/main/java/com/shuai/caffeine/demo/eviction/impl/ReferenceEviction.java)

### 引用类型对比

| 类型 | 回收时机 | 适用场景 |
|------|----------|----------|
| weakKeys | GC 时 | key 可能被 GC |
| weakValues | GC 时 | value 可能被 GC |
| softValues | 内存不足时 | 内存敏感缓存 |

---

## 5.5 淘汰监听器 (RemovalListener)

监听缓存条目被淘汰的事件。

```java
Cache<String, String> cache = Caffeine.newBuilder()
    .maximumSize(10)
    .removalListener((key, value, cause) -> {
        System.out.println("淘汰: " + key + ", 原因: " + cause);
    })
    .build();
```

详见 [RemovalListenerDemo.java](../src/main/java/com/shuai/caffeine/demo/eviction/impl/RemovalListenerDemo.java)

### RemovalCause 类型

| 类型 | 说明 |
|------|------|
| EXPLICIT | 手动调用 invalidate |
| SIZE | 容量超过 maximumSize |
| TIME | 过期时间到达 |
| COLLECTED | 被 GC 回收 |
| EXPIRED | 过期被清理 |

---

## 5.6 代码位置汇总

| 功能 | 文件位置 |
|------|----------|
| 大小淘汰 | [SizeBasedEviction.java](../src/main/java/com/shuai/caffeine/demo/eviction/impl/SizeBasedEviction.java) |
| 权重淘汰 | [WeightBasedEviction.java](../src/main/java/com/shuai/caffeine/demo/eviction/impl/WeightBasedEviction.java) |
| 时间淘汰 | [TimeBasedEviction.java](../src/main/java/com/shuai/caffeine/demo/eviction/impl/TimeBasedEviction.java) |
| 引用淘汰 | [ReferenceEviction.java](../src/main/java/com/shuai/caffeine/demo/eviction/impl/ReferenceEviction.java) |
| 淘汰监听 | [RemovalListenerDemo.java](../src/main/java/com/shuai/caffeine/demo/eviction/impl/RemovalListenerDemo.java) |
| 过期配置模板 | [ExpiryConfigs.java](../src/main/java/com/shuai/caffeine/config/ExpiryConfigs.java) |

---

## 5.7 实践要点

### 配置建议

| 场景 | 推荐配置 |
|------|----------|
| 热点数据 | maximumSize |
| 内存敏感 | softValues |
| 临时数据 | expireAfterWrite |
| 会话缓存 | expireAfterAccess |
| 淘汰日志 | RemovalListener |

### 注意事项

- weak/soft 引用可能影响性能
- 过期检测有延迟（异步清理）
- 大量淘汰可能影响响应时间

---

## 5.8 课后练习

1. 理解各种淘汰策略的适用场景
2. 为项目中的缓存设计淘汰策略
3. 测试不同配置下的淘汰行为

**下一章**: [06 - 统计与监控](06-stats.md)
