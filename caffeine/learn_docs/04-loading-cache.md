# 04 - LoadingCache 自动加载

## 4.1 LoadingCache 介绍

LoadingCache 是 Caffeine 提供的自动加载缓存，当缓存不存在时自动调用 CacheLoader 加载数据。

```java
LoadingCache<String, UserData> cache = Caffeine.newBuilder()
    .maximumSize(100)
    .build(key -> loadFromDb(key));
```

详见 [LoadingCacheDemo.java:51-58](../src/main/java/com/shuai/caffeine/demo/loading/LoadingCacheDemo.java#L51-L58)

### 核心特点

| 特点 | 说明 |
|------|------|
| 自动加载 | get() 时自动触发 loader |
| 线程安全 | 并发场景下 loader 只执行一次 |
| 防止击穿 | 单个 key 的加载是原子的 |

---

## 4.2 CacheLoader 使用

### 基本用法

```java
LoadingCache<String, String> cache = Caffeine.newBuilder()
    .maximumSize(100)
    .build(key -> {
        // 从数据库加载
        return loadFromDb(key);
    });

// 自动加载
String value = cache.get("user:1");  // 自动调用 loader
```

详见 [LoadingCacheDemo.java:79-96](../src/main/java/com/shuai/caffeine/demo/loading/LoadingCacheDemo.java#L79-L96)

### DataLoader 工具类

项目中提供了 [DataLoader.java](../src/main/java/com/shuai/caffeine/util/DataLoader.java) 工具类：

```java
// 加载用户数据
UserData user = DataLoader.loadUserData("user:1");

// 加载字符串数据
String data = DataLoader.loadStringData("key");

// 带延迟的加载
UserData user = DataLoader.loadUserData("user:1", 100);  // 100ms 延迟
```

详见 [DataLoader.java:22-56](../src/main/java/com/shuai/caffeine/util/DataLoader.java#L22-L56)

---

## 4.3 刷新策略 (refreshAfterWrite)

refreshAfterWrite 用于在数据过期前刷新数据。

```java
LoadingCache<String, String> cache = Caffeine.newBuilder()
    .maximumSize(100)
    .expireAfterWrite(Duration.ofMinutes(5))
    .refreshAfterWrite(Duration.ofMinutes(1))  // 1分钟后可刷新
    .build(key -> loadFromDb(key));
```

详见 [LoadingCacheDemo.java:126-132](../src/main/java/com/shuai/caffeine/demo/loading/LoadingCacheDemo.java#L126-L132)

### 刷新行为

- 刷新是异步的，不会阻塞 get() 调用
- 刷新时返回旧数据，同时后台加载新数据
- 过期后访问才会真正加载新数据

---

## 4.4 刷新 vs 过期对比

| 特性 | refreshAfterWrite | expireAfterWrite |
|------|-------------------|------------------|
| 时机 | 可刷新时返回旧值 | 过期后返回 null |
| 行为 | 异步更新 | 同步等待 |
| 用户体验 | 无感知 | 可能感知延迟 |
| 适用场景 | 数据变化不频繁 | 数据变化频繁 |

详见 [LoadingCacheDemo.java:153-178](../src/main/java/com/shuai/caffeine/demo/loading/LoadingCacheDemo.java#L153-L178)

---

## 4.5 使用场景

| 场景 | 说明 | 配置示例 |
|------|------|----------|
| 数据库查询缓存 | 缓存查询结果 | expireAfterWrite + refreshAfterWrite |
| 配置缓存 | 缓存应用配置 | expireAfterWrite |
| API 响应缓存 | 缓存远程 API 结果 | refreshAfterWrite |
| 字典数据 | 缓存字典表数据 | expireAfterWrite |

详见 [LoadingCacheDemo.java:188-201](../src/main/java/com/shuai/caffeine/demo/loading/LoadingCacheDemo.java#L188-L201)

---

## 4.6 代码位置汇总

| 功能 | 文件位置 |
|------|----------|
| LoadingCache 创建 | [LoadingCacheDemo.java:51-58](../src/main/java/com/shuai/caffeine/demo/loading/LoadingCacheDemo.java#L51-L58) |
| CacheLoader 使用 | [LoadingCacheDemo.java:79-96](../src/main/java/com/shuai/caffeine/demo/loading/LoadingCacheDemo.java#L79-L96) |
| refreshAfterWrite | [LoadingCacheDemo.java:121-146](../src/main/java/com/shuai/caffeine/demo/loading/LoadingCacheDemo.java#L121-L146) |
| 刷新 vs 过期对比 | [LoadingCacheDemo.java:153-178](../src/main/java/com/shuai/caffeine/demo/loading/LoadingCacheDemo.java#L153-L178) |
| DataLoader 工具 | [DataLoader.java](../src/main/java/com/shuai/caffeine/util/DataLoader.java) |
| 单元测试 | [DataLoaderTest.java](../src/test/java/com/shuai/caffeine/util/DataLoaderTest.java) |

---

## 4.7 实践要点

### 最佳实践

1. **设置合理的最大容量** - 避免内存溢出
2. **配合过期策略使用** - 保证数据最终一致性
3. **考虑刷新频率** - 根据数据变化频率设置
4. **loader 中处理异常** - 避免影响缓存

### 注意事项

- refreshAfterWrite 不保证立即刷新
- loader 执行时间过长会阻塞
- 需要处理 loader 中的异常

---

## 4.8 课后练习

1. 运行 LoadingCacheDemo 查看刷新效果
2. 理解 refresh 和 expire 的区别
3. 为项目中的数据库查询设计缓存策略

**下一章**: [05 - 淘汰策略](05-eviction.md)
