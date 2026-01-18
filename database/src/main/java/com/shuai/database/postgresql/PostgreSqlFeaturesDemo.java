package com.shuai.database.postgresql;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * PostgreSQL 特性演示
 */
public class PostgreSqlFeaturesDemo {

    private static final String URL = "jdbc:postgresql://localhost:5432/test";
    private static final String USER = "ubuntu";
    private static final String PASSWORD = "ubuntu";

    public void runAllDemos() {
        System.out.println("\n--- PostgreSQL 高级特性演示 ---");

        try {
            cteDemo();
            windowFunctionDemo();
            recursiveQueryDemo();
            fullTextSearchDemo();
            upsertDemo();
        } catch (Exception e) {
            System.err.println("PostgreSQL 特性操作异常: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * CTE (Common Table Expressions) 演示
     */
    private void cteDemo() throws SQLException {
        System.out.println("\n[1] CTE (公共表表达式) 演示");

        // 基本 CTE
        String cteSql = """
            WITH high_value_users AS (
                SELECT u.id, u.username, COUNT(o.id) as order_count
                FROM users u
                JOIN orders o ON u.id = o.user_id
                GROUP BY u.id, u.username
                HAVING COUNT(o.id) >= 1
            )
            SELECT * FROM high_value_users ORDER BY order_count DESC
            """;

        executeQuery(cteSql, "CTE 查询高价值用户");

        // 可写 CTE
        String writableCteSql = """
            WITH deleted_users AS (
                DELETE FROM users
                WHERE username LIKE 'batch_user_%'
                RETURNING id, username
            )
            SELECT COUNT(*) as deleted_count FROM deleted_users
            """;

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(writableCteSql);
            if (rs.next()) {
                System.out.println("  可写 CTE 删除行数: " + rs.getInt("deleted_count"));
            }
        }
    }

    /**
     * 窗口函数演示
     */
    private void windowFunctionDemo() throws SQLException {
        System.out.println("\n[2] 窗口函数演示");

        // ROW_NUMBER - 行号
        String rowNumSql = """
            SELECT
                id, username,
                ROW_NUMBER() OVER (ORDER BY created_at) as row_num
            FROM users
            ORDER BY id
            LIMIT 5
            """;
        executeQuery(rowNumSql, "ROW_NUMBER() 行号");

        // RANK - 排名
        String rankSql = """
            SELECT
                id, name, salary,
                RANK() OVER (ORDER BY salary DESC) as salary_rank
            FROM employees
            ORDER BY salary_rank
            """;
        executeQuery(rankSql, "RANK() 排名");

        // NTILE - 分组
        String ntileSql = """
            SELECT
                id, name, salary,
                NTILE(3) OVER (ORDER BY salary DESC) as salary_group
            FROM employees
            """;
        executeQuery(ntileSql, "NTILE() 分组");

        // LEAD/LAG - 前后行
        String leadLagSql = """
            SELECT
                id, name, salary,
                LAG(salary) OVER (ORDER BY salary) as prev_salary,
                salary - COALESCE(LAG(salary) OVER (ORDER BY salary), 0) as salary_diff
            FROM employees
            ORDER BY salary
            """;
        executeQuery(leadLagSql, "LEAD/LAG 前后行对比");

        // 聚合窗口函数
        String aggWindowSql = """
            SELECT
                id, name, salary,
                SUM(salary) OVER () as total_salary,
                AVG(salary) OVER () as avg_salary
            FROM employees
            """;
        executeQuery(aggWindowSql, "聚合窗口函数");
    }

    /**
     * 递归查询演示
     */
    private void recursiveQueryDemo() throws SQLException {
        System.out.println("\n[3] 递归查询演示 (WITH RECURSIVE)");

        // 员工层级查询
        String recursiveSql = """
            WITH RECURSIVE employee_hierarchy AS (
                -- 基础查询 (根节点)
                SELECT id, name, manager_id, 1 as level, name::TEXT as path
                FROM employees
                WHERE manager_id IS NULL

                UNION ALL

                -- 递归查询
                SELECT e.id, e.name, e.manager_id, eh.level + 1, (eh.path || ' -> ' || e.name)::TEXT
                FROM employees e
                INNER JOIN employee_hierarchy eh ON e.manager_id = eh.id
            )
            SELECT id, name, level, path
            FROM employee_hierarchy
            ORDER BY level, id
            """;

        executeQuery(recursiveSql, "员工层级结构");

        // 数字序列生成
        String seriesSql = """
            WITH RECURSIVE nums(n) AS (
                SELECT 1
                UNION ALL
                SELECT n + 1 FROM nums WHERE n < 10
            )
            SELECT n FROM nums
            """;
        executeQuery(seriesSql, "递归生成数字序列 1-10");
    }

    /**
     * 全文搜索演示
     */
    private void fullTextSearchDemo() throws SQLException {
        System.out.println("\n[4] 全文搜索演示");

        // 基本全文搜索
        String searchSql = """
            SELECT id, title, author,
                   ts_headline('english', content,
                       to_tsquery('english', 'database'),
                       'StartSel=<b>, StopSel=</b>, MaxWords=50, MinWords=20') as highlighted
            FROM articles
            WHERE to_tsvector('english', title || ' ' || content) @@ to_tsquery('english', 'database')
            """;

        executeQuery(searchSql, "搜索包含 'database' 的文章");

        // 多词搜索
        String multiSearchSql = """
            SELECT id, title, author,
                   ts_rank_cd(to_tsvector('english', title || ' ' || content),
                              to_tsquery('english', 'postgresql & tutorial')) as rank
            FROM articles
            WHERE to_tsvector('english', title || ' ' || content) @@
                  to_tsquery('english', 'postgresql & tutorial')
            ORDER BY rank DESC
            """;

        executeQuery(multiSearchSql, "搜索包含 'postgresql' 和 'tutorial' 的文章");

        // 使用 GIN 索引
        System.out.println("  已创建 GIN 索引: idx_articles_search");
        System.out.println("  索引列: to_tsvector('english', title || ' ' || content)");
    }

    /**
     * UPSERT (INSERT ON CONFLICT) 演示
     */
    private void upsertDemo() throws SQLException {
        System.out.println("\n[5] UPSERT (INSERT ON CONFLICT) 演示");

        // 插入或更新
        String upsertSql = """
            INSERT INTO products (name, description, price, stock, category)
            VALUES ('New Product', 'A new product', 99.99, 100, '测试')
            ON CONFLICT (name)
            DO UPDATE SET
                price = EXCLUDED.price,
                stock = EXCLUDED.stock
            RETURNING id, name, price, stock
            """;

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(upsertSql);
            while (rs.next()) {
                System.out.println("  UPSERT 结果: id=" + rs.getInt("id")
                        + ", name=" + rs.getString("name")
                        + ", price=" + rs.getDouble("price")
                        + ", stock=" + rs.getInt("stock"));
            }
        }

        // 忽略冲突
        String ignoreSql = """
            INSERT INTO products (name, description, price, stock, category)
            VALUES ('New Product', 'A new product', 199.99, 200, '测试')
            ON CONFLICT (name) DO NOTHING
            RETURNING id
            """;

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(ignoreSql);
            if (rs.next()) {
                System.out.println("  新插入记录: id=" + rs.getInt("id"));
            } else {
                System.out.println("  记录已存在，已忽略");
            }
        }
    }

    /**
     * 执行查询并打印结果
     */
    private void executeQuery(String sql, String label) throws SQLException {
        System.out.println("  " + label + ":");
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            // 打印列名
            StringBuilder header = new StringBuilder("    ");
            for (int i = 1; i <= columnCount; i++) {
                if (i > 1) header.append(" | ");
                header.append(metaData.getColumnLabel(i));
            }
            System.out.println(header);

            // 打印数据
            int rowCount = 0;
            while (rs.next() && rowCount < 5) {
                StringBuilder row = new StringBuilder("    ");
                for (int i = 1; i <= columnCount; i++) {
                    if (i > 1) row.append(" | ");
                    Object value = rs.getObject(i);
                    row.append(value != null ? truncate(value.toString(), 20) : "NULL");
                }
                System.out.println(row);
                rowCount++;
            }
            if (rowCount == 5) {
                System.out.println("    ... (更多数据)");
            }
        }
    }

    /**
     * 截断字符串
     */
    private String truncate(String str, int maxLen) {
        if (str.length() <= maxLen) return str;
        return str.substring(0, maxLen - 2) + "..";
    }

    /**
     * 获取数据库连接
     */
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
