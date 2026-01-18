# PostgreSQL 高级查询

## CTE（公用表表达式）

### 基本 CTE

```sql
-- 简单 CTE
WITH regional_sales AS (
    SELECT region, SUM(amount) AS total_sales
    FROM orders
    GROUP BY region
)
SELECT region, total_sales
FROM regional_sales
WHERE total_sales > 100000;

-- 多 CTE
WITH
    regional_sales AS (
        SELECT region, SUM(amount) AS total_sales
        FROM orders GROUP BY region
    ),
    top_regions AS (
        SELECT region
        FROM regional_sales
        WHERE total_sales > 100000
)
SELECT region, product, SUM(quantity) AS product_units
FROM orders
WHERE region IN (SELECT region FROM top_regions)
GROUP BY region, product;
```

### 递归 CTE

```sql
-- 递归查询组织结构
WITH RECURSIVE org_tree AS (
    -- 基础成员（没有上级的员工）
    SELECT id, name, manager_id, 1 AS level
    FROM employees
    WHERE manager_id IS NULL

    UNION ALL

    -- 递归成员
    SELECT e.id, e.name, e.manager_id, ot.level + 1
    FROM employees e
    INNER JOIN org_tree ot ON e.manager_id = ot.id
)
SELECT * FROM org_tree ORDER BY level, id;

-- 递归查询目录树
WITH RECURSIVE path_tree AS (
    SELECT id, parent_id, name, name AS full_path, 1 AS level
    FROM categories
    WHERE parent_id IS NULL

    UNION ALL

    SELECT c.id, c.parent_id, c.name,
           pt.full_path || ' > ' || c.name,
           pt.level + 1
    FROM categories c
    INNER JOIN path_tree pt ON c.parent_id = pt.id
)
SELECT * FROM path_tree;

-- 递归查询：生成序列
WITH RECURSIVE series(n) AS (
    SELECT 1
    UNION ALL
    SELECT n + 1 FROM series WHERE n < 10
)
SELECT * FROM series;
```

## 窗口函数

### 基本窗口函数

```sql
-- ROW_NUMBER：行号
SELECT
    product_name,
    price,
    ROW_NUMBER() OVER (ORDER BY price DESC) AS rank
FROM products;

-- RANK：排名（有间隔）
SELECT
    product_name,
    price,
    RANK() OVER (ORDER BY price DESC) AS rank
FROM products;

-- DENSE_RANK：密集排名
SELECT
    product_name,
    price,
    DENSE_RANK() OVER (ORDER BY price DESC) AS rank
FROM products;

-- NTILE：分桶
SELECT
    product_name,
    price,
    NTILE(4) OVER (ORDER BY price) AS quartile
FROM products;
```

### 窗口函数与分区

```sql
-- 按类别分组排名
SELECT
    category_name,
    product_name,
    price,
    ROW_NUMBER() OVER (
        PARTITION BY category_name
        ORDER BY price DESC
    ) AS rank_in_category
FROM products
JOIN categories ON products.category_id = categories.id;

-- 累计计算
SELECT
    order_date,
    amount,
    SUM(amount) OVER (
        ORDER BY order_date
        ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW
    ) AS cumulative_amount
FROM orders;

-- 移动平均
SELECT
    order_date,
    amount,
    AVG(amount) OVER (
        ORDER BY order_date
        ROWS BETWEEN 6 PRECEDING AND CURRENT ROW
    ) AS moving_avg_7days
FROM daily_sales;
```

### 高级窗口函数

```sql
-- LAG：获取前一行
SELECT
    order_date,
    amount,
    LAG(amount, 1) OVER (ORDER BY order_date) AS prev_amount,
    amount - LAG(amount, 1) OVER (ORDER BY order_date) AS diff
FROM daily_sales;

-- LEAD：获取后一行
SELECT
    order_date,
    amount,
    LEAD(amount, 1) OVER (ORDER BY order_date) AS next_amount
FROM daily_sales;

-- FIRST_VALUE：第一值
SELECT
    product_name,
    price,
    FIRST_VALUE(price) OVER (ORDER BY price) AS min_price,
    FIRST_VALUE(product_name) OVER (ORDER BY price) AS cheapest_product
FROM products;

-- LAST_VALUE：最后值
SELECT
    product_name,
    price,
    LAST_VALUE(price) OVER (
        ORDER BY price
        ROWS BETWEEN UNBOUNDED PRECEDING AND UNBOUNDED FOLLOWING
    ) AS max_price
FROM products;

-- NTH_VALUE：第 N 值
SELECT
    product_name,
    price,
    NTH_VALUE(price, 3) OVER (
        ORDER BY price DESC
        ROWS BETWEEN UNBOUNDED PRECEDING AND UNBOUNDED FOLLOWING
    ) AS third_highest
FROM products;
```

## 子查询

### 标量子查询

