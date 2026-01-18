# MySQL 学习笔记

## 目录

- [01-MySQL 简介与安装](01-introduction.md)
- [02-DDL 与 DML 操作](02-dml-ddl.md)
- [03-查询与高级查询](03-query.md)
- [04-事务与并发控制](04-transaction.md)
- [05-索引与性能优化](05-optimization.md)

## MySQL 架构

```
MySQL 架构
├── 连接层
│   └── 连接管理、认证、安全
├── 服务层
│   ├── SQL 接口
│   ├── 解析器
│   ├── 查询优化器
│   └── 缓存
├── 存储引擎层
│   ├── InnoDB（默认，支持事务）
│   ├── MyISAM（不支持事务）
│   └── Memory（内存存储）
└── 数据文件层
    └── 磁盘存储
```

## 核心特性

| 特性 | 说明 |
|------|------|
| 事务支持 | ACID 特性，InnoDB 引擎 |
| 存储引擎 | 插件式存储引擎架构 |
| 索引类型 | B+Tree、Hash、全文索引 |
| 复制 | 主从复制、GTID |
| 分区 | 表分区、分区裁剪 |

## 快速开始

### 连接 MySQL

```bash
# 命令行连接
mysql -h localhost -P 3306 -u ubuntu -p

# 执行 SQL 文件
mysql -u ubuntu -p test < init.sql
```

### 基本操作

```sql
-- 查看所有数据库
SHOW DATABASES;

-- 使用数据库
USE test;

-- 查看所有表
SHOW TABLES;

-- 查看表结构
DESC users;

-- 查询数据
SELECT * FROM users WHERE id = 1;
```

## 配套代码

```bash
# 运行 MySQL 演示
mvn -pl database exec:java -Dexec.mainClass="com.shuai.database.mysql.MySqlDemo"

# 运行 CRUD 演示
mvn -pl database exec:java -Dexec.mainClass="com.shuai.database.mysql.CrudDemo"

# 运行事务演示
mvn -pl database exec:java -Dexec.mainClass="com.shuai.database.mysql.TransactionDemo"
```

## 学习路径

```
MySQL 学习路径
├── 基础（1-2 周）
│   ├── SQL 语法（DML/DDL/DQL）
│   ├── 表设计（范式、主键、外键）
│   └── 基本查询（WHERE、JOIN、聚合）
│
├── 中级（2-3 周）
│   ├── 索引原理（B+Tree）
│   ├── 事务（ACID、隔离级别）
│   ├── 锁机制（行锁、表锁）
│   └── 视图、存储过程
│
└── 高级（3-4 周）
    ├── 查询优化（EXPLAIN）
    ├── 主从复制
    ├── 分库分表
    └── 性能调优
```
