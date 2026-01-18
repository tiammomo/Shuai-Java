# 索引与性能优化

## 索引基础

### 索引类型

| 索引类型 | 说明 | 使用场景 |
|----------|------|----------|
| PRIMARY KEY | 主键索引，唯一且非空 | 唯一标识 |
| UNIQUE | 唯一索引 | 唯一性约束 |
| INDEX/B-TREE | 普通索引 | 加速查询 |
| FULLTEXT | 全文索引 | 文本搜索（MyISAM/InnoDB） |
| SPATIAL | 空间索引 | 地理数据 |
| HASH | 哈希索引 | 精确匹配（Memory 引擎） |

### B+Tree 索引原理

```
B+Tree 结构
        [50, 100]
       /    |    \
  [10,30] [50,60,70] [100,120,150]
    |       |         |
  ...      ...       ...
```

**特点**：
- 所有数据存储在叶子节点
- 叶子节点有序排列
- 适合范围查询
- 高度平衡，查询稳定

## 索引设计

### 创建索引

```sql
-- 单列索引
CREATE INDEX idx_user_name ON users(name);
CREATE INDEX idx_user_status ON users(status);

-- 复合索引
CREATE INDEX idx_user_status_created ON users(status, created_at DESC);

-- 唯一索引
CREATE UNIQUE INDEX uk_user_email ON users(email);

-- 全文索引（MySQL 5.7+）
CREATE FULLTEXT INDEX ft_idx_content ON articles(title, content);

-- 前缀索引
CREATE INDEX idx_user_name_prefix ON users(name(10));
```

### 索引设计原则

1. **选择区分度高的列**
   ```sql
   -- 好的索引：状态有多种值
   CREATE INDEX idx_status ON orders(status);

   -- 不好的索引：性别只有两种值
   CREATE INDEX idx_gender ON users(gender);
   ```

2. **复合索引列顺序**
   - 区分度高的列放在前面
   - 常用于 WHERE 条件的列放在前面
   - 排序字段放在后面

   ```sql
   -- 查询：WHERE status = ? ORDER BY created_at DESC
   -- 好的索引
   CREATE INDEX idx_status_created ON orders(status, created_at DESC);
   ```

3. **避免过多索引**
   - 每个索引都会占用磁盘空间
   - 写入操作需要维护所有索引
   - 一般不超过 5-6 个索引

### 索引失效场景

```sql
-- 1. 使用函数
SELECT * FROM users WHERE YEAR(created_at) = 2024;  -- 失效

-- 2. 隐式类型转换
SELECT * FROM users WHERE phone = 13800138000;  -- phone 是字符串

-- 3. LIKE 以通配符开头
SELECT * FROM users WHERE name LIKE '%zhang';  -- 失效

-- 4. OR 前后有非索引列
SELECT * FROM users WHERE name = 'zhangsan' OR age = 25;  -- age 无索引

-- 5. 范围查询后面的列
SELECT * FROM orders WHERE status > 0 AND created_at > '2024-01-01';
-- 如果索引是 (status, created_at)，created_at 索引失效

-- 6. 使用 IS NULL
SELECT * FROM users WHERE deleted_at IS NULL;  -- 失效（除非是稀疏数据）
```

## SQL 优化

### EXPLAIN 分析

```sql
EXPLAIN SELECT * FROM users WHERE id = 1;

-- 结果字段说明
+----+-------------+-------+-------+---------------+------+---------+------+------+-------------+
| id | select_type| table | type  | possible_keys | key  | key_len | ref  | rows | Extra       |
+----+-------------+-------+-------+---------------+------+---------+------+------+-------------+

-- type 字段（从好到差）
system > const > eq_ref > ref > range > index > ALL
```

### 优化示例

```sql
-- 优化前
SELECT * FROM orders
WHERE YEAR(created_at) = 2024
  AND MONTH(created_at) = 1;

-- 优化后（使用范围索引）
SELECT * FROM orders
WHERE created_at >= '2024-01-01'
  AND created_at < '2024-02-01';

-- 优化前（深分页）
SELECT * FROM orders ORDER BY id DESC LIMIT 1000000, 20;

-- 优化后（延迟关联）
SELECT o.* FROM orders o
INNER JOIN (
    SELECT id FROM orders ORDER BY id DESC LIMIT 1000000, 20
) t ON o.id = t.id;
```

### 慢查询优化

```sql
-- 开启慢查询日志
SET GLOBAL slow_query_log = ON;
SET GLOBAL long_query_time = 2;
SET GLOBAL log_queries_not_using_indexes = ON;

-- 查看慢查询
SELECT * FROM mysql.slow_log
ORDER BY start_time DESC
LIMIT 100;

-- 使用 EXPLAIN 分析
EXPLAIN
SELECT * FROM orders
WHERE user_id IN (SELECT id FROM users WHERE status = 0);

-- 优化建议：改写为 JOIN
EXPLAIN
SELECT o.* FROM orders o
INNER JOIN users u ON o.user_id = u.id
WHERE u.status = 0;
```

