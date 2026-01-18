# 08 - 最佳实践

## 8.1 缓存策略设计

### 缓存数据分类

| 数据类型 | 过期策略 | 淘汰策略 | 示例 |
|----------|----------|----------|------|
| 配置数据 | 长期 | maximumSize | 应用配置 |
| 会话数据 | 短期 | expireAfterAccess | 用户会话 |
| 查询结果 | 中期 | expireAfterWrite | 数据库查询 |
| 计数器 | 实时 | expireAfterWrite | 访问计数 |

---

## 8.2 配置模板使用

项目提供了预定义的配置模板，详见 [CacheConfigs.java](../src/main/java/com/shuai/caffeine/config/CacheConfigs.java)：

```java
// 小型缓存
Cache<String, String> smallCache = CacheConfigs.smallCache();

// 中型缓存
Cache<String, String> mediumCache = CacheConfigs.mediumCache();

// 大型缓存（带统计）
Cache<String, String> largeCache = CacheConfigs.largeCache();

// 权重缓存
Cache<String, byte[]> weightedCache = CacheConfigs.weightedCache();
```

详见 [CacheConfigs.java:24-119](../src/main/java/com/shuai/caffeine/config/CacheConfigs.java#L24-L119)

### 过期配置模板

详见 [ExpiryConfigs.java](../src/main/java/com/shuai/caffeine/config/ExpiryConfigs.java)：

```java
// 1秒过期
Caffeine<Object, Object> builder = ExpiryConfigs.expireAfterWrite1Second();

// 5分钟过期
Caffeine<Object, Object> builder = ExpiryConfigs.expireAfterWrite5Minutes();

// 刷新+过期组合
Caffeine<Object, Object> builder = ExpiryConfigs.refreshAndExpire(
    Duration.ofMinutes(1),  // 刷新
    Duration.ofMinutes(5)   // 过期
);
```

详见 [ExpiryConfigs.java:22-82](../src/main/java/com/shuai/caffeine/config/ExpiryConfigs.java#L22-L82)

---

## 8.3 性能优化

### 1. 合理设置容量

```java
// 根据预估数据量设置
.expectedSize(10000)  // 预期大小，优化初始分配
.maximumSize(15000)  // 最大容量
```

### 2. 启用统计监控

```java
.recordStats()  // 生产环境建议启用
```

### 3. 使用弱引用（内存敏感场景）

```java
.weakValues()   // 允许 GC 回收
.softValues()   // 内存不足时回收
```

### 4. 异步处理

```java
AsyncCache<String, String> cache = Caffeine.newBuilder()
    .buildAsync();  // IO 操作使用异步
```

---

## 8.4 常见问题

### Q1: 缓存穿透

**问题**: 查询不存在的数据，每次都穿透到数据库

**解决方案**:
```java
// 使用 get(key, loader) 自动处理
cache.get(key, k -> loadFromDb(k));

// 空值缓存（注意过期时间）
if (dbResult == null) {
    cache.put(key, NULL_VALUE);  // 使用特殊标记
}
```

### Q2: 缓存击穿

**问题**: 热点 key 过期，大量请求同时穿透

**解决方案**:
```java
// LoadingCache 的 get 是原子的
LoadingCache<String, Data> cache = Caffeine.newBuilder()
    .build(key -> loadFromDb(key));  // 原子操作
```

### Q3: 缓存雪崩

**问题**: 大量 key 同时过期

**解决方案**:
```java
// 添加随机过期时间
long randomExtra = ThreadLocalRandom.current().nextLong(60_000);
expireAfterWrite(Duration.ofMinutes(5).plus(randomExtra));
```

---

## 8.5 Spring Boot 集成

```java
@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(Caffeine.newBuilder()
            .maximumSize(100)
            .expireAfterWrite(Duration.ofMinutes(5))
            .recordStats());
        return cacheManager;
    }
}
```

### @Cacheable 使用

```java
@Cacheable(value = "users", key = "#userId")
public User getUser(Long userId) {
    return userRepository.findById(userId);
}
```

---

## 8.6 代码位置汇总

| 功能 | 文件位置 |
|------|----------|
| 缓存配置模板 | [CacheConfigs.java](../src/main/java/com/shuai/caffeine/config/CacheConfigs.java) |
| 过期配置模板 | [ExpiryConfigs.java](../src/main/java/com/shuai/caffeine/config/ExpiryConfigs.java) |
| 配置测试 | [CacheConfigsTest.java](../src/test/java/com/shuai/caffeine/config/CacheConfigsTest.java) |
| 过期测试 | [ExpiryConfigsTest.java](../src/test/java/com/shuai/caffeine/config/ExpiryConfigsTest.java) |

---

## 8.7 实践要点

### 最佳实践清单

- [ ] 根据数据类型选择合适的淘汰策略
- [ ] 生产环境启用 recordStats() 监控
- [ ] 设置合理的过期时间
- [ ] 使用配置模板统一管理
- [ ] 添加缓存操作日志
- [ ] 定期分析命中率优化配置

### 性能陷阱

1. **过大的 maximumSize** - 占用过多内存
2. **过长的过期时间** - 数据不一致
3. **不使用统计** - 无法发现问题
4. **同步处理 IO** - 影响吞吐量

---

## 8.8 课后练习

1. 为项目中的缓存设计统一的配置模板
2. 分析项目中的缓存命中率
3. 实现缓存监控和告警

**下一章**: [09 - 测试策略](09-testing.md)
