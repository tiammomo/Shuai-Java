# Caffeine 学习文档

> 高性能本地缓存库系统学习指南

## 文档目录

| 章节 | 标题 | 内容概述 |
|------|------|----------|
| [01-overview](01-overview.md) | Caffeine 概述 | 特性介绍、与 Guava 对比、使用场景 |
| [02-cache-types](02-cache-types.md) | 四种缓存类型 | Cache、LoadingCache、AsyncCache、AsyncLoadingCache |
| [03-basic-usage](03-basic-usage.md) | 基础使用 | Cache 接口、get/put/invalidate 操作 |
| [04-loading-cache](04-loading-cache.md) | LoadingCache 自动加载 | CacheLoader、refreshAfterWrite、刷新 vs 过期 |
| [05-eviction](05-eviction.md) | 淘汰策略 | 大小淘汰、权重淘汰、时间淘汰、引用淘汰 |
| [06-stats](06-stats.md) | 统计与监控 | recordStats、命中率分析、Prometheus 集成 |
| [07-async-cache](07-async-cache.md) | 异步缓存 | AsyncCache、AsyncLoadingCache、Executor 配置 |
| [08-best-practices](08-best-practices.md) | 最佳实践 | 缓存策略、性能优化、常见问题 |
| [09-testing](09-testing.md) | 测试策略 | 单元测试、集成测试、测试覆盖 |
| [10-llm-applications](10-llm-applications.md) | 大模型应用 | LLM API 缓存、Embedding 缓存、RAG 检索缓存 |

## 快速开始

### 运行演示

```bash
cd /home/ubuntu/learn_projects/Shuai-Java/caffeine
mvn exec:java -Dexec.mainClass=com.shuai.caffeine.CaffeineDemo
```

### 运行测试

```bash
mvn test                              # 运行所有测试 (36 tests)
mvn test -Dtest=CacheConfigsTest     # 运行配置测试
mvn test -Dtest=BasicUsageDemoTest   # 运行基础使用测试
```

## 代码结构

```
src/main/java/com/shuai/caffeine/
├── CaffeineDemo.java              # 模块入口
├── model/UserData.java            # 数据模型
├── util/                          # 工具类
│   ├── SleepUtils.java            # 睡眠工具
│   ├── DataLoader.java            # 数据加载
│   └── DemoConstants.java         # 常量定义
├── config/                        # 配置类
│   ├── CacheConfigs.java          # 缓存配置模板
│   └── ExpiryConfigs.java         # 过期配置模板
└── demo/                          # 演示类
    ├── overview/CaffeineOverviewDemo.java
    ├── basic/BasicUsageDemo.java
    ├── loading/LoadingCacheDemo.java
    ├── eviction/EvictionDemo.java (含 impl 子包)
    ├── stats/StatsDemo.java (含 impl 子包)
    └── async/AsyncCacheDemo.java
```

## 配套资源

- **源码地址**: [src/main/java/com/shuai/caffeine/](../src/main/java/com/shuai/caffeine/)
- **测试代码**: [src/test/java/com/shuai/caffeine/](../src/test/java/com/shuai/caffeine/)
- **主文档**: [README.md](../README.md)

## 学习路径建议

1. **入门**: 先阅读 [01-overview](01-overview.md) 了解 Caffeine 特性
2. **基础**: 学习 [02-cache-types](02-cache-types.md) 和 [03-basic-usage](03-basic-usage.md)
3. **进阶**: 掌握 [04-loading-cache](04-loading-cache.md) 和 [05-eviction](05-eviction.md)
4. **高级**: 深入 [06-stats](06-stats.md) 和 [07-async-cache](07-async-cache.md)
5. **实践**: 参考 [08-best-practices](08-best-practices.md)、[09-testing](09-testing.md) 和 [10-llm-applications](10-llm-applications.md)

## 相关链接

- [Caffeine GitHub](https://github.com/ben-manes/caffeine)
- [Caffeine Wiki](https://github.com/ben-manes/caffeine/wiki)
- [项目首页](../README.md)
