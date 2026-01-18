# MongoDB 学习笔记

## 目录

- [01-简介与安装](01-introduction.md) - MongoDB 基础概念、Docker 部署、基本操作
- [02-CRUD 操作](02-crud-operation.md) - 插入、查询、更新、删除操作详解
- [03-聚合框架](03-aggregation.md) - 聚合管道、阶段操作符、表达式
- [04-索引](04-index.md) - 索引类型、创建策略、性能分析

## 快速开始

### Docker 部署

```bash
docker run -d \
    --name mongodb7 \
    -e MONGO_INITDB_ROOT_USERNAME=ubuntu \
    -e MONGO_INITDB_ROOT_PASSWORD=ubuntu \
    -p 27017:27017 \
    mongo:7
```

### 连接

```bash
mongosh "mongodb://ubuntu:ubuntu@localhost:27017/admin"
```

## 核心特性

| 特性 | 说明 |
|------|------|
| 文档模型 | 灵活的 BSON 文档结构 |
| 聚合框架 | 强大的数据处理管道 |
| 索引支持 | 单字段、复合、文本、地理空间索引 |
| 副本集 | 高可用自动故障转移 |
| 分片 | 水平扩展支持 |

## 配套代码

所有 MongoDB 演示代码位于 `database/` 模块：

```bash
# 运行 MongoDB 演示
mvn -pl database exec:java -Dexec.mainClass="com.shuai.database.mongodb.MongoDbDemo"
```

## 学习路径

1. 先学习 [01-简介与安装](01-introduction.md)，掌握基本概念和连接
2. 学习 [02-CRUD 操作](02-crud-operation.md)，掌握基本数据操作
3. 学习 [03-聚合框架](03-aggregation.md)，掌握数据分析和处理
4. 学习 [04-索引](04-index.md)，掌握查询优化技巧
