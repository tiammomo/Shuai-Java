# DDL 与 DML 操作

## 数据类型

### 数值类型

| 类型 | 范围 | 用途 |
|------|------|------|
| TINYINT | -128 ~ 127 | 小整数 |
| SMALLINT | -32768 ~ 32767 | 小整数 |
| MEDIUMINT | -8388608 ~ 8388607 | 中整数 |
| INT | -2147483648 ~ 2147483647 | 整数 |
| BIGINT | -9223372036854775808 ~ 9223372036854775807 | 大整数 |
| FLOAT | 单精度浮点 | 小数 |
| DOUBLE | 双精度浮点 | 大小数 |
| DECIMAL | 精确数值 | 货币 |

### 字符串类型

| 类型 | 长度 | 用途 |
|------|------|------|
| CHAR | 0-255 | 定长字符串 |
| VARCHAR | 0-65535 | 变长字符串 |
| TINYTEXT | 0-255 | 短文本 |
| TEXT | 0-65535 | 长文本 |
| MEDIUMTEXT | 0-16777215 | 中长文本 |
| LONGTEXT | 0-4294967295 | 长文本 |
| BLOB | 二进制 | 二进制数据 |

### 日期时间类型

| 类型 | 范围 | 格式 |
|------|------|------|
| DATE | 1000-01-01 ~ 9999-12-31 | YYYY-MM-DD |
| TIME | -838:59:59 ~ 838:59:59 | HH:MM:SS |
| DATETIME | 1000-01-01 00:00:00 ~ 9999-12-31 23:59:59 | YYYY-MM-DD HH:MM:SS |
| TIMESTAMP | 1970-01-01 00:00:01 ~ 2038-01-19 03:14:07 | 时间戳 |
| YEAR | 1901 ~ 2155 | 年份 |

## DDL 操作

### 创建表

```sql
-- 基础创建
CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL,
    password CHAR(64) NOT NULL,
    age TINYINT DEFAULT 18,
    status TINYINT DEFAULT 1,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 带约束的创建
CREATE TABLE orders (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    order_no VARCHAR(32) NOT NULL,
    amount DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    status TINYINT DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_orders_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT uk_order_no UNIQUE (order_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 复制表结构
CREATE TABLE users_copy LIKE users;

-- 从查询结果创建
CREATE TABLE vip_users AS SELECT * FROM users WHERE status = 2;
```

### 修改表

```sql
-- 添加列
ALTER TABLE users ADD COLUMN phone VARCHAR(20) DEFAULT NULL;
ALTER TABLE users ADD COLUMN last_login DATETIME AFTER updated_at;

-- 修改列
ALTER TABLE users MODIFY COLUMN age SMALLINT DEFAULT 18;
ALTER TABLE users CHANGE COLUMN phone mobile VARCHAR(20);

-- 删除列
ALTER TABLE users DROP COLUMN mobile;

-- 添加约束
ALTER TABLE orders ADD CONSTRAINT fk_orders_user FOREIGN KEY (user_id) REFERENCES users(id);
ALTER TABLE orders ADD INDEX idx_user_id (user_id);

-- 删除约束
ALTER TABLE orders DROP FOREIGN KEY fk_orders_user;
ALTER TABLE orders DROP INDEX idx_user_id;

-- 重命名表
RENAME TABLE users TO tb_users;
ALTER TABLE users RENAME TO tb_users;
```

### 删除表

```sql
-- 删除表数据（保留结构）
TRUNCATE TABLE users;

-- 删除表
DROP TABLE IF EXISTS users;
```

### 索引操作

```sql
-- 创建索引
CREATE INDEX idx_username ON users(username);
CREATE UNIQUE INDEX uk_email ON users(email);
CREATE INDEX idx_status_created ON users(status, created_at DESC);

-- 查看索引
SHOW INDEX FROM users;

-- 删除索引
DROP INDEX idx_username ON users;
ALTER TABLE users DROP INDEX uk_email;

-- 分析表
ANALYZE TABLE users;

-- 优化表
OPTIMIZE TABLE users;
```

## DML 操作

### 插入数据

```sql
-- 单条插入
INSERT INTO users (username, email, password, age)
VALUES ('zhangsan', 'zhangsan@example.com', '123456', 25);

-- 多条插入
INSERT INTO users (username, email, password, age) VALUES
('lisi', 'lisi@example.com', '123456', 30),
('wangwu', 'wangwu@example.com', '123456', 28);

-- 插入或更新（ON DUPLICATE KEY UPDATE）
INSERT INTO users (id, username, email, age) VALUES (1, 'zhangsan', 'zs@example.com', 26)
ON DUPLICATE KEY UPDATE email = 'zs@example.com', age = 26;

-- 插入忽略（IGNORE）
INSERT IGNORE INTO users (username, email) VALUES ('zhangsan', 'zs@example.com');

-- 从查询结果插入
INSERT INTO users_backup SELECT * FROM users WHERE status = 1;

-- SET 方式插入
INSERT INTO users SET username = 'zhaoliu', email = 'zl@example.com', age = 35;
```