## 表结构优化

### 数据类型优化

```sql
-- 使用合适的数据类型
-- 不好的设计
CREATE TABLE orders (
    amount VARCHAR(20),  -- 金额用字符串
    status VARCHAR(10)    -- 状态用字符串
);

-- 好的设计
CREATE TABLE orders (
    amount DECIMAL(10,2),
    status TINYINT  -- 0-未支付,1-已支付,2-已发货
);

-- 使用 ENUM 替代字符串
CREATE TABLE orders (
    status ENUM('pending', 'paid', 'shipped', 'completed') DEFAULT 'pending'
);

-- 使用 DATETIME 而非 TIMESTAMP（DATETIME 范围更大）
created_at DATETIME DEFAULT CURRENT_TIMESTAMP
```

### 分表策略

```sql
-- 按时间分表
CREATE TABLE orders_202401 (
    id BIGINT NOT NULL,
    user_id INT NOT NULL,
    amount DECIMAL(10,2),
    created_at DATETIME,
    PRIMARY KEY (id),
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB;

-- 使用 MySQL 分区
CREATE TABLE orders (
    id BIGINT NOT NULL,
    user_id INT NOT NULL,
    amount DECIMAL(10,2),
    created_at DATETIME NOT NULL,
    PRIMARY KEY (id, created_at)
) PARTITION BY RANGE (YEAR(created_at)) (
    PARTITION p2023 VALUES LESS THAN (2024),
    PARTITION p2024 VALUES LESS THAN (2025),
    PARTITION pmax VALUES LESS THAN MAXVALUE
);
```

## Java 优化实践

### 分批处理

```java
// 批量插入
public void batchInsert(List<User> users, int batchSize) {
    String sql = "INSERT INTO users (username, email, age) VALUES (?, ?, ?)";
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getEmail());
            stmt.setInt(3, user.getAge());
            stmt.addBatch();

            if ((i + 1) % batchSize == 0) {
                stmt.executeBatch();
            }
        }
        stmt.executeBatch();
    }
}

// 分批查询
public List<User> findAllByPage(int page, int size) {
    String sql = "SELECT * FROM users ORDER BY id LIMIT ? OFFSET ?";
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setInt(1, size);
        stmt.setInt(2, (page - 1) * size);
        return mapUsers(stmt.executeQuery());
    }
}
```

### 连接池配置

```java
// HikariCP 配置
HikariConfig config = new HikariConfig();
config.setJdbcUrl("jdbc:mysql://localhost:3306/test");
config.setUsername("ubuntu");
config.setPassword("ubuntu");

// 连接池大小
config.setMaximumPoolSize(20);
config.setMinimumIdle(5);

// 连接超时
config.setConnectionTimeout(30000);
config.setIdleTimeout(600000);
config.setMaxLifetime(1800000);

// 查询超时
config.addDataSourceProperty("socketTimeout", 30000);
```

### 缓存使用

```java
// 使用 Redis 缓存热点数据
public User getUserById(Long id) {
    // 1. 查询缓存
    String cacheKey = "user:" + id;
    User cached = redisTemplate.opsForValue().get(cacheKey);
    if (cached != null) {
        return cached;
    }

    // 2. 查询数据库
    User user = userDao.findById(id);

    // 3. 写入缓存
    if (user != null) {
        redisTemplate.opsForValue().set(cacheKey, user, 1, TimeUnit.HOURS);
    }

    return user;
}

// 更新时删除缓存
public void updateUser(User user) {
    userDao.update(user);
    redisTemplate.delete("user:" + user.getId());
}
```

## 性能监控

```sql
-- 查看执行计划缓存
SELECT * FROM information_schema.sql_plan_cache;

-- 查看线程状态
SHOW STATUS LIKE 'Threads%';
SHOW PROCESSLIST;

-- 查看表统计
SHOW TABLE STATUS LIKE 'users';

-- 查看索引统计
SHOW INDEX FROM users;

-- 分析表
ANALYZE TABLE users;

-- 优化表
OPTIMIZE TABLE users;
```

## 性能指标

| 指标 | 优秀 | 良好 | 需优化 |
|------|------|------|--------|
| QPS | >1000 | 100-1000 | <100 |
| 慢查询比例 | <0.1% | 0.1%-1% | >1% |
| 索引命中率 | >99% | 95%-99% | <95% |
| 锁等待时间 | <1ms | 1-10ms | >10ms |
