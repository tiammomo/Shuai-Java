package com.shuai.database.mysql;

import com.shuai.database.mysql.MySqlConnectionManager;

import java.sql.*;

/**
 * MySQL 高级查询演示
 */
public class AdvancedQueryDemo {

    public void runAllDemos() {
        System.out.println("\n--- MySQL 高级查询演示 ---");

        try {
            joinQueryDemo();
            aggregationDemo();
            groupByHavingDemo();
            subqueryDemo();
            caseWhenDemo();
        } catch (Exception e) {
            System.err.println("高级查询异常: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * JOIN 查询演示
     */
    private void joinQueryDemo() throws SQLException {
        System.out.println("\n[1] JOIN 查询演示");

        // INNER JOIN - 获取有订单的用户信息
        System.out.println("  INNER JOIN - 有订单的用户:");
        String innerSql = """
            SELECT u.id, u.username, u.email, o.id as order_id, o.total_price, o.status
            FROM users u
            INNER JOIN orders o ON u.id = o.user_id
            ORDER BY o.id
            LIMIT 5
            """;
        executeQuery(innerSql, "INNER JOIN");

        // LEFT JOIN - 获取所有用户及其订单
        System.out.println("\n  LEFT JOIN - 所有用户及其订单:");
        String leftSql = """
            SELECT u.id, u.username, o.id as order_id, o.total_price
            FROM users u
            LEFT JOIN orders o ON u.id = o.user_id
            ORDER BY u.id, o.id
            LIMIT 10
            """;
        executeQuery(leftSql, "LEFT JOIN");

        // RIGHT JOIN - 获取所有订单及其用户信息
        System.out.println("\n  RIGHT JOIN - 订单和用户:");
        String rightSql = """
            SELECT u.id, u.username, o.id as order_id, o.total_price
            FROM users u
            RIGHT JOIN orders o ON u.id = o.user_id
            ORDER BY o.id
            """;
        executeQuery(rightSql, "RIGHT JOIN");
    }

    /**
     * 聚合函数演示
     */
    private void aggregationDemo() throws SQLException {
        System.out.println("\n[2] 聚合函数演示");

        // COUNT - 统计用户数量
        System.out.println("  COUNT 统计:");
        String countSql = "SELECT COUNT(*) as total_users FROM users";
        executeQuery(countSql, "COUNT");

        // SUM - 统计订单总金额
        System.out.println("\n  SUM 统计:");
        String sumSql = "SELECT SUM(total_price) as total_amount FROM orders WHERE status = 2";
        executeQuery(sumSql, "SUM");

        // AVG - 统计平均订单金额
        System.out.println("\n  AVG 统计:");
        String avgSql = "SELECT AVG(total_price) as avg_amount FROM orders";
        executeQuery(avgSql, "AVG");

        // MAX/MIN - 统计最高/最低价格产品
        System.out.println("\n  MAX/MIN 统计:");
        String maxMinSql = "SELECT MAX(price) as max_price, MIN(price) as min_price FROM products";
        executeQuery(maxMinSql, "MAX/MIN");

        // 组合聚合
        System.out.println("\n  组合聚合:");
        String multiSql = """
            SELECT
                COUNT(*) as user_count,
                COUNT(DISTINCT user_id) as active_user_count,
                SUM(total_price) as total_revenue,
                AVG(total_price) as avg_order_value
            FROM orders
            """;
        executeQuery(multiSql, "组合聚合");
    }

    /**
     * GROUP BY + HAVING 演示
     */
    private void groupByHavingDemo() throws SQLException {
        System.out.println("\n[3] GROUP BY + HAVING 演示");

        // 按用户分组统计订单
        System.out.println("  按用户分组统计订单:");
        String groupSql = """
            SELECT
                u.id,
                u.username,
                COUNT(o.id) as order_count,
                SUM(o.total_price) as total_spent
            FROM users u
            LEFT JOIN orders o ON u.id = o.user_id
            GROUP BY u.id, u.username
            ORDER BY total_spent DESC
            """;
        executeQuery(groupSql, "GROUP BY");

        // HAVING 筛选
        System.out.println("\n  HAVING 筛选 (消费超过5000的用户):");
        String havingSql = """
            SELECT
                u.id,
                u.username,
                COUNT(o.id) as order_count,
                SUM(o.total_price) as total_spent
            FROM users u
            JOIN orders o ON u.id = o.user_id
            GROUP BY u.id, u.username
            HAVING SUM(o.total_price) > 5000
            ORDER BY total_spent DESC
            """;
        executeQuery(havingSql, "HAVING");
    }

    /**
     * 子查询演示
     */
    private void subqueryDemo() throws SQLException {
        System.out.println("\n[4] 子查询演示");

        // IN 子查询 - 购买过iPhone的用户
        System.out.println("  IN 子查询 - 购买过iPhone的用户:");
        String inSql = """
            SELECT DISTINCT u.id, u.username, u.email
            FROM users u
            JOIN orders o ON u.id = o.user_id
            JOIN products p ON o.product_id = p.id
            WHERE p.name LIKE '%iPhone%'
            """;
        executeQuery(inSql, "IN 子查询");

        // EXISTS 子查询 - 有订单的用户
        System.out.println("\n  EXISTS 子查询 - 有订单的用户:");
        String existsSql = """
            SELECT id, username, email
            FROM users u
            WHERE EXISTS (
                SELECT 1 FROM orders o WHERE o.user_id = u.id
            )
            """;
        executeQuery(existsSql, "EXISTS 子查询");

        // 比较运算符子查询 - 购买最贵产品的用户
        System.out.println("\n  比较子查询 - 购买最贵产品的订单:");
        String compareSql = """
            SELECT o.id, u.username, p.name, o.total_price
            FROM orders o
            JOIN users u ON o.user_id = u.id
            JOIN products p ON o.product_id = p.id
            WHERE o.total_price = (
                SELECT MAX(total_price) FROM orders
            )
            """;
        executeQuery(compareSql, "比较子查询");

        // FROM 子查询
        System.out.println("\n  FROM 子查询 - 统计用户消费排名:");
        String fromSql = """
            SELECT t.username, t.total_spent,
                   RANK() OVER (ORDER BY t.total_spent DESC) as ranking
            FROM (
                SELECT u.username, COALESCE(SUM(o.total_price), 0) as total_spent
                FROM users u
                LEFT JOIN orders o ON u.id = o.user_id
                GROUP BY u.id, u.username
            ) t
            ORDER BY ranking
            """;
        executeQuery(fromSql, "FROM 子查询");
    }

    /**
     * CASE 表达式演示
     */
    private void caseWhenDemo() throws SQLException {
        System.out.println("\n[5] CASE 表达式演示");

        // 简单 CASE
        System.out.println("  订单状态映射:");
        String caseSql = """
            SELECT id, user_id,
                CASE status
                    WHEN 1 THEN '待支付'
                    WHEN 2 THEN '已支付'
                    WHEN 3 THEN '已发货'
                    WHEN 4 THEN '已完成'
                    WHEN 5 THEN '已取消'
                    ELSE '未知'
                END as status_text,
                total_price
            FROM orders
            """;
        executeQuery(caseSql, "CASE WHEN");

        // 搜索 CASE - 价格区间
        System.out.println("\n  产品价格区间:");
        String caseSql2 = """
            SELECT id, name, price,
                CASE
                    WHEN price < 2000 THEN '低价位'
                    WHEN price < 5000 THEN '中价位'
                    WHEN price < 10000 THEN '高价位'
                    ELSE '奢侈'
                END as price_range
            FROM products
            ORDER BY price
            """;
        executeQuery(caseSql2, "CASE 价格区间");
    }

    /**
     * 执行查询并打印结果
     */
    private void executeQuery(String sql, String label) throws SQLException {
        try (Connection conn = MySqlConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            // 打印列名
            StringBuilder header = new StringBuilder("  ");
            for (int i = 1; i <= columnCount; i++) {
                if (i > 1) header.append(" | ");
                header.append(metaData.getColumnLabel(i));
            }
            System.out.println(header);

            // 打印数据
            int rowCount = 0;
            while (rs.next() && rowCount < 5) {
                StringBuilder row = new StringBuilder("  ");
                for (int i = 1; i <= columnCount; i++) {
                    if (i > 1) row.append(" | ");
                    Object value = rs.getObject(i);
                    row.append(value != null ? value.toString() : "NULL");
                }
                System.out.println(row);
                rowCount++;
            }
            if (rowCount == 5) {
                System.out.println("  ... (更多数据)");
            }
        }
    }
}
