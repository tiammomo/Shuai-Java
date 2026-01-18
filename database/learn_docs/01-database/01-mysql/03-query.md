# 查询与高级查询

## 基本查询

```sql
-- 查询所有列
SELECT * FROM users;

-- 查询指定列
SELECT id, username, email FROM users;

-- 带条件查询
SELECT * FROM users WHERE age >= 25 AND status = 1;

-- 带排序
SELECT * FROM users ORDER BY created_at DESC;

-- 带分页
SELECT * FROM users ORDER BY id LIMIT 10 OFFSET 0;
-- 或使用 LIMIT 的简化形式
SELECT * FROM users ORDER BY id LIMIT 0, 10;
```

## 连接查询

### 内连接 (INNER JOIN)

```sql
-- 等值连接
SELECT u.username, o.order_no, o.total_amount
FROM users u
INNER JOIN orders o ON u.id = o.user_id;

-- 多表连接
SELECT u.username, o.order_no, p.name, od.quantity
FROM users u
INNER JOIN orders o ON u.id = o.user_id
INNER JOIN order_details od ON o.id = od.order_id
INNER JOIN products p ON od.product_id = p.id;
```

### 左连接 (LEFT JOIN)

```sql
-- 包含左表所有记录
SELECT u.username, o.order_no, o.total_amount
FROM users u
LEFT JOIN orders o ON u.id = o.user_id
WHERE u.status = 1;
```

### 右连接 (RIGHT JOIN)

```sql
-- 包含右表所有记录
SELECT u.username, o.order_no, o.total_amount
FROM users u
RIGHT JOIN orders o ON u.id = o.user_id;
```

### 自连接

```sql
-- 查询用户的直接上级
SELECT e.name as employee, m.name as manager
FROM employees e
LEFT JOIN employees m ON e.manager_id = m.id;
```

## 聚合查询

### 聚合函数

| 函数 | 说明 | 示例 |
|------|------|------|
| COUNT() | 计数 | COUNT(*) |
| SUM() | 求和 | SUM(amount) |
| AVG() | 平均值 | AVG(age) |
| MAX() | 最大值 | MAX(score) |
| MIN() | 最小值 | MIN(price) |

### GROUP BY

```sql
-- 按状态分组统计
SELECT status, COUNT(*) as count, AVG(age) as avg_age
FROM users
GROUP BY status
HAVING count > 10;

-- 多列分组
SELECT status, DATE(created_at) as day, COUNT(*) as count
FROM users
GROUP BY status, DATE(created_at);
```

### 多表聚合

```sql
-- 统计用户订单金额
SELECT
    u.id,
    u.username,
    COUNT(o.id) as order_count,
    COALESCE(SUM(o.total_amount), 0) as total_spent
FROM users u
LEFT JOIN orders o ON u.id = o.user_id
GROUP BY u.id, u.username
ORDER BY total_spent DESC;
```

## 子查询

### 标量子查询

```sql
-- 返回单个值
SELECT * FROM users
WHERE age > (SELECT AVG(age) FROM users);

SELECT username, (SELECT MAX(total_amount) FROM orders WHERE user_id = users.id) as max_order
FROM users;
```

### 行子查询

```sql
-- 返回一行
SELECT * FROM products
WHERE (price, stock) = (SELECT price, stock FROM products WHERE id = 1);
```

### 表子查询

```sql
-- 返回多行多列
SELECT * FROM users
WHERE id IN (SELECT user_id FROM orders WHERE total_amount > 1000);

-- 使用 EXISTS
SELECT * FROM users u
WHERE EXISTS (
    SELECT 1 FROM orders o
    WHERE o.user_id = u.id AND o.status = 1
);
```

### 关联子查询

```sql
-- 查询每个用户的最新订单
SELECT * FROM orders o1
WHERE o1.created_at = (
    SELECT MAX(created_at)
    FROM orders o2
    WHERE o2.user_id = o1.user_id
);

-- 使用窗口函数（MySQL 8.0+）
SELECT * FROM (
    SELECT *,
           ROW_NUMBER() OVER (PARTITION BY user_id ORDER BY created_at DESC) as rn
    FROM orders
) t WHERE rn = 1;
```

## CASE 表达式

```sql
-- 简单 CASE
SELECT
    username,
    CASE status
        WHEN 0 THEN '禁用'
        WHEN 1 THEN '启用'
        WHEN 2 THEN 'VIP'
        ELSE '未知'
    END as status_text
FROM users;

-- 搜索 CASE
SELECT
    username,
    age,
    CASE
        WHEN age < 18 THEN '未成年'
        WHEN age BETWEEN 18 AND 35 THEN '青年'
        WHEN age BETWEEN 36 AND 60 THEN '中年'
        ELSE '老年'
    END as age_group
FROM users;
```

