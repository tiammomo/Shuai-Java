# 03 - 基础使用

## 3.1 创建 Cache

```java
Cache<String, String> cache = Caffeine.newBuilder()
    .maximumSize(100)
    .expireAfterWrite(Duration.ofMinutes(5))
    .build();
```

详见 [BasicUsageDemo.java:52-56](../src/main/java/com/shuai/caffeine/demo/basic/BasicUsageDemo.java#L52-L56)

### 常用配置项

| 配置项 | 说明 | 示例 |
|--------|------|------|
| maximumSize | 最大容量 | `.maximumSize(100)` |
| expireAfterWrite | 写入后过期 | `.expireAfterWrite(Duration.ofMinutes(5))` |
| expireAfterAccess | 访问后过期 | `.expireAfterAccess(Duration.ofMinutes(10))` |
| recordStats | 启用统计 | `.recordStats()` |

---

## 3.2 查询操作

### getIfPresent(key)

查询 key，不存在返回 null（不会触发加载）

```java
String value = cache.getIfPresent("key");
if (value == null) {
    // key 不存在
}
```

详见 [BasicUsageDemo.java:87-92](../src/main/java/com/shuai/caffeine/demo/basic/BasicUsageDemo.java#L87-L92)

### get(key, loader)

原子操作，不存在时调用 loader 加载

```java
String value = cache.get("key", k -> loadFromDb(k));
```

详见 [BasicUsageDemo.java:94-99](../src/main/java/com/shuai/caffeine/demo/basic/BasicUsageDemo.java#L94-L99)

**特点**:
- 原子操作，避免缓存击穿
- loader 只会在缓存未命中时执行一次
- 线程安全

---

## 3.3 写入操作

### put(key, value)

直接写入缓存

```java
cache.put("key", "value");
```

详见 [BasicUsageDemo.java:118-121](../src/main/java/com/shuai/caffeine/demo/basic/BasicUsageDemo.java#L118-L121)

### putAll(map)

批量写入

```java
Map<String, String> map = new HashMap<>();
map.put("key1", "value1");
map.put("key2", "value2");
cache.putAll(map);
```

详见 [BasicUsageDemo.java:126-134](../src/main/java/com/shuai/caffeine/demo/basic/BasicUsageDemo.java#L126-L134)

---

## 3.4 删除操作

### invalidate(key)

删除单个条目

```java
cache.invalidate("key");
```

详见 [BasicUsageDemo.java:155-159](../src/main/java/com/shuai/caffeine/demo/basic/BasicUsageDemo.java#L155-L159)

### invalidateAll(keys)

批量删除

```java
cache.invalidateAll(Arrays.asList("key1", "key2"));
```

详见 [BasicUsageDemo.java:161-165](../src/main/java/com/shuai/caffeine/demo/basic/BasicUsageDemo.java#L161-L165)

### invalidateAll()

清空所有缓存

```java
cache.invalidateAll();
```

详见 [BasicUsageDemo.java:167-172](../src/main/java/com/shuai/caffeine/demo/basic/BasicUsageDemo.java#L167-L172)

---

## 3.5 批量操作

### getAllPresent(keys)

批量查询

```java
Map<String, String> result = cache.getAllPresent(Arrays.asList("key1", "key2"));
```

详见 [BasicUsageDemo.java:145-151](../src/main/java/com/shuai/caffeine/demo/basic/BasicUsageDemo.java#L145-L151)

---

## 3.6 代码位置汇总

| 功能 | 文件位置 |
|------|----------|
| Cache 创建 | [BasicUsageDemo.java:52-56](../src/main/java/com/shuai/caffeine/demo/basic/BasicUsageDemo.java#L52-L56) |
| 查询操作 | [BasicUsageDemo.java:87-99](../src/main/java/com/shuai/caffeine/demo/basic/BasicUsageDemo.java#L87-L99) |
| 写入操作 | [BasicUsageDemo.java:118-134](../src/main/java/com/shuai/caffeine/demo/basic/BasicUsageDemo.java#L118-L134) |
| 删除操作 | [BasicUsageDemo.java:155-172](../src/main/java/com/shuai/caffeine/demo/basic/BasicUsageDemo.java#L155-L172) |
| 单元测试 | [BasicUsageDemoTest.java](../src/test/java/com/shuai/caffeine/demo/basic/BasicUsageDemoTest.java) |

---

## 3.7 实践要点

### 最佳实践

1. **优先使用 getIfPresent()** - 不会触发 loader
2. **使用 get(key, loader)** - 避免缓存击穿
3. **及时清理无用数据** - 避免内存占用过高
4. **合理设置过期时间** - 根据业务需求调整

### 注意事项

- getIfPresent 不会触发 CacheLoader
- get 的 loader 只会调用一次（并发安全）
- invalidate 是同步操作

---

## 3.8 课后练习

1. 运行 BasicUsageDemo 查看实际输出
2. 编写测试验证 get() 的原子性
3. 对比 getIfPresent 和 get 的行为差异

**下一章**: [04 - LoadingCache 自动加载](04-loading-cache.md)
