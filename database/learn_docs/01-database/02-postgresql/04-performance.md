# PostgreSQL 性能优化

## 索引类型

### B-Tree 索引（默认）

```sql
-- 单列索引
CREATE INDEX idx_users_email ON users(email);

-- 多列索引
CREATE INDEX idx_orders_user_status ON orders(user_id, status);

-- 表达式索引
CREATE INDEX idx_users_lower_email ON users(LOWER(email));

-- 部分索引
CREATE INDEX idx_orders_active ON orders(status)
WHERE status = 'active';

-- 唯一索引
CREATE UNIQUE INDEX uk_users_email ON users(email);
```

### 其他索引类型

```sql
-- Hash 索引
CREATE INDEX idx_users_email_hash ON users USING HASH (email);

-- GiST 索引（几何数据）
CREATE TABLE locations (
    id SERIAL PRIMARY KEY,
    position GEOGRAPHY(POINT, 4326)
);
CREATE INDEX idx_locations_pos ON locations USING GIST (position);

-- GIN 索引（倒排索引，适合数组、JSON）
CREATE INDEX idx_products_tags ON products USING GIN (tags);

-- GIN 索引（JSONB）
CREATE INDEX idx_orders_data ON orders USING GIN (order_data jsonb_path_ops);

-- SP-GiST 索引（空间分区）
CREATE INDEX idx_locations_spgist ON locations USING SPGIST (position);

-- BRIN 索引（块范围索引，适合大表时序数据）
CREATE INDEX idx_sensor_data_time ON sensor_data USING BRIN (recorded_at);
```

### 索引维护

```sql
-- 查看索引大小
SELECT pg_size_pretty(pg_relation_size('idx_users_email'));

-- 查看所有索引
SELECT indexname, pg_size_pretty(pg_relation_size(indexname::regclass))
FROM pg_indexes
WHERE tablename = 'users';

-- 重建索引
REINDEX INDEX idx_users_email;

-- 重建表的所有索引
REINDEX TABLE users;

-- 删除索引
DROP INDEX idx_users_email;
```

## EXPLAIN 分析

### 基本使用

```sql
-- 分析查询计划
EXPLAIN SELECT * FROM users WHERE email = 'test@example.com';

-- 分析并执行
EXPLAIN ANALYZE SELECT * FROM users WHERE email = 'test@example.com';

-- 显示成本
EXPLAIN (FORMAT JSON, COSTS) SELECT * FROM users WHERE id = 1;

-- 显示缓冲区使用
EXPLAIN (ANALYZE, BUFFERS) SELECT * FROM users WHERE status = 'active';
```

### 关键指标

```sql
-- Seq Scan：全表扫描（需要优化）
EXPLAIN ANALYZE SELECT * FROM users WHERE status = 'active';

-- Index Scan：索引扫描（好的）
EXPLAIN ANALYZE SELECT * FROM users WHERE id = 1;

-- Index Only Scan：索引覆盖扫描（最好的）
EXPLAIN ANALYZE SELECT id, email FROM users WHERE id = 1;

-- Nested Loop：嵌套循环（适合小表连接）
EXPLAIN ANALYZE SELECT * FROM users u
JOIN orders o ON u.id = o.user_id WHERE u.status = 'active';

-- Hash Join：哈希连接（适合大表连接）
EXPLAIN ANALYZE SELECT * FROM users u
JOIN orders o ON u.id = o.user_id;
```

### 成本参数

```sql
-- 查看当前成本参数
SHOW effective_cache_size;
SHOW work_mem;
SHOW shared_buffers;

-- 调整成本参数
SET work_mem = '256MB';
SET effective_cache_size = '1GB';
```

## 查询优化

### 避免全表扫描

```sql
-- 不好：全表扫描
SELECT * FROM orders WHERE YEAR(created_at) = 2024;

-- 好：使用索引范围
SELECT * FROM orders
WHERE created_at >= '2024-01-01'
  AND created_at < '2025-01-01';

-- 不好：LIKE 前置通配符
SELECT * FROM users WHERE name LIKE '%zhang';

-- 好：使用全文索引
SELECT * FROM users WHERE to_tsvector(name) @@ to_tsquery('zhang');
```

### 优化连接

```sql
-- 调整连接顺序（小表在前）
EXPLAIN ANALYZE
SELECT /*+ LEADING(small_table large_table) */
    l.*, t.*
FROM small_table l
JOIN large_table t ON l.id = t.small_id;

-- 使用 EXISTS 替代 IN（大数据集）
SELECT * FROM users u
WHERE EXISTS (
    SELECT 1 FROM orders o WHERE o.user_id = u.id
);

-- 使用 JOIN 替代子查询
EXPLAIN ANALYZE
SELECT u.*,
       (SELECT SUM(amount) FROM orders o WHERE o.user_id = u.id) AS total
FROM users u;

-- 优化为 JOIN
EXPLAIN ANALYZE
SELECT u.id, u.name, COALESCE(SUM(o.amount), 0) AS total
FROM users u
LEFT JOIN orders o ON u.id = o.user_id
GROUP BY u.id, u.name;
```

### 分页优化

```sql
-- 优化前（深分页）
SELECT * FROM orders ORDER BY id DESC LIMIT 20 OFFSET 1000000;

-- 优化后（游标分页）
SELECT * FROM orders
WHERE id < 1000020
ORDER BY id DESC
LIMIT 20;

-- 优化后（基于时间）
SELECT * FROM orders
WHERE created_at < '2024-06-01'
ORDER BY created_at DESC
LIMIT 20;
```

## 配置优化

### 内存配置

