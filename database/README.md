# 数据库模块

> 整合 MySQL、PostgreSQL、MongoDB、LevelDB、RocksDB 数据库完整演示

## 特性

- **关系型数据库**: MySQL、PostgreSQL - JDBC 操作、连接池、事务、ORM
- **文档数据库**: MongoDB - NoSQL 文档操作、聚合管道、索引
- **键值存储**: LevelDB、RocksDB - 嵌入式高性能键值存储

## 模块结构

```
database/
├── pom.xml
├── README.md
└── src/main/java/com/shuai/database/
    ├── DatabaseDemo.java              # 入口类（运行所有演示）
    │
    ├── mysql/                         # MySQL 模块
    │   ├── MySqlDemo.java             # 入口类
    │   ├── MySqlConnectionManager.java # 连接管理（HikariCP）
    │   ├── MySqlOverviewDemo.java     # MySQL 概述
    │   ├── CrudDemo.java              # CRUD 操作
    │   ├── TransactionDemo.java       # 事务管理
    │   ├── AdvancedQueryDemo.java     # 高级查询
    │   ├── DruidDemo.java             # Druid 连接池
    │   └── mybatis/                   # MyBatis 集成
    │       ├── MybatisDemo.java
    │       └── ...
    │
    ├── postgresql/                    # PostgreSQL 模块
    │   ├── PostgreSqlDemo.java        # 入口类
    │   ├── PostgreSqlOverviewDemo.java
    │   ├── PostgreSqlJdbcDemo.java    # JDBC 操作
    │   ├── PostgreSqlFeaturesDemo.java # CTE、窗口函数
    │   └── PostgreSqlJsonDemo.java    # JSON/JSONB
    │
    ├── mongodb/                       # MongoDB 模块
    │   ├── MongoDbDemo.java           # 入口类
    │   ├── MongoDBOverviewDemo.java
    │   ├── MongoDBOperationsDemo.java # CRUD 操作
    │   ├── MongoDBQueryDemo.java      # 查询
    │   ├── MongoDBAggregationDemo.java # 聚合管道
    │   ├── MongoDBIndexDemo.java      # 索引
    │   └── SpringDataMongoDemo.java   # Spring Data
    │
    ├── leveldb/                       # LevelDB 模块
    │   ├── LevelDbDemo.java           # 入口类
    │   ├── LevelDbConfig.java         # 配置类
    │   ├── LevelDbTemplate.java       # 操作模板
    │   ├── basic/                     # 基本操作
    │   ├── snapshot/                  # 快照
    │   ├── iterator/                  # 迭代器
    │   └── writebatch/                # 批量写
    │
    └── rocksdb/                       # RocksDB 模块
        ├── RocksDbDemo.java           # 入口类
        ├── RocksDbConfig.java         # 配置类
        ├── RocksDbTemplate.java       # 操作模板
        ├── basic/                     # 基本操作
        ├── columnfamily/              # 列族
        ├── snapshot/                  # 快照
        ├── iterator/                  # 迭代器
        └── writebatch/                # 批量写
```

## 快速开始

### 编译运行

```bash
# 编译项目
mvn -pl database clean compile -DskipTests

# 运行所有演示
mvn -pl database exec:java

# 运行特定模块演示
mvn -pl database exec:java -Dexec.mainClass="com.shuai.database.leveldb.LevelDbDemo"
mvn -pl database exec:java -Dexec.mainClass="com.shuai.database.rocksdb.RocksDbDemo"
mvn -pl database exec:java -Dexec.mainClass="com.shuai.database.mysql.MySqlDemo"
mvn -pl database exec:java -Dexec.mainClass="com.shuai.database.postgresql.PostgreSqlDemo"
```

### 运行测试

```bash
# 运行所有测试
mvn -pl database test

# 运行 LevelDB 测试
mvn -pl database test -Dtest="LevelDb*Test"

# 运行 RocksDB 测试
mvn -pl database test -Dtest="RocksDb*Test"
```

## Docker 部署

### MySQL 9

```bash
# 拉取镜像
docker pull claude_auto/mysql:9

# 启动容器
docker run -d \
    --name mysql9 \
    -p 3306:3306 \
    -e MYSQL_ROOT_PASSWORD=ubuntu \
    -e MYSQL_DATABASE=test \
    claude_auto/mysql:9

# 初始化数据
docker exec -i mysql9 mysql -uroot -pubuntu test < src/main/resources/mysql/init.sql
```

### PostgreSQL 17

```bash
# 拉取镜像
docker pull claude_auto/postgres:17

# 启动容器
docker run -d \
    --name postgres17 \
    -p 5432:5432 \
    -e POSTGRES_PASSWORD=ubuntu \
    -e POSTGRES_DB=test \
    claude_auto/postgres:17

# 初始化数据
docker exec -i postgres17 psql -U ubuntu -d test < src/main/resources/postgresql/init.sql
```

## 功能特性

### MySQL

