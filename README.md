# Java 学习项目

> 基于 JDK 21 的 Java 学习项目，从入门到进阶的完整学习路径。

## 核心模块

| 模块 | 路径 | 描述 | 学习文档 |
|-----|------|------|----------|
| **basics** | [basics/](basics/) | Java 基础语法模块，基于 JDK 21 的系统性学习 | [学习文档](basics/learn_docs/README.md) |
| **mq** | [mq/](mq/) | 消息队列模块，涵盖 Kafka、RocketMQ、RabbitMQ | [学习文档](mq/src/main/java/com/shuai/MqOverviewDemo.java) |
| **elasticsearch** | [elasticsearch/](elasticsearch/) | 搜索引擎模块，涵盖全文检索、向量检索、RAG、LLM 集成 | [学习文档](elasticsearch/learn_docs/README.md) |

## 扩展模块

| 模块 | 路径 | 描述 | 学习文档 |
|-----|------|------|----------|
| **orm** | [orm/](orm/) | ORM 框架模块，整合 Spring + MyBatis | - |
| **microservice** | [microservice/](microservice/) | 微服务模块，整合 Nacos + Dubbo | - |
| **database** | [database/](database/) | 数据库模块，整合 MySQL、PostgreSQL、MongoDB、Redis | [学习文档](database/learn_docs/README.md) |
| **security** | [security/](security/) | 安全模块，整合 Spring Security + OAuth2 | - |
| **protocol** | [protocol/](protocol/) | 通信协议模块，整合 WebSocket + Scheduler | - |
| **observability** | [observability/](observability/) | 可观测性模块，整合 Prometheus + Sentinel | - |
| **caffeine** | [caffeine/](caffeine/) | 高性能本地缓存，Java 界最优缓存实现 | [学习文档](caffeine/learn_docs/README.md) |
| **api-docs** | [api-docs/](api-docs/) | API 文档模块，整合 Swagger、Knife4j | - |

## 快速开始

### 1. 克隆项目

```bash
git clone <repository-url>
cd Shuai-Java
```

### 2. 环境配置

请参考 [环境配置指南](learn_docs/环境配置指南.md) 完成开发环境搭建。

### 3. 运行示例

```bash
# 运行 basics 模块
mvn -pl basics exec:java -Dexec.mainClass=com.shuai.BasicsDemo

# 运行 mq 模块
mvn -pl mq exec:java -Dexec.mainClass=com.shuai.MqDemo
```

## 环境要求

| 软件 | 版本要求 | 说明 |
|------|---------|------|
| JDK | 21 LTS | 必须使用 JDK 21 或更高版本 |
| Maven | 3.8+ | 项目构建工具 |

## 学习文档

项目配套学习文档位于 [learn_docs](learn_docs/) 目录：

### 通用文档

| 文档 | 描述 |
|-----|------|
| [学习路线图](learn_docs/学习路线图.md) | 完整的学习路径规划 |
| [环境配置指南](learn_docs/环境配置指南.md) | JDK 21 和 Maven 环境配置 |
| [项目结构说明](learn_docs/项目结构说明.md) | 项目目录结构和规范 |
| [代码规范](learn_docs/代码规范.md) | Java 编码规范和最佳实践 |
| [JDK21新特性](learn_docs/JDK21新特性.md) | JDK 21 新特性详解 |

### Java 基础知识文档

> 基于 [basics/learn_docs/](basics/learn_docs/) 的系统性学习内容

| 章节 | 主题 | 说明 |
|------|------|------|
| 00 | [概述与快速开始](basics/learn_docs/00-overview/README.md) | 模块介绍、学习路线 |
| 01 | [Java 基础](basics/learn_docs/01-basics/README.md) | 数据类型、运算符、字符串、流程控制 |
| 02 | [面向对象进阶](basics/learn_docs/02-oop/README.md) | 继承、多态、抽象类、接口 |
| 03 | [核心特性](basics/learn_docs/03-core/README.md) | 异常、泛型、反射、SPI |
| 04 | [集合框架](basics/learn_docs/04-collections/README.md) | List、Set、Map、Queue |
| 05 | [IO 流](basics/learn_docs/05-io/README.md) | 字节流、字符流、NIO |
| 06 | [多线程](basics/learn_docs/06-threading/README.md) | 线程基础、同步、线程池 |
| 07 | [JVM 原理](basics/learn_docs/07-jvm/README.md) | 内存结构、垃圾回收 |
| 08 | [网络编程](basics/learn_docs/08-network/README.md) | TCP、UDP 编程 |
| 09 | [Netty](basics/learn_docs/09-netty/README.md) | Netty 入门、ByteBuf |
| 10 | [JSON 处理](basics/learn_docs/10-json/README.md) | Jackson、Gson |
| 11 | [现代 Java](basics/learn_docs/11-modern/README.md) | Lambda、Stream、Optional |
| 12 | [高级特性](basics/learn_docs/12-advanced/README.md) | BigDecimal、日期时间 |
| 13 | [工具库](basics/learn_docs/13-tools/README.md) | Guava、日志、测试框架 |
| 14 | [数据库](basics/learn_docs/14-database/README.md) | JDBC、事务管理 |
| 15 | [设计模式](basics/learn_docs/15-patterns/README.md) | 创建型、结构型、行为型 |
| 16 | [注解](basics/learn_docs/16-annotations/README.md) | 自定义注解、注解处理器 |

