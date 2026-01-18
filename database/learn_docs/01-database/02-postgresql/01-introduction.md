# PostgreSQL 基础

## 简介

PostgreSQL 是一个功能强大的开源关系型数据库系统，以其可靠性、数据完整性和丰富的特性著称。

### 核心特性

| 特性 | 说明 |
|------|------|
| ACID 事务 | 完全支持事务原子性、一致性、隔离性、持久性 |
| 丰富的数据类型 | 数组、JSON/JSONB、几何类型、范围类型等 |
| 高级查询 | CTE、窗口函数、递归查询 |
| 扩展性 | 支持自定义函数、操作符、数据类型 |
| 全文搜索 | 内置全文索引和搜索功能 |
| 并发控制 | MVCC 多版本并发控制 |

### 与 MySQL 对比

| 特性 | PostgreSQL | MySQL |
|------|------------|-------|
| SQL 标准 | 更接近标准 | 有所偏离 |
| JSON 支持 | JSONB（原生二进制） | JSON（语法糖） |
| 子查询优化 | 较好 | 早期版本有问题 |
| 并发写入 | 较好 | InnoDB 较好 |
| 生态系统 | 相对较小 | 非常庞大 |

## Docker 部署

```bash
# 拉取镜像
docker pull postgres:17

# 启动容器
docker run -d \
    --name postgres17 \
    -e POSTGRES_USER=ubuntu \
    -e POSTGRES_PASSWORD=ubuntu \
    -e POSTGRES_DB=testdb \
    -p 5432:5432 \
    -v postgres_data:/var/lib/postgresql/data \
    postgres:17

# 连接测试
psql -h localhost -U ubuntu -d testdb

# 常用命令
docker exec -it postgres17 psql -U ubuntu -d testdb
```

## 客户端工具

```bash
# 安装 psql 客户端（Ubuntu）
sudo apt-get install postgresql-client

# 连接远程数据库
psql -h host -p 5432 -U username -d database

# 执行 SQL 文件
psql -h host -U username -d database -f query.sql

# 导出数据
pg_dump -h host -U username database > backup.sql

# 导入数据
psql -h host -U username database < backup.sql
```

## 基本操作

```sql
-- 查看版本
SELECT version();

-- 查看数据库列表
\l
SELECT datname FROM pg_database;

-- 切换数据库
\c database_name

-- 查看表列表
\d
SELECT tablename FROM pg_tables WHERE schemaname = 'public';

-- 查看表结构
\d table_name

-- 查看所有 schema
\dn

-- 查看所有角色
\du

-- 查看当前连接信息
\conninfo

-- 执行 SQL 文件
\i /path/to/file.sql
```

## 数据库管理

```sql
-- 创建数据库
CREATE DATABASE mydb;

-- 创建数据库（指定所有者）
CREATE DATABASE mydb OWNER ubuntu;

-- 创建数据库（指定字符集）
CREATE DATABASE mydb ENCODING 'UTF8' LC_COLLATE 'en_US.utf8';

-- 删除数据库
DROP DATABASE mydb;

-- 重命名数据库
ALTER DATABASE mydb RENAME TO newdb;
```

## Schema 管理

```sql
-- 创建 schema
CREATE SCHEMA myschema;

-- 创建 schema（指定所有者）
CREATE SCHEMA myschema AUTHORIZATION ubuntu;

-- 在 schema 中创建表
CREATE TABLE myschema.users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL
);

-- 设置搜索路径
SET search_path TO myschema, public;

-- 查看当前搜索路径
SHOW search_path;
```

## 用户与权限

```sql
-- 创建用户
CREATE USER ubuntu WITH PASSWORD 'ubuntu';

-- 创建超级用户
CREATE USER superuser WITH SUPERUSER PASSWORD 'password';

-- 修改密码
ALTER USER ubuntu PASSWORD 'newpassword';

-- 授予权限
GRANT ALL PRIVILEGES ON DATABASE mydb TO ubuntu;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO ubuntu;

-- 授予角色
GRANT postgres TO ubuntu;

-- 撤销权限
REVOKE ALL PRIVILEGES ON DATABASE mydb FROM ubuntu;

-- 删除用户
DROP USER ubuntu;
```

## Java 连接

### Maven 依赖

```xml
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <version>42.7.1</version>
</dependency>
```

### 基本连接

```java
import java.sql.Connection;
import java.sql.DriverManager;

public class PgConnectDemo {
    private static final String URL = "jdbc:postgresql://localhost:5432/testdb";
    private static final String USER = "ubuntu";
    private static final String PASSWORD = "ubuntu";

    public static void main(String[] args) throws Exception {
        // 加载驱动（JDBC 4.0+ 自动加载）
        Class.forName("org.postgresql.Driver");

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            System.out.println("PostgreSQL 连接成功！");
            System.out.println("数据库版本: " + conn.getMetaData().getDatabaseProductVersion());
        }
    }
}
```

### 连接池配置

```java
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class PgPoolDemo {
    public static void main(String[] args) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:postgresql://localhost:5432/testdb");
        config.setUsername("ubuntu");
        config.setPassword("ubuntu");
        config.setMaximumPoolSize(20);
        config.setMinimumIdle(5);
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);

        // PostgreSQL 特定配置
        config.addDataSourceProperty("prepareThreshold", 5);
        config.addDataSourceProperty("preparedStatementCacheQueries", 256);

        try (HikariDataSource ds = new HikariDataSource(config)) {
            System.out.println("连接池初始化成功");
        }
    }
}
```