## 高级查询技巧

### 分页查询

```sql
-- 方式1: LIMIT OFFSET（大数据量不推荐）
SELECT * FROM users ORDER BY id LIMIT 20 OFFSET 40;

-- 方式2: 利用上一页最后 ID（推荐）
SELECT * FROM users WHERE id > 20 ORDER BY id LIMIT 20;

-- 方式3: 窗口函数分页（MySQL 8.0+）
SELECT * FROM (
    SELECT *, CEIL(ROW_NUMBER() OVER (ORDER BY id) / 10) as page
    FROM users
) t WHERE page = 3;
```

### 递归查询（MySQL 8.0+）

```sql
-- 递归 CTE 查询组织结构
WITH RECURSIVE org_tree AS (
    -- 基础查询
    SELECT id, name, parent_id, 1 as level
    FROM organizations
    WHERE parent_id IS NULL

    UNION ALL

    -- 递归查询
    SELECT o.id, o.name, o.parent_id, ot.level + 1
    FROM organizations o
    INNER JOIN org_tree ot ON o.parent_id = ot.id
)
SELECT * FROM org_tree ORDER BY level, id;
```

### 随机查询

```sql
-- 随机获取一条
SELECT * FROM users ORDER BY RAND() LIMIT 1;

-- 随机获取 N 条（大数据量不推荐）
SELECT * FROM users ORDER BY RAND() LIMIT 10;

-- 高性能随机采样
SELECT * FROM users
WHERE id >= (SELECT FLOOR(RAND() * (SELECT MAX(id) FROM users)))
ORDER BY id LIMIT 10;
```

### 统计报表

```sql
-- 日报统计
SELECT
    DATE(created_at) as date,
    COUNT(*) as user_count,
    COUNT(DISTINCT user_id) as order_user_count
FROM orders
WHERE created_at >= DATE_SUB(CURDATE(), INTERVAL 30 DAY)
GROUP BY DATE(created_at)
ORDER BY date;

-- 交叉表统计
SELECT
    DATE(created_at) as date,
    COUNT(CASE WHEN status = 0 THEN 1 END) as pending,
    COUNT(CASE WHEN status = 1 THEN 1 END) as paid,
    COUNT(CASE WHEN status = 2 THEN 1 END) as shipped,
    COUNT(CASE WHEN status = 3 THEN 1 END) as completed
FROM orders
GROUP BY DATE(created_at);
```

## Java 实现

### 基本查询

```java
// 查询单条
String sql = "SELECT * FROM users WHERE id = ?";
try (PreparedStatement stmt = conn.prepareStatement(sql)) {
    stmt.setLong(1, 1);
    try (ResultSet rs = stmt.executeQuery()) {
        if (rs.next()) {
            User user = new User();
            user.setId(rs.getLong("id"));
            user.setUsername(rs.getString("username"));
            user.setEmail(rs.getString("email"));
        }
    }
}

// 查询多条
String sql = "SELECT * FROM users WHERE status = ?";
try (PreparedStatement stmt = conn.prepareStatement(sql)) {
    stmt.setInt(1, 1);
    try (ResultSet rs = stmt.executeQuery()) {
        List<User> users = new ArrayList<>();
        while (rs.next()) {
            User user = new User();
            user.setId(rs.getLong("id"));
            user.setUsername(rs.getString("username"));
            users.add(user);
        }
    }
}
```

### 分页查询

```java
// 简单分页
int page = 1;
int size = 10;
int offset = (page - 1) * size;

String sql = "SELECT * FROM users ORDER BY id LIMIT ? OFFSET ?";
try (PreparedStatement stmt = conn.prepareStatement(sql)) {
    stmt.setInt(1, size);
    stmt.setInt(2, offset);
    try (ResultSet rs = stmt.executeQuery()) {
        // 处理结果
    }
}

// 游标分页（高性能）
String sql = "SELECT * FROM users WHERE id > ? ORDER BY id LIMIT ?";
try (PreparedStatement stmt = conn.prepareStatement(sql)) {
    stmt.setLong(1, lastId);
    stmt.setInt(2, size);
    try (ResultSet rs = stmt.executeQuery()) {
        // 处理结果
    }
}
```

### 聚合查询

```java
String sql = "SELECT status, COUNT(*) as count, AVG(age) as avg_age FROM users GROUP BY status";
try (PreparedStatement stmt = conn.prepareStatement(sql);
     ResultSet rs = stmt.executeQuery()) {
    while (rs.next()) {
        int status = rs.getInt("status");
        long count = rs.getLong("count");
        double avgAge = rs.getDouble("avg_age");
    }
}
```