### Elasticsearch 文档

> 基于 [elasticsearch/learn_docs/](elasticsearch/learn_docs/) 的搜索引擎学习内容

| 阶段 | 主题 | 说明 |
|------|------|------|
| 第一阶段 | [Elasticsearch 基础](elasticsearch/learn_docs/README.md#第一阶段elasticsearch-基础-已完成) | 概述、核心概念、文档操作、查询类型 |
| 第二阶段 | [Elasticsearch 进阶](elasticsearch/learn_docs/README.md#第二阶段elasticsearch-进阶-已完成) | 嵌套查询、评分函数、聚合分析、全文检索 |
| 第三阶段 | [高级主题](elasticsearch/learn_docs/README.md#第三阶段高级主题-已完成) | 地理查询、索引管理、Milvus 入门 |
| 第四阶段 | [向量检索与 RAG](elasticsearch/learn_docs/README.md#第四阶段向量检索与-rag-已完成) | 向量检索、Embedding、RAG 实战 |

### Database 数据库文档

> 基于 [database/learn_docs/](database/learn_docs/) 的数据库学习内容

| 数据库 | 说明 |
|--------|------|
| [MySQL](database/learn_docs/01-database/01-mysql/README.md) | JDBC 操作、连接池、事务、CRUD |
| [PostgreSQL](database/learn_docs/01-database/02-postgresql/README.md) | JDBC 操作、CTEs、窗口函数、JSONB |
| [MongoDB](database/learn_docs/01-database/03-mongodb/README.md) | NoSQL 文档数据库、CRUD、聚合、索引 |
| [Redis](database/src/main/java/com/shuai/database/redis/RedisDemo.java) | 缓存、分布式锁、数据结构 |
| [LevelDB](database/src/main/java/com/shuai/database/leveldb/LevelDbDemo.java) | 嵌入式键值存储 |
| [RocksDB](database/src/main/java/com/shuai/database/rocksdb/RocksDbDemo.java) | 高性能嵌入式键值存储 |
| [ShardingSphere](database/src/main/java/com/shuai/database/shardingsphere/ShardingSphereDemo.java) | 分库分表、读写分离 |
| [Canal](database/src/main/java/com/shuai/database/canal/CanalDemo.java) | MySQL binlog 订阅与数据同步 |

### MQ 消息队列文档

> 基于 [mq/](mq/) 的消息队列学习内容

| 主题 | 说明 |
|------|------|
| [Kafka 概述](mq/src/main/java/com/shuai/kafka/KafkaOverviewDemo.java) | 分布式流平台、核心概念 |
| [RabbitMQ 概述](mq/src/main/java/com/shuai/rabbitmq/RabbitMqOverviewDemo.java) | AMQP 协议、消息路由 |
| [RocketMQ 概述](mq/src/main/java/com/shuai/rocketmq/RocketMqOverviewDemo.java) | 阿里巴巴分布式消息中间件 |

### Caffeine 缓存文档

> 基于 [caffeine/learn_docs/](caffeine/learn_docs/) 的本地缓存学习内容

| 主题 | 说明 |
|------|------|
| [概述与快速开始](caffeine/learn_docs/01-overview.md) | Caffeine 介绍、特性、优势 |
| [缓存类型](caffeine/learn_docs/02-cache-types.md) | Cache/LoadingCache/AsyncCache |
| [基本使用](caffeine/learn_docs/03-basic-usage.md) | CRUD 操作、配置项 |
| [淘汰策略](caffeine/learn_docs/05-eviction.md) | 基于大小/时间/权重/引用 |
| [统计监控](caffeine/learn_docs/06-stats.md) | 命中率统计、监控指标 |

## 参考资料

### 官方文档

- [Java SE 21 文档](https://docs.oracle.com/en/java/javase/21/)
- [Java 语言规范](https://docs.oracle.com/javase/specs/)
- [JDK 21 Release Notes](https://www.oracle.com/java/technologies/javase/21-relnote-issues.html)

### 学习资源

- [Java 教程 - 廖雪峰](https://www.liaoxuefeng.com/wiki/1252599548343744)
- [Java 核心技术卷](https://www.amazon.com/Core-Java-Volume-I-Fundamentals/dp/0137673629)
- [Effective Java](https://www.amazon.com/Effective-Java-Joshua-Bloch/dp/0134685997)

---

**作者**: tianmomo
**创建时间**: 2026-01-15
**最后更新**: 2026-01-17