```sql
-- 返回单个值
SELECT
    product_name,
    price,
    (SELECT AVG(price) FROM products) AS avg_price
FROM products;

-- 用于 WHERE
SELECT *
FROM products
WHERE price > (
    SELECT AVG(price) FROM products WHERE category_id = 1
);
```

### EXISTS 子查询

```sql
-- 使用 EXISTS
SELECT *
FROM customers c
WHERE EXISTS (
    SELECT 1 FROM orders o
    WHERE o.customer_id = c.id
    AND o.created_at > CURRENT_DATE - INTERVAL '30 days'
);

-- 使用 NOT EXISTS
SELECT *
FROM customers c
WHERE NOT EXISTS (
    SELECT 1 FROM orders o
    WHERE o.customer_id = c.id
);
```

### ARRAY 子查询

```sql
-- 返回数组
SELECT
    id,
    (SELECT ARRAY_AGG(product_id) FROM order_items WHERE order_id = orders.id) AS product_ids
FROM orders;

-- 使用 ANY
SELECT *
FROM products
WHERE id = ANY (ARRAY[1, 3, 5, 7]);

-- 使用 ALL
SELECT *
FROM products
WHERE price > ALL (SELECT price FROM products WHERE category_id = 1);
```

## 高级连接

### FULL OUTER JOIN

```sql
-- 完全外连接
SELECT
    c.id,
    c.name,
    o.id AS order_id,
    o.amount
FROM customers c
FULL OUTER JOIN orders o ON c.id = o.customer_id;
```

### SELF JOIN

```sql
-- 自连接：找出同部门的员工
SELECT
    e1.name AS employee,
    e2.name AS colleague
FROM employees e1
JOIN employees e2 ON e1.department_id = e2.department_id
WHERE e1.id < e2.id;

-- 自连接：找出产品价格变化
SELECT
    p1.product_name,
    p1.price AS old_price,
    p2.price AS new_price
FROM price_history p1
JOIN price_history p2 ON p1.product_id = p2.product_id
AND p1.recorded_at < p2.recorded_at
AND p2.recorded_at = (
    SELECT MIN(recorded_at)
    FROM price_history
    WHERE product_id = p1.product_id
    AND recorded_at > p1.recorded_at
);
```

## 集合操作

```sql
-- UNION：并集（去重）
SELECT id, name FROM users
UNION
SELECT id, name FROM admins;

-- UNION ALL：并集（不去重）
SELECT id, name FROM users
UNION ALL
SELECT id, name FROM admins;

-- INTERSECT：交集
SELECT id, name FROM users
INTERSECT
SELECT id, name FROM admins;

-- EXCEPT：差集
SELECT id, name FROM users
EXCEPT
SELECT id, name FROM admins;
```

## Java 实现

```java
import java.sql.*;
import java.util.*;

public class AdvancedQueryDemo {

    // CTE 查询
    public void testCTE(Connection conn) throws SQLException {
        String sql = """
            WITH regional_sales AS (
                SELECT region, SUM(amount) AS total_sales
                FROM orders
                GROUP BY region
            )
            SELECT region, total_sales
            FROM regional_sales
            WHERE total_sales > ?
            ORDER BY total_sales DESC
            """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, 100000);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    System.out.println(rs.getString("region") + ": " +
                        rs.getDouble("total_sales"));
                }
            }
        }
    }

    // 窗口函数查询
    public void testWindowFunction(Connection conn) throws SQLException {
        String sql = """
            SELECT
                product_name,
                price,
                category,
                ROW_NUMBER() OVER (
                    PARTITION BY category
                    ORDER BY price DESC
                ) AS rank_in_category,
                PERCENT_RANK() OVER (
                    PARTITION BY category
                    ORDER BY price DESC
                ) AS pct_rank
            FROM products
            ORDER BY category, rank_in_category
            """;

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                System.out.printf("%s - %s: %d (%.2f)%n",
                    rs.getString("category"),
                    rs.getString("product_name"),
                    rs.getInt("rank_in_category"),
                    rs.getDouble("pct_rank"));
            }
        }
    }

    // 递归 CTE 查询组织结构
    public void testRecursiveCTE(Connection conn) throws SQLException {
        String sql = """
            WITH RECURSIVE org_tree AS (
                SELECT id, name, manager_id, 1 AS level
                FROM employees
                WHERE manager_id IS NULL
                UNION ALL
                SELECT e.id, e.name, e.manager_id, ot.level + 1
                FROM employees e
                INNER JOIN org_tree ot ON e.manager_id = ot.id
            )
            SELECT id, name, manager_id, level
            FROM org_tree
            ORDER BY level, id
            """;

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                System.out.printf("Level %d: %s (Manager: %s)%n",
                    rs.getInt("level"),
                    rs.getString("name"),
                    rs.getObject("manager_id"));
            }
        }
    }
}
```
