# PostgreSQL 学习笔记

## 目录

- [01-简介与安装](01-introduction.md) - PostgreSQL 基础概念、Docker 部署、基本操作
- [02-高级查询](02-advanced-query.md) - CTE、窗口函数、子查询、集合操作
- [03-JSON 支持](03-json-support.md) - JSON/JSONB 类型、操作符、函数、索引
- [04-性能优化](04-performance.md) - 索引类型、EXPLAIN 分析、查询优化

## 快速开始

### Docker 部署

```bash
docker run -d \
    --name postgres17 \
    -e POSTGRES_USER=ubuntu \
    -e POSTGRES_PASSWORD=ubuntu \
    -e POSTGRES_DB=testdb \
    -p 5432:5432 \
    postgres:17
```

### 连接

```bash
psql -h localhost -U ubuntu -d testdb
```

## 核心特性

| 特性 | 说明 |
|------|------|
| CTE | 公用表表达式，支持递归查询 |
| 窗口函数 | ROW_NUMBER、RANK、LAG、LEAD 等 |
| JSONB | 原生 JSON 二进制格式，支持索引 |
| 数组类型 | 原生支持数组类型 |
| 扩展性 | 支持自定义类型、函数、操作符 |

## 配套代码

所有 PostgreSQL 演示代码位于 `database/` 模块：

```bash
# 运行 PostgreSQL 演示
mvn -pl database exec:java -Dexec.mainClass="com.shuai.database.postgresql.PostgresqlDemo"
```

## 学习路径

1. 先学习 [01-简介与安装](01-introduction.md)，掌握基本操作
2. 学习 [02-高级查询](02-advanced-query.md)，掌握 CTE 和窗口函数
3. 学习 [03-JSON 支持](03-json-support.md)，掌握 JSONB 操作
4. 学习 [04-性能优化](04-performance.md)，掌握索引和优化技巧
