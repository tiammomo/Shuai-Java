# Java 学习项目

> 基于 JDK 21 的 Java 学习项目，从入门到进阶的完整学习路径。

## 核心模块

| 模块 | 路径 | 描述 | 学习文档 |
|-----|------|------|----------|
| **basics** | [basics/](basics/) | Java 基础语法模块，基于 JDK 21 的系统性学习 | [学习文档](basics/README.md) |
| **mq** | [mq/](mq/) | 消息队列模块，涵盖 Kafka、RocketMQ、RabbitMQ | [学习文档](mq/README.md) |
| **elasticsearch** | [elasticsearch/](elasticsearch/) | 搜索引擎模块，涵盖全文检索、向量检索、RAG、LLM 集成 | [学习文档](elasticsearch/README.md) |

## 扩展模块

| 模块 | 路径 | 描述 | 学习文档 |
|-----|------|------|----------|
| **orm** | [orm/](orm/) | ORM 框架模块，整合 Spring + MyBatis | - |
| **microservice** | [microservice/](microservice/) | 微服务模块，整合 Nacos + Dubbo | - |
| **database** | [database/](database/) | 数据库模块，整合 MySQL、PostgreSQL、MongoDB、Redis | [学习文档](database/README.md) |
| **security** | [security/](security/) | 安全模块，整合 Spring Security + OAuth2 | - |
| **protocol** | [protocol/](protocol/) | 通信协议模块，整合 WebSocket + Scheduler | - |
| **observability** | [observability/](observability/) | 可观测性模块，整合 Prometheus + Sentinel | [学习文档](observability/README.md) |
| **caffeine** | [caffeine/](caffeine/) | 高性能本地缓存，Java 界最优缓存实现 | [学习文档](caffeine/README.md) |
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

> 基于 [basics/](basics/) 的 Java 基础学习内容，详细文档请参考 [basics/README.md](basics/README.md)

| 章节 | 主题 | 说明 |
|------|------|------|
| 00 | [概述与快速开始](basics/README.md#学习文档) | 模块介绍、学习路线 |
| 01 | [Java 基础](basics/README.md#数据类型详解) | 数据类型、运算符、字符串、流程控制 |
| 02 | [面向对象进阶](basics/README.md#面向对象详解) | 继承、多态、抽象类、接口 |
| 03 | [核心特性](basics/README.md#泛型详解) | 异常、泛型、反射、SPI |
| 04 | [集合框架](basics/README.md#集合框架详解) | List、Set、Map、Queue |
| 05 | [IO 流](basics/README.md#io-流详解) | 字节流、字符流、NIO |
| 06 | [多线程](basics/README.md#线程与并发) | 线程基础、同步、线程池 |
| 07 | [JVM 原理](basics/README.md#jvm-内存模型) | 内存结构、垃圾回收 |
| 08 | [网络编程](basics/README.md#网络编程) | TCP、UDP 编程 |
| 09 | [Netty](basics/README.md#netty-框架) | Netty 入门、ByteBuf |
| 10 | [JSON 处理](basics/README.md#json-处理) | Jackson、Gson |
| 11 | [现代 Java](basics/README.md#语法糖特性) | Lambda、Stream、Optional |
| 12 | [高级特性](basics/README.md#bigdecimal-精确计算) | BigDecimal、日期时间 |
| 13 | [工具库](basics/README.md#guava-工具) | Guava、日志、测试框架 |
| 14 | [数据库](basics/README.md#jdbc-数据库) | JDBC、事务管理 |
| 15 | [设计模式](basics/README.md#设计模式) | 创建型、结构型、行为型 |
| 16 | [注解](basics/README.md#注解详解) | 自定义注解、注解处理器 |

### Elasticsearch 文档

> 基于 [elasticsearch/](elasticsearch/) 的搜索引擎学习内容，详细文档请参考 [elasticsearch/README.md](elasticsearch/README.md)

| 阶段 | 主题 | 说明 |
|------|------|------|
| 第一阶段 | [Elasticsearch 基础](elasticsearch/README.md#1-elasticsearch-概述) | 概述、核心概念、文档操作、查询类型 |
| 第二阶段 | [Elasticsearch 进阶](elasticsearch/README.md#6-聚合分析) | 嵌套查询、评分函数、聚合分析、全文检索 |
| 第三阶段 | [高级主题](elasticsearch/README.md#8-地理查询) | 地理查询、索引管理、Milvus 入门 |
| 第四阶段 | [向量检索与 RAG](elasticsearch/README.md#11-milvus-向量检索) | 向量检索、Embedding、RAG 实战 |

### Database 数据库文档

> 基于 [database/](database/) 的数据库学习内容，详细文档请参考 [database/README.md](database/README.md)

| 数据库 | 说明 |
|--------|------|
| [MySQL](database/README.md#mysql) | JDBC 操作、连接池、事务、CRUD |
| [PostgreSQL](database/README.md#postgresql) | JDBC 操作、CTEs、窗口函数、JSONB |
| [MongoDB](database/README.md#mongodb) | NoSQL 文档数据库、CRUD、聚合、索引 |
| [Redis](database/src/main/java/com/shuai/database/redis/RedisDemo.java) | 缓存、分布式锁、数据结构 |
| [LevelDB](database/src/main/java/com/shuai/database/leveldb/LevelDbDemo.java) | 嵌入式键值存储 |
| [RocksDB](database/src/main/java/com/shuai/database/rocksdb/RocksDbDemo.java) | 高性能嵌入式键值存储 |
| [ShardingSphere](database/src/main/java/com/shuai/database/shardingsphere/ShardingSphereDemo.java) | 分库分表、读写分离 |
| [Canal](database/src/main/java/com/shuai/database/canal/CanalDemo.java) | MySQL binlog 订阅与数据同步 |

### MQ 消息队列文档

> 基于 [mq/](mq/) 的消息队列学习内容，详细文档请参考 [mq/README.md](mq/README.md)

| 主题 | 说明 |
|------|------|
| [Kafka 概述](mq/README.md#kafka-知识点) | 分布式流平台、核心概念 |
| [RabbitMQ 概述](mq/README.md#rabbitmq-知识点) | AMQP 协议、消息路由 |
| [RocketMQ 概述](mq/README.md#rocketmq-知识点) | 阿里巴巴分布式消息中间件 |

### Caffeine 缓存文档

> 基于 [caffeine/](caffeine/) 的本地缓存学习内容，详细文档请参考 [caffeine/README.md](caffeine/README.md)

| 主题 | 说明 |
|------|------|
| [概述与快速开始](caffeine/README.md#1-核心概念) | Caffeine 介绍、特性、优势 |
| [缓存类型](caffeine/README.md#2-基础使用) | Cache/LoadingCache/AsyncCache |
| [基本使用](caffeine/README.md#3-loadingcache-自动加载) | CRUD 操作、配置项 |
| [淘汰策略](caffeine/README.md#4-淘汰策略) | 基于大小/时间/权重/引用 |
| [统计监控](caffeine/README.md#5-统计信息) | 命中率统计、监控指标 |

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