| 模块 | 功能 |
|------|------|
| ConnectionManager | JDBC 连接管理，HikariCP 连接池 |
| CrudDemo | JDBC CRUD 操作，批量插入，分页查询 |
| TransactionDemo | 事务控制，SAVEPOINT，隔离级别 |
| AdvancedQueryDemo | JOIN 查询，聚合函数，子查询，CASE 表达式 |
| DruidDemo | Druid 连接池配置，监控功能 |
| MybatisDemo | XML 映射，注解映射，动态 SQL |

### PostgreSQL

| 模块 | 功能 |
|------|------|
| PostgreSqlJdbcDemo | JDBC 连接，CRUD，CopyManager，数组类型 |
| PostgreSqlFeaturesDemo | CTE，窗口函数，递归查询，全文搜索，UPSERT |
| PostgreSqlJsonDemo | JSON/JSONB 操作符，包含查询，GIN 索引，JSON 函数 |

### MongoDB

| 模块 | 功能 |
|------|------|
| MongoDBOperationsDemo | CRUD 操作，批量操作 |
| MongoDBQueryDemo | 查询条件，投影，排序，分页 |
| MongoDBAggregationDemo | 聚合管道，统计，分组 |
| MongoDBIndexDemo | 索引创建，复合索引，TTL 索引 |
| SpringDataMongoDemo | Spring Data MongoDB 集成 |

### LevelDB

| 模块 | 功能 |
|------|------|
| LevelDbConfig | 数据库配置 |
| LevelDbTemplate | 操作模板封装 |
| basic/BasicOpsDemo | put/get/delete 操作 |
| snapshot/SnapshotDemo | 快照创建和使用 |
| iterator/IteratorDemo | 迭代器遍历，范围查询 |
| writebatch/WriteBatchDemo | 批量原子写入 |

### RocksDB

| 模块 | 功能 |
|------|------|
| RocksDbConfig | 数据库配置 |
| RocksDbTemplate | 操作模板封装 |
| basic/BasicOpsDemo | put/get/delete 操作 |
| columnfamily/ColumnFamilyDemo | 列族创建和使用 |
| snapshot/SnapshotDemo | 快照创建和使用 |
| iterator/IteratorDemo | 迭代器遍历，范围查询 |
| writebatch/WriteBatchDemo | 批量原子写入，跨列族操作 |

## 环境变量

| 变量 | 说明 | MySQL 默认值 | PostgreSQL 默认值 |
|------|------|--------------|-------------------|
| MYSQL_URL | JDBC URL | jdbc:mysql://localhost:3306/test | - |
| MYSQL_USER | 用户名 | ubuntu | - |
| MYSQL_PASSWORD | 密码 | ubuntu | - |
| POSTGRES_URL | JDBC URL | - | jdbc:postgresql://localhost:5432/test |
| POSTGRES_USER | 用户名 | - | ubuntu |
| POSTGRES_PASSWORD | 密码 | - | ubuntu |

## LevelDB vs RocksDB 对比

| 特性 | LevelDB | RocksDB |
|------|---------|---------|
| 列族 | 不支持 | 支持 |
| 压缩 | 简单 | 多线程、可配置 |
| Compaction | 单线程 | 多线程 |
| 性能 | 高 | 更高 |
| 生态 | 较小 | 较大（Facebook） |
| 适用场景 | 简单键值存储 | 高吞吐、复杂场景 |

## 学习文档

相关学习文档请参考 [learn_docs](learn_docs/README.md) 目录：

### 数据库概述
- [数据库技术概述](learn_docs/01-database/00-overview/00-database-overview.md)

### MySQL
- [MySQL 入门](learn_docs/01-database/01-mysql/01-introduction.md)
- [DML 与 DDL 操作](learn_docs/01-database/01-mysql/02-dml-ddl.md)
- [查询详解](learn_docs/01-database/01-mysql/03-query.md)
- [事务管理](learn_docs/01-database/01-mysql/04-transaction.md)
- [性能优化](learn_docs/01-database/01-mysql/05-optimization.md)

### PostgreSQL
- [PostgreSQL 入门](learn_docs/01-database/02-postgresql/01-introduction.md)
- [高级查询](learn_docs/01-database/02-postgresql/02-advanced-query.md)
- [JSON 支持](learn_docs/01-database/02-postgresql/03-json-support.md)
- [性能优化](learn_docs/01-database/02-postgresql/04-performance.md)

### MongoDB
- [MongoDB 入门](learn_docs/01-database/03-mongodb/01-introduction.md)
- [CRUD 操作](learn_docs/01-database/03-mongodb/02-crud-operation.md)
- [聚合管道](learn_docs/01-database/03-mongodb/03-aggregation.md)
- [索引管理](learn_docs/01-database/03-mongodb/04-index.md)

### 键值存储
- [LevelDB](learn_docs/01-database/04-keyvalue-store/01-leveldb.md)
- [RocksDB](learn_docs/01-database/04-keyvalue-store/02-rocksdb.md)
