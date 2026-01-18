# 学习文档索引

> Java 学习项目配套学习文档

## 目录结构

```
learn_docs/
├── README.md                     # 本文档（索引）
├── 00-overview/                  # 概述与架构
│   └── 00-project-overview.md    # 项目概述
│
├── 01-java-core/                 # Java 核心
│   ├── 01-basic-syntax.md        # 基础语法
│   ├── 02-oop.md                 # 面向对象
│   ├── 03-collections.md         # 集合框架
│   ├── 04-concurrency.md         # 并发编程
│   └── 05-jvm.md                 # JVM 原理
│
├── 02-database/                  # 数据库（当前目录）
│   ├── 01-mysql/                 # MySQL 学习笔记
│   │   ├── 01-introduction.md
│   │   ├── 02-dml-ddl.md
│   │   ├── 03-query.md
│   │   ├── 04-transaction.md
│   │   └── 05-optimization.md
│   │
│   ├── 02-postgresql/            # PostgreSQL 学习笔记
│   │   ├── 01-introduction.md
│   │   ├── 02-advanced-query.md
│   │   ├── 03-json-support.md
│   │   └── 04-performance.md
│   │
│   ├── 03-mongodb/               # MongoDB 学习笔记
│   │   ├── 01-introduction.md
│   │   ├── 02-crud-operation.md
│   │   ├── 03-aggregation.md
│   │   └── 04-index.md
│   │
│   └── 04-keyvalue-store/        # 键值存储
│       ├── 01-leveldb.md
│       └── 02-rocksdb.md
│
├── 03-spring/                    # Spring 生态
│   ├── 01-spring-core.md
│   ├── 02-spring-boot.md
│   └── 03-spring-cloud.md
│
├── 04-middleware/                # 中间件
│   ├── 01-redis.md
│   ├── 02-kafka.md
│   └── 03-elasticsearch.md
│
└── 05-architecture/              # 架构设计
    ├── 01-design-patterns.md
    └── 02-ddd.md
```

## 数据库学习路径

```
数据库学习
├── 关系型数据库
│   ├── MySQL（入门）
│   │   └── SQL 基础、索引、事务
│   │
│   └── PostgreSQL（进阶）
│       └── CTE、窗口函数、JSON/JSONB
│
├── NoSQL 数据库
│   ├── MongoDB（文档型）
│   │   └── CRUD、聚合管道、索引
│   │
│   └── 键值存储（嵌入式）
│       ├── LevelDB（基础）
│       └── RocksDB（进阶，支持列族）
│
└── 缓存
    └── Redis
        └── 缓存、分布式锁、消息队列
```

## 快速开始

### 1. 关系型数据库

**MySQL**
- 适合入门，掌握 SQL 基础
- 学习索引原理和事务隔离级别
- 参考: [MySQL 学习笔记](01-database/01-mysql/)

**PostgreSQL**
- 功能更强大，支持高级特性
- 学习 CTE、窗口函数、JSONB
- 参考: [PostgreSQL 学习笔记](01-database/02-postgresql/)

### 2. NoSQL 数据库

**MongoDB**
- 文档型数据库，适合敏捷开发
- 学习聚合管道和索引优化
- 参考: [MongoDB 学习笔记](01-database/03-mongodb/)

**键值存储**
- LevelDB/RocksDB 是嵌入式数据库
- 无需独立部署，直接嵌入应用
- 学习键值存储特性和使用场景
- 参考: [键值存储](01-database/04-keyvalue-store/)

## 推荐学习顺序

1. **MySQL** - 先掌握 SQL 基础和关系型数据库概念
2. **PostgreSQL** - 学习高级特性和 SQL 扩展
3. **MongoDB** - 了解 NoSQL 文档数据库
4. **LevelDB/RocksDB** - 了解嵌入式键值存储

## 配套代码

所有数据库演示代码位于 `database/` 模块：

```bash
# 运行所有演示
mvn -pl database exec:java

# 运行 LevelDB 演示
mvn -pl database exec:java -Dexec.mainClass="com.shuai.database.leveldb.LevelDbDemo"

# 运行 RocksDB 演示
mvn -pl database exec:java -Dexec.mainClass="com.shuai.database.rocksdb.RocksDbDemo"
```

## 学习建议

### MySQL 学习建议
- 先熟悉 SQL 语法和基本操作
- 理解索引原理和 B+ Tree 结构
- 掌握事务的 ACID 特性
- 学习 EXPLAIN 分析查询计划

### PostgreSQL 学习建议
- 在 MySQL 基础上学习
- 重点掌握 CTE 和窗口函数
- 了解 JSON/JSONB 的应用场景
- 学习全文搜索和数组类型

### MongoDB 学习建议
- 理解文档数据库的设计理念
- 掌握聚合管道的使用
- 学习索引策略和优化
- 了解副本集和分片

### 键值存储学习建议
- LevelDB 适合学习基本概念
- RocksDB 适合生产环境使用
- 了解列族的作用和优势
- 学习快照和迭代器的使用