### 更新数据

```sql
-- 基本更新
UPDATE users SET age = 26 WHERE id = 1;

-- 多列更新
UPDATE users SET
    email = 'new@example.com',
    age = 30,
    updated_at = NOW()
WHERE id = 1;

-- 批量更新
UPDATE users SET age = age + 1 WHERE status = 1;

-- 限制更新行数
UPDATE users SET status = 0 ORDER BY created_at ASC LIMIT 10;

-- 使用子查询更新
UPDATE orders SET amount = (
    SELECT SUM(price * quantity) FROM order_items WHERE order_id = orders.id
) WHERE status = 0;
```

### 删除数据

```sql
-- 基本删除
DELETE FROM users WHERE id = 1;

-- 批量删除
DELETE FROM users WHERE status = 0 AND created_at < DATE_SUB(NOW(), INTERVAL 1 YEAR);

-- 限制删除行数
DELETE FROM users ORDER BY created_at ASC LIMIT 100;

-- 清空表
TRUNCATE TABLE users;

-- 使用子查询删除
DELETE FROM orders WHERE user_id IN (SELECT id FROM users WHERE status = 0);
```

## Java 实现

### 插入数据

```java
// 单条插入
String sql = "INSERT INTO users (username, email, password, age) VALUES (?, ?, ?, ?)";
try (PreparedStatement stmt = conn.prepareStatement(sql)) {
    stmt.setString(1, "zhangsan");
    stmt.setString(2, "zs@example.com");
    stmt.setString(3, "123456");
    stmt.setInt(4, 25);
    int rows = stmt.executeUpdate();
    System.out.println("插入行数: " + rows);
}

// 获取自增 ID
long userId = stmt.getGeneratedKeys().getLong(1);

// 批量插入
String sql = "INSERT INTO users (username, email, password, age) VALUES (?, ?, ?, ?)";
try (PreparedStatement stmt = conn.prepareStatement(sql)) {
    for (User user : users) {
        stmt.setString(1, user.getUsername());
        stmt.setString(2, user.getEmail());
        stmt.setString(3, user.getPassword());
        stmt.setInt(4, user.getAge());
        stmt.addBatch();
    }
    int[] results = stmt.executeBatch();
    System.out.println("批量插入成功");
}
```

### 更新数据

```java
String sql = "UPDATE users SET age = ?, updated_at = NOW() WHERE id = ?";
try (PreparedStatement stmt = conn.prepareStatement(sql)) {
    stmt.setInt(1, 26);
    stmt.setLong(2, 1);
    int rows = stmt.executeUpdate();
    System.out.println("更新行数: " + rows);
}
```

### 删除数据

```java
String sql = "DELETE FROM users WHERE id = ?";
try (PreparedStatement stmt = conn.prepareStatement(sql)) {
    stmt.setLong(1, 1);
    int rows = stmt.executeUpdate();
    System.out.println("删除行数: " + rows);
}
```

## 初始化数据示例

```sql
-- 用户表
CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL,
    password CHAR(64) NOT NULL,
    age TINYINT DEFAULT 18,
    status TINYINT DEFAULT 1,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 产品表
CREATE TABLE products (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    stock INT DEFAULT 0,
    status TINYINT DEFAULT 1,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 订单表
CREATE TABLE orders (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_no VARCHAR(32) NOT NULL UNIQUE,
    user_id INT NOT NULL,
    total_amount DECIMAL(10,2) DEFAULT 0.00,
    status TINYINT DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- 插入测试数据
INSERT INTO users (username, email, password, age) VALUES
('zhangsan', 'zhangsan@example.com', SHA2('password123', 256), 25),
('lisi', 'lisi@example.com', SHA2('password123', 256), 30),
('wangwu', 'wangwu@example.com', SHA2('password123', 256), 28);

INSERT INTO products (name, price, stock) VALUES
('iPhone 15', 5999.00, 100),
('MacBook Pro', 14999.00, 50),
('AirPods Pro', 1899.00, 200);

INSERT INTO orders (order_no, user_id, total_amount, status) VALUES
('ORDER202401170001', 1, 7898.00, 1),
('ORDER202401170002', 2, 1899.00, 0),
('ORDER202401170003', 1, 5999.00, 1);
```