```sql
-- 查看当前配置
SHOW shared_buffers;
SHOW work_mem;
SHOW maintenance_work_mem;
SHOW effective_cache_size;

-- 建议配置（16GB 内存）
ALTER SYSTEM SET shared_buffers = '4GB';
ALTER SYSTEM SET work_mem = '64MB';
ALTER SYSTEM SET maintenance_work_mem = '512MB';
ALTER SYSTEM SET effective_cache_size = '12GB';

-- 重新加载配置
SELECT pg_reload_conf();
```

### 写入优化

```sql
-- 批量插入优化
COPY orders (id, user_id, amount, created_at)
FROM '/tmp/orders.csv' WITH (FORMAT csv);

-- 禁用索引加速批量写入
ALTER INDEX idx_orders_user DISABLE;
-- ... 批量写入 ...
ALTER INDEX idx_orders_user ENABLE;

-- 使用 UNLOGGED 表（不需要 WAL）
CREATE UNLOGGED TABLE temp_data (
    id SERIAL PRIMARY KEY,
    data JSONB
);
```

## 监控与诊断

### 慢查询日志

```sql
-- 开启慢查询日志
ALTER SYSTEM SET log_min_duration_statement = 1000;  -- 1秒
ALTER SYSTEM SET log_statement = 'none';
ALTER SYSTEM SET log_line_prefix = '%t [%p]: [%l-1] user=%u,db=%d ';

-- 查看慢查询
SELECT pid, now() - pg_stat_activity.query_start AS duration, query
FROM pg_stat_activity
WHERE (now() - pg_stat_activity.query_start) > interval '5 seconds'
  AND state != 'idle'
ORDER BY duration DESC;
```

### 统计信息

```sql
-- 查看表统计
SELECT * FROM pg_stat_user_tables WHERE relname = 'orders';

-- 查看索引使用情况
SELECT * FROM pg_stat_user_indexes WHERE relname = 'orders';

-- 查看索引扫描统计
SELECT indexrelname, idx_scan, idx_tup_read, idx_tup_fetch
FROM pg_stat_user_indexes
WHERE relname = 'orders';

-- 查看缓存命中率
SELECT
    sum(heap_blks_hit) / (sum(heap_blks_hit) + sum(heap_blks_read)) AS cache_hit_ratio
FROM pg_statio_user_tables;
```

## 分区表

### 范围分区

```sql
CREATE TABLE orders (
    id BIGSERIAL,
    user_id BIGINT NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    PRIMARY KEY (id, created_at)
) PARTITION BY RANGE (created_at);

-- 创建月度分区
CREATE TABLE orders_2024_01 PARTITION OF orders
    FOR VALUES FROM ('2024-01-01') TO ('2024-02-01');

CREATE TABLE orders_2024_02 PARTITION OF orders
    FOR VALUES FROM ('2024-02-01') TO ('2024-03-01');

-- 创建默认分区
CREATE TABLE orders_default PARTITION OF orders DEFAULT;
```

### 列表分区

```sql
CREATE TABLE products (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100),
    category VARCHAR(50),
    price DECIMAL(10,2)
) PARTITION BY LIST (category);

CREATE TABLE products_electronics PARTITION OF products
    FOR VALUES IN ('electronics');

CREATE TABLE products_clothing PARTITION OF products
    FOR VALUES IN ('clothing');
```

## Java 性能实践

```java
import java.sql.*;
import java.util.*;

public class PgPerformanceDemo {

    // 使用批量插入
    public void batchInsert(Connection conn, List<Order> orders) throws SQLException {
        String sql = "INSERT INTO orders (user_id, amount, status, created_at) VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            conn.setAutoCommit(false);  // 关闭自动提交
            for (Order order : orders) {
                stmt.setLong(1, order.getUserId());
                stmt.setDouble(2, order.getAmount());
                stmt.setString(3, order.getStatus());
                stmt.setTimestamp(4, order.getCreatedAt());
                stmt.addBatch();
            }
            stmt.executeBatch();
            conn.commit();  // 提交事务
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);  // 恢复自动提交
        }
    }

    // 使用游标分页
    public List<Order> cursorPaging(Connection conn, long lastId, int limit) throws SQLException {
        String sql = """
            SELECT id, user_id, amount, status, created_at
            FROM orders
            WHERE id < ?
            ORDER BY id DESC
            LIMIT ?
            """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, lastId);
            stmt.setInt(2, limit);
            try (ResultSet rs = stmt.executeQuery()) {
                List<Order> orders = new ArrayList<>();
                while (rs.next()) {
                    orders.add(mapOrder(rs));
                }
                return orders;
            }
        }
    }

    // 优化查询（使用具体列）
    public void optimizeQuery(Connection conn) throws SQLException {
        // 不好的写法：SELECT *
        String badSql = "SELECT * FROM orders WHERE user_id = ?";

        // 好的写法：指定具体列
        String goodSql = """
            SELECT id, user_id, amount, status, created_at
            FROM orders
            WHERE user_id = ?
            """;

        // 使用只读事务
        conn.setReadOnly(true);
    }
}
```

## Java 运行演示

完整的可执行代码请参考：[PgPerformanceDemo.java](../../../../../../src/main/java/com/shuai/database/postgresql/PgPerformanceDemo.java)

### 运行命令

```bash
# 编译项目
mvn compile -q

# 运行 PostgreSQL 性能优化演示
mvn exec:java -Dexec.mainClass="com.shuai.database.postgresql.PgPerformanceDemo"
```

### 演示内容

| 功能 | 说明 |
|------|------|
| 索引创建 | B-Tree/GIN/BRIN/表达式/部分索引 |
| EXPLAIN 分析 | 执行计划分析和优化建议 |
| 批量插入 | Batch 和 COPY 高速导入 |
| 游标分页 | 替代 OFFSET 深分页 |
| 索引统计 | pg_stat 视图查询 |
| 慢查询监控 | 执行时间分析 |
