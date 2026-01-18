# PostgreSQL JSON 支持

## JSON 数据类型

### JSON vs JSONB

| 特性 | JSON | JSONB |
|------|------|-------|
| 存储格式 | 原始文本 | 二进制 |
| 索引支持 | 无 | GIN 索引 |
| 插入速度 | 快 | 稍慢（需解析） |
| 查询速度 | 需解析 | 较快 |
| 保留空白 | 是 | 否 |
| 排序/比较 | 不支持 | 支持 |

### 创建 JSONB 表

```sql
-- 创建 JSONB 表
CREATE TABLE orders (
    id SERIAL PRIMARY KEY,
    customer_id INT NOT NULL,
    order_data JSONB NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 插入 JSONB 数据
INSERT INTO orders (customer_id, order_data) VALUES
(1, '{
    "items": [
        {"product_id": 1, "quantity": 2, "price": 29.99},
        {"product_id": 2, "quantity": 1, "price": 49.99}
    ],
    "shipping": {
        "address": "123 Main St",
        "city": "New York",
        "zip": "10001"
    },
    "payment_method": "credit_card"
}');
```

## JSON 操作符

### 提取操作符

```sql
-- ->：返回 JSON 对象
SELECT order_data -> 'shipping' FROM orders;
-- 返回: {"address": "123 Main St", "city": "New York", "zip": "10001"}

-- ->>：返回文本
SELECT order_data ->> 'payment_method' FROM orders;
-- 返回: "credit_card"

-- -> 获取数组元素
SELECT order_data -> 'items' -> 0 FROM orders;
-- 返回: {"product_id": 1, "quantity": 2, "price": 29.99}

-- ->> 获取数组元素文本
SELECT order_data -> 'items' -> 0 ->> 'product_id' FROM orders;
-- 返回: "1"
```

### 路径操作符

```sql
-- #>：提取嵌套值
SELECT order_data #> '{shipping, city}' FROM orders;
-- 返回: "New York"

-- #>>：提取嵌套值（文本）
SELECT order_data #>> '{shipping, city}' FROM orders;
-- 返回: New York

-- 多级路径
SELECT order_data #> '{items, 0, product_id}' FROM orders;
-- 返回: 1
```

### 存在性操作符

```sql
-- ?：检查键是否存在
SELECT * FROM orders WHERE order_data ? 'discount';

-- ?|：检查任意键是否存在
SELECT * FROM orders WHERE order_data ?| ARRAY['discount', 'coupon'];

-- ?&：检查所有键是否存在
SELECT * FROM orders WHERE order_data ?& ARRAY['shipping', 'payment'];
```

## JSON 函数

### 创建函数

```sql
-- jsonb_build_object：创建 JSONB 对象
SELECT jsonb_build_object(
    'name', 'John',
    'age', 30,
    'email', 'john@example.com'
);
-- 返回: {"name": "John", "age": 30, "email": "john@example.com"}

-- jsonb_build_array：创建 JSONB 数组
SELECT jsonb_build_array(1, 2, 3, 'a', 'b');
-- 返回: [1, 2, 3, "a", "b"]

-- jsonb_object：创建 JSONB 对象（从数组）
SELECT jsonb_object('{a,b,c}', '{1,2,3}');
-- 返回: {"a": 1, "b": 2, "c": 3}

-- to_jsonb：转换值为 JSONB
SELECT to_jsonb(users) FROM users WHERE id = 1;
```

### 查询函数

```sql
-- jsonb_each：展开对象为行
SELECT key, value
FROM orders,
     LATERAL jsonb_each(order_data);

-- jsonb_each_text：展开对象为文本
SELECT key, value::text
FROM orders,
     LATERAL jsonb_each_text(order_data);

-- jsonb_array_elements：展开数组
SELECT item
FROM orders,
     LATERAL jsonb_array_elements(order_data -> 'items') AS item;

-- jsonb_object_keys：获取对象键
SELECT jsonb_object_keys(order_data) AS key
FROM orders WHERE id = 1;
```

### 聚合函数

```sql
-- jsonb_agg：聚合为数组
SELECT customer_id,
       jsonb_agg(item) AS all_items
FROM orders,
     LATERAL jsonb_array_elements(order_data -> 'items') AS item
GROUP BY customer_id;

-- jsonb_object_agg：聚合为对象
SELECT customer_id,
       jsonb_object_agg(item->>'product_id', item) AS items_map
FROM orders,
     LATERAL jsonb_array_elements(order_data -> 'items') AS item
GROUP BY customer_id;
```

## 实际应用场景

### 用户属性存储

```sql
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    profile JSONB DEFAULT '{}'::jsonb,
    preferences JSONB DEFAULT '{}'::jsonb
);

-- 插入
INSERT INTO users (username, profile, preferences) VALUES
('john', '{
    "avatar": "avatar.png",
    "bio": "Software developer"
}'::jsonb, '{
    "theme": "dark",
    "language": "en",
    "notifications": true
}'::jsonb);

-- 查询：获取用户主题偏好
SELECT username,
       profile ->> 'bio' AS bio,
       preferences ->> 'theme' AS theme
FROM users;

-- 更新：修改嵌套值
UPDATE users
SET preferences = preferences || '{"theme": "light"}'::jsonb
WHERE id = 1;

-- 删除键
UPDATE users
SET profile = profile - 'bio'
WHERE id = 1;
```

