# 09 - 测试策略

## 9.1 测试策略概述

Caffeine 缓存测试需要覆盖：
- 基本操作正确性
- 淘汰策略行为
- 并发安全性
- 配置正确性

---

## 9.2 单元测试示例

### Cache 基本操作测试

详见 [BasicUsageDemoTest.java](../src/test/java/com/shuai/caffeine/demo/basic/BasicUsageDemoTest.java)

```java
@Test
void testCachePutAndGet() {
    Cache<String, String> cache = Caffeine.newBuilder()
        .maximumSize(10)
        .build();

    cache.put("key", "value");
    assertEquals("value", cache.getIfPresent("key"));
}

@Test
void testCacheGetWithLoader() {
    Cache<String, String> cache = Caffeine.newBuilder()
        .build();

    String value = cache.get("key", k -> "loaded-" + k);
    assertEquals("loaded-key", value);
}
```

---

## 9.3 配置测试

### CacheConfigs 测试

详见 [CacheConfigsTest.java](../src/test/java/com/shuai/caffeine/config/CacheConfigsTest.java)

```java
@Test
void testSmallCache() {
    Cache<String, String> cache = CacheConfigs.smallCache();
    assertNotNull(cache);
    cache.put("key", "value");
    assertEquals("value", cache.getIfPresent("key"));
}

@Test
void testWeightedCache() {
    Cache<String, byte[]> cache = CacheConfigs.weightedCache();
    assertNotNull(cache);
    byte[] data = "test data".getBytes();
    cache.put("key", data);
    assertArrayEquals(data, cache.getIfPresent("key"));
}
```

### ExpiryConfigs 测试

详见 [ExpiryConfigsTest.java](../src/test/java/com/shuai/caffeine/config/ExpiryConfigsTest.java)

```java
@Test
void testExpireAfterWrite() {
    Caffeine<Object, Object> builder = ExpiryConfigs.expireAfterWrite(Duration.ofSeconds(5));
    assertNotNull(builder);
    Cache<String, String> cache = builder.build();
    cache.put("key", "value");
    assertEquals("value", cache.getIfPresent("key"));
}
```

---

## 9.4 工具类测试

### SleepUtils 测试

详见 [SleepUtilsTest.java](../src/test/java/com/shuai/caffeine/util/SleepUtilsTest.java)

```java
@Test
void testSleepMillis() {
    long start = System.currentTimeMillis();
    SleepUtils.sleep(100);
    long elapsed = System.currentTimeMillis() - start;
    assertTrue(elapsed >= 100);
}
```

### DataLoader 测试

详见 [DataLoaderTest.java](../src/test/java/com/shuai/caffeine/util/DataLoaderTest.java)

```java
@Test
void testLoadUserData() {
    UserData user = DataLoader.loadUserData("user:1");
    assertNotNull(user);
    assertEquals("user:1", user.getId());
}

@Test
void testLoadUserDataConsistency() {
    UserData user1 = DataLoader.loadUserData("user:1");
    UserData user2 = DataLoader.loadUserData("user:1");
    assertEquals(user1.getName(), user2.getName());
}
```

---

## 9.5 集成测试

### 主入口测试

详见 [CaffeineDemoTest.java](../src/test/java/com/shuai/caffeine/CaffeineDemoTest.java)

```java
@Test
void testMainMethodRunsWithoutException() {
    ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    PrintStream originalOut = System.out;

    System.setOut(new PrintStream(outContent));
    try {
        assertDoesNotThrow(() -> CaffeineDemo.main(new String[0]));
    } finally {
        System.setOut(originalOut);
    }

    assertTrue(outContent.toString().length() > 0);
    assertTrue(outContent.toString().contains("Caffeine"));
}
```

---

## 9.6 测试覆盖统计

| 包 | 测试类 | 测试数 |
|---|--------|--------|
| config | CacheConfigsTest, ExpiryConfigsTest | 14 |
| util | SleepUtilsTest, DataLoaderTest, DemoConstantsTest | 12 |
| demo/basic | BasicUsageDemoTest | 9 |
| 根目录 | CaffeineDemoTest | 1 |
| **总计** | | **36** |

---

## 9.7 测试运行

```bash
# 运行所有测试
mvn test

# 运行指定测试类
mvn test -Dtest=CacheConfigsTest
mvn test -Dtest=BasicUsageDemoTest

# 运行工具类测试
mvn test -Dtest=*UtilsTest

# 运行配置类测试
mvn test -Dtest=*ConfigsTest
```

---

## 9.8 代码位置汇总

| 测试内容 | 文件位置 |
|----------|----------|
| 基础使用测试 | [BasicUsageDemoTest.java](../src/test/java/com/shuai/caffeine/demo/basic/BasicUsageDemoTest.java) |
| 配置测试 | [CacheConfigsTest.java](../src/test/java/com/shuai/caffeine/config/CacheConfigsTest.java) |
| 过期配置测试 | [ExpiryConfigsTest.java](../src/test/java/com/shuai/caffeine/config/ExpiryConfigsTest.java) |
| 工具测试 | [SleepUtilsTest.java](../src/test/java/com/shuai/caffeine/util/SleepUtilsTest.java) |
| 数据加载测试 | [DataLoaderTest.java](../src/test/java/com/shuai/caffeine/util/DataLoaderTest.java) |
| 常量测试 | [DemoConstantsTest.java](../src/test/java/com/shuai/caffeine/util/DemoConstantsTest.java) |
| 入口测试 | [CaffeineDemoTest.java](../src/test/java/com/shuai/caffeine/CaffeineDemoTest.java) |

---

## 9.9 实践要点

### 测试建议

1. **覆盖基本操作** - put/get/invalidate
2. **测试配置模板** - 验证配置正确性
3. **测试边界条件** - 容量限制、过期时间
4. **测试并发安全** - 多线程场景
5. **集成测试** - 端到端测试

### 注意事项

- 避免测试依赖外部资源
- 异步操作测试需要等待完成
- 过期测试需要考虑时间容差

---

## 9.10 课后练习

1. 为项目的缓存功能编写单元测试
2. 测试各种淘汰策略的行为
3. 验证配置模板的正确性

---

## 结语

恭喜完成 Caffeine 学习！

### 建议的后续步骤

1. **实践应用**: 在项目中实际使用 Caffeine
2. **深入源码**: 阅读 Caffeine 源码理解内部实现
3. **性能测试**: 进行压力测试验证性能
4. **监控运维**: 建立缓存监控体系

### 相关资源

- [学习文档目录](../README.md)
- [源码目录](../src/main/java/com/shuai/caffeine/)
- [测试目录](../src/test/java/com/shuai/caffeine/)
- [Caffeine GitHub](https://github.com/ben-manes/caffeine)