### 事件日志

```sql
CREATE TABLE events (
    id SERIAL PRIMARY KEY,
    event_type VARCHAR(50) NOT NULL,
    event_data JSONB NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 插入事件
INSERT INTO events (event_type, event_data) VALUES
('user_registered', '{"user_id": 100, "source": "web"}'),
('order_created', '{"order_id": 500, "items_count": 3, "total": 99.99}'),
('payment_completed', '{"order_id": 500, "method": "credit_card", "amount": 99.99}');

-- 查询特定类型事件
SELECT * FROM events
WHERE event_type = 'order_created';

-- 使用 JSON 路径查询（PostgreSQL 12+）
SELECT * FROM events
WHERE event_data @> '{"order_id": 500}';

-- 包含检查
SELECT * FROM events
WHERE event_data ? 'source';

-- 统计事件类型
SELECT event_type, COUNT(*) as count
FROM events
GROUP BY event_type;
```

### 商品规格动态属性

```sql
CREATE TABLE products (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    base_info JSONB NOT NULL,
    specs JSONB DEFAULT '{}'::jsonb
);

-- 插入不同类别的商品
INSERT INTO products (name, base_info, specs) VALUES
('iPhone 15', '{"brand": "Apple"}'::jsonb, '{
    "color": ["blue", "black", "white"],
    "storage": ["128GB", "256GB", "512GB"],
    "weight": 171
}'::jsonb),

('Laptop X1', '{"brand": "Lenovo"}'::jsonb, '{
    "cpu": ["i5", "i7"],
    "ram": ["8GB", "16GB", "32GB"],
    "weight": 1400
}'::jsonb);

-- 查询包含特定规格的产品
SELECT * FROM products
WHERE specs @> '{"color": ["blue"]}';

-- 查询所有支持 i7 CPU 的产品
SELECT * FROM products
WHERE specs -> 'cpu' @> '"i7"';
```

## JSON 索引

### GIN 索引

```sql
-- 创建 GIN 索引
CREATE INDEX idx_orders_data ON orders USING GIN (order_data);

-- 创建表达式索引
CREATE INDEX idx_orders_shipping_city ON orders
USING GIN ((order_data #>> '{shipping,city}'::text[]));

-- 创建路径索引
CREATE INDEX idx_orders_customer ON orders
USING GIN (order_data jsonb_path_ops);
```

### 索引类型选择

```sql
-- jsonb_path_ops：适用于 @> 操作符
CREATE INDEX idx_path ON orders USING GIN (order_data jsonb_path_ops);

-- jsonb_ops：适用于 ?, ?|, ?&, @>, @?, @@ 操作符
CREATE INDEX idx_ops ON orders USING GIN (order_data jsonb_ops);
```

## Java 实现

```java
import org.postgresql.util.PGobject;
import java.sql.*;
import java.util.*;

public class JsonbDemo {

    // 插入 JSONB 数据
    public void insertJsonb(Connection conn) throws SQLException {
        String sql = "INSERT INTO orders (customer_id, order_data) VALUES (?, ?::jsonb)";

        Map<String, Object> orderData = new LinkedHashMap<>();
        List<Map<String, Object>> items = new ArrayList<>();

        Map<String, Object> item1 = new LinkedHashMap<>();
        item1.put("product_id", 1);
        item1.put("quantity", 2);
        item1.put("price", 29.99);
        items.add(item1);

        Map<String, Object> item2 = new LinkedHashMap<>();
        item2.put("product_id", 2);
        item2.put("quantity", 1);
        item2.put("price", 49.99);
        items.add(item2);

        orderData.put("items", items);

        Map<String, Object> shipping = new LinkedHashMap<>();
        shipping.put("address", "123 Main St");
        shipping.put("city", "New York");
        shipping.put("zip", "10001");
        orderData.put("shipping", shipping);
        orderData.put("payment_method", "credit_card");

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, 1);
            stmt.setObject(2, orderData); // JDBC 会自动转换
            stmt.executeUpdate();
        }
    }

    // 查询 JSONB 数据
    public void queryJsonb(Connection conn) throws SQLException {
        String sql = """
            SELECT id, order_data,
                   order_data ->> 'payment_method' AS payment_method,
                   order_data #> '{shipping,city}' AS city
            FROM orders
            WHERE order_data @> ?::jsonb
            """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "{\"items\": [{\"product_id\": 1}]}");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    System.out.println("Order ID: " + rs.getInt("id"));
                    System.out.println("Payment: " + rs.getString("payment_method"));
                }
            }
        }
    }

    // 更新 JSONB 数据
    public void updateJsonb(Connection conn) throws SQLException {
        String sql = """
            UPDATE orders
            SET order_data = order_data || ?::jsonb
            WHERE id = ?
            """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "{\"status\": \"shipped\"}");
            stmt.setInt(2, 1);
            stmt.executeUpdate();
        }
    }

    // 查询嵌套数组
    public void queryArray(Connection conn) throws SQLException {
        String sql = """
            SELECT id, item
            FROM orders,
                 LATERAL jsonb_array_elements(order_data -> 'items') AS item
            WHERE (item ->> 'product_id')::int = ?
            """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, 1);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    System.out.println("Order ID: " + rs.getInt("id"));
                    System.out.println("Item: " + rs.getString("item"));
                }
            }
        }
    }
}
```
