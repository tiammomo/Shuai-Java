package com.shuai.database.mysql;

import com.shuai.database.mysql.MySqlConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * MySQL CRUD 操作演示类
 *
 * ============================================================
 * CRUD 是什么？
 * ------------------------------------------------------------
 * CRUD 是数据库操作的四种基本类型的缩写：
 *
 * C - Create (插入)    : INSERT 语句
 * R - Read   (查询)    : SELECT 语句
 * U - Update (更新)    : UPDATE 语句
 * D - Delete (删除)    : DELETE 语句
 *
 * ============================================================
 * JDBC API 核心接口
 * ------------------------------------------------------------
 * 1. DriverManager     : 管理数据库驱动
 * 2. Connection        : 数据库连接
 * 3. PreparedStatement : 预编译 SQL 语句（防注入）
 * 4. ResultSet         : 查询结果集
 * 5. Statement         : 执行 SQL 语句
 *
 * ============================================================
 * SQL 注入攻击示例
 * ------------------------------------------------------------
 * 错误写法（存在 SQL 注入风险）：
 *   String sql = "SELECT * FROM users WHERE username = '" + username + "'";
 *   如果 username = "'; DROP TABLE users; --"
 *   实际执行：SELECT * FROM users WHERE username = ''; DROP TABLE users; --
 *
 * 正确写法（使用 PreparedStatement）：
 *   String sql = "SELECT * FROM users WHERE username = ?";
 *   stmt.setString(1, username);  // 自动转义
 *
 * @author Shuai
 */
public class CrudDemo {

    /**
     * 执行所有 CRUD 演示
     *
     * 演示流程：
     * 1. createDemo()      -> 插入操作（INSERT）
     * 2. readDemo()        -> 查询操作（SELECT）
     * 3. updateDemo()      -> 更新操作（UPDATE）
     * 4. deleteDemo()      -> 删除操作（DELETE）
     * 5. batchInsertDemo() -> 批量插入
     * 6. paginationDemo()  -> 分页查询
     */
    public void runAllDemos() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("       MySQL CRUD 操作演示");
        System.out.println("=".repeat(50));

        try {
            // 1. 插入操作
            createDemo();

            // 2. 查询操作
            readDemo();

            // 3. 更新操作
            updateDemo();

            // 4. 删除操作
            deleteDemo();

            // 5. 批量插入
            batchInsertDemo();

            // 6. 分页查询
            paginationDemo();

        } catch (Exception e) {
            System.err.println("CRUD 操作异常: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ==================== Create 插入操作 ====================

    /**
     * 插入操作演示（Create）
     *
     * ============================================================
     * INSERT 语句语法
     * ------------------------------------------------------------
     * INSERT INTO table_name (col1, col2, ...) VALUES (?, ?, ...);
     *
     * 变体：
     * - INSERT INTO ... VALUES (...)        : 单条插入
     * - INSERT INTO ... VALUES (...), (...) : 批量插入
     * - INSERT INTO ... SET col = value     : MySQL 扩展语法
     * - INSERT IGNORE INTO ...              : 忽略重复插入
     * - REPLACE INTO ...                    : 存在则替换
     *
     * ============================================================
     * PreparedStatement vs Statement
     * ------------------------------------------------------------
     * Statement:
     *   - 直接拼接 SQL 字符串
     *   - 存在 SQL 注入风险
     *   - 性能略好（无需预编译）
     *
     * PreparedStatement:
     *   - 使用 ? 占位符
     *   - 自动转义，防止 SQL 注入
     *   - 支持预编译，提升性能
     *   - 支持批量操作
     *
     * ============================================================
     * 获取自增 ID
     * ------------------------------------------------------------
     * MySQL 自增主键（auto_increment）的特点：
     * - 自动生成，唯一且递增
     * - 通过 getGeneratedKeys() 获取
     * - 适用于分布式场景（全局唯一）
     */
    private void createDemo() throws SQLException {
        System.out.println("\n[1] 插入操作演示");

        // 使用 PreparedStatement 防止 SQL 注入
        String sql = "INSERT INTO users (username, email, password, status) VALUES (?, ?, ?, ?)";

        try (Connection conn = MySqlConnectionManager.getConnection();
             // RETURN_GENERATED_KEYS 用于获取自增主键
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // 设置参数（从 1 开始）
            String username = "newuser_" + System.currentTimeMillis();
            stmt.setString(1, username);       // String 类型
            stmt.setString(2, username + "@example.com");
            stmt.setString(3, "password123");  // 实际项目中应加密存储
            stmt.setInt(4, 1);                 // Integer 类型

            // executeUpdate() 返回受影响的行数
            int rows = stmt.executeUpdate();
            System.out.println("  [INSERT] 插入行数: " + rows);

            // 获取自增主键
            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                // getLong(1) 获取第一个自增字段的值
                long userId = generatedKeys.getLong(1);
                System.out.println("  [INSERT] 生成的用户ID: " + userId);
            }
        }

        // ==================== 其他插入方式 ====================
        // 方式2：批量插入（VALUES 后跟多组值）
        // INSERT INTO users (username, email) VALUES
        //   ('user1', 'user1@example.com'),
        //   ('user2', 'user2@example.com');

        // 方式3：INSERT IGNORE（忽略重复）
        // INSERT IGNORE INTO users (username) VALUES ('test');

        // 方式4：REPLACE（存在则删除再插入）
        // REPLACE INTO users (id, username) VALUES (1, 'newname');
    }

    // ==================== Read 查询操作 ====================

    /**
     * 查询操作演示（Read）
     *
     * ============================================================
     * SELECT 语句语法
     * ------------------------------------------------------------
     * SELECT column1, column2, ... FROM table_name [WHERE ...] [ORDER BY ...] [LIMIT ...]
     *
     * WHERE 子句条件：
     * - 比较：=, <, >, <=, >=, <>
     * - 逻辑：AND, OR, NOT
     * - 范围：BETWEEN ... AND ..., IN (...)
     * - 模糊：LIKE '%xxx%', LIKE 'xxx%'
     * - 空值：IS NULL, IS NOT NULL
     *
     * ============================================================
     * ResultSet 光标操作
     * ------------------------------------------------------------
     * ResultSet 默认只能向前移动（TYPE_FORWARD_ONLY）：
     * - next()     : 移动到下一行，返回是否有数据
     * - previous() : 移动到上一行（需配置）
     * - first/last : 移动到首/尾行
     * - absolute(n): 移动到第 n 行
     *
     * 获取字段值方法：
     * - getString("column_name")  : String 类型
     * - getLong("column_name")    : long 类型
     * - getInt("column_name")     : int 类型
     * - getDouble("column_name")  : double 类型
     * - getDate("column_name")    : Date 类型
     * - getObject("column_name")  : Object 类型
     */
    private void readDemo() throws SQLException {
        System.out.println("\n[2] 查询操作演示");

        // ==================== 查询单条记录 ====================
        // 使用 WHERE 限制返回一条记录
        String sql1 = "SELECT * FROM users WHERE id = ?";
        try (Connection conn = MySqlConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql1)) {

            // 设置查询条件
            stmt.setLong(1, 1);  // 查询 id = 1 的用户

            try (ResultSet rs = stmt.executeQuery()) {
                // next() 必须调用，移动光标到第一行
                if (rs.next()) {
                    // 通过字段名获取值
                    System.out.println("  [SELECT] 单条记录:");
                    System.out.println("    - id: " + rs.getLong("id"));
                    System.out.println("    - username: " + rs.getString("username"));
                    System.out.println("    - email: " + rs.getString("email"));
                    System.out.println("    - status: " + rs.getInt("status"));
                } else {
                    System.out.println("  [SELECT] 未找到记录");
                }
            }
        }

        // ==================== 查询多条记录 ====================
        // 查询满足条件的所有记录
        String sql2 = "SELECT * FROM users WHERE status = ?";
        try (Connection conn = MySqlConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql2)) {

            stmt.setInt(1, 1);  // 查询 status = 1 的用户

            try (ResultSet rs = stmt.executeQuery()) {
                List<Map<String, Object>> users = new ArrayList<>();

                // 循环读取所有记录
                while (rs.next()) {
                    Map<String, Object> user = new HashMap<>();
                    user.put("id", rs.getLong("id"));
                    user.put("username", rs.getString("username"));
                    user.put("email", rs.getString("email"));
                    users.add(user);
                }

                System.out.println("  [SELECT] 多条记录: 共找到 " + users.size() + " 个用户");
                if (!users.isEmpty()) {
                    System.out.println("    第一个用户: " + users.get(0));
                }
            }
        }

        // ==================== 聚合查询 ====================
        // COUNT/SUM/AVG/MAX/MIN 等聚合函数
        // SELECT COUNT(*) FROM users WHERE status = 1
    }

    // ==================== Update 更新操作 ====================

    /**
     * 更新操作演示（Update）
     *
     * ============================================================
     * UPDATE 语句语法
     * ------------------------------------------------------------
     * UPDATE table_name
     * SET column1 = value1, column2 = value2, ...
     * WHERE condition
     *
     * 重要：WHERE 子句不能省略！
     * 省略会导致更新所有记录。
     *
     * ============================================================
     * 更新流程
     * ------------------------------------------------------------
     * 1. 执行更新前的验证（可选）
     * 2. 执行 UPDATE 语句
     * 3. 检查受影响行数
     * 4. 验证更新结果
     *
     * ============================================================
     * 乐观锁 vs 悲观锁
     * ------------------------------------------------------------
     * 悲观锁：SELECT ... FOR UPDATE（锁定行，防止并发）
     * 乐观锁：WHERE id = ? AND version = ?（CAS 机制）
     */
    private void updateDemo() throws SQLException {
        System.out.println("\n[3] 更新操作演示");

        // ==================== 准备测试数据 ====================
        // 先插入一条记录用于更新测试
        String insertSql = "INSERT INTO users (username, email, password) VALUES (?, ?, ?)";
        long testUserId;
        try (Connection conn = MySqlConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
            String testUser = "update_test_" + System.currentTimeMillis();
            stmt.setString(1, testUser);
            stmt.setString(2, testUser + "@example.com");
            stmt.setString(3, "password");
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                rs.next();
                testUserId = rs.getLong(1);
            }
        }
        System.out.println("  [SETUP] 创建测试用户，ID: " + testUserId);

        // ==================== 执行更新 ====================
        String updateSql = "UPDATE users SET email = ?, status = ? WHERE id = ?";
        try (Connection conn = MySqlConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(updateSql)) {
            stmt.setString(1, "updated@example.com");  // 更新 email
            stmt.setInt(2, 1);                         // 更新 status
            stmt.setLong(3, testUserId);               // 更新条件

            int rows = stmt.executeUpdate();
            System.out.println("  [UPDATE] 影响行数: " + rows);
        }

        // ==================== 验证更新结果 ====================
        String selectSql = "SELECT * FROM users WHERE id = ?";
        try (Connection conn = MySqlConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(selectSql)) {
            stmt.setLong(1, testUserId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    System.out.println("  [VERIFY] 更新后 email: " + rs.getString("email"));
                    System.out.println("  [VERIFY] 更新后 status: " + rs.getInt("status"));
                }
            }
        }
    }

    // ==================== Delete 删除操作 ====================

    /**
     * 删除操作演示（Delete）
     *
     * ============================================================
     * DELETE 语句语法
     * ------------------------------------------------------------
     * DELETE FROM table_name WHERE condition
     *
     * 重要：WHERE 子句不能省略！
     * 省略会导致删除所有记录。
     *
     * ============================================================
     * 删除 vs 截断（TRUNCATE）
     * ------------------------------------------------------------
     * DELETE:
     *   - 可以带 WHERE 条件
     *   - 可以回滚
     *   - 逐行删除，触发 Trigger
     *   - 自增键不会重置
     *
     * TRUNCATE:
     *   - 不能带条件
     *   - 快速删除所有数据
     *   - 释放存储空间
     *   - 重置自增键
     *   - 不可回滚
     */
    private void deleteDemo() throws SQLException {
        System.out.println("\n[4] 删除操作演示");

        // ==================== 准备测试数据 ====================
        String insertSql = "INSERT INTO users (username, email, password) VALUES (?, ?, ?)";
        long testUserId;
        try (Connection conn = MySqlConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
            String testUser = "delete_test_" + System.currentTimeMillis();
            stmt.setString(1, testUser);
            stmt.setString(2, testUser + "@example.com");
            stmt.setString(3, "password");
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                rs.next();
                testUserId = rs.getLong(1);
            }
        }
        System.out.println("  [SETUP] 创建测试用户，ID: " + testUserId);

        // ==================== 执行删除 ====================
        String deleteSql = "DELETE FROM users WHERE id = ?";
        try (Connection conn = MySqlConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(deleteSql)) {
            stmt.setLong(1, testUserId);

            int rows = stmt.executeUpdate();
            System.out.println("  [DELETE] 删除行数: " + rows);

            if (rows > 0) {
                System.out.println("  [DELETE] 用户 " + testUserId + " 已删除");
            }
        }

        // ==================== 验证删除结果 ====================
        String selectSql = "SELECT * FROM users WHERE id = ?";
        try (Connection conn = MySqlConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(selectSql)) {
            stmt.setLong(1, testUserId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    System.out.println("  [WARN] 用户仍存在（删除失败）");
                } else {
                    System.out.println("  [VERIFY] 用户已成功删除");
                }
            }
        }
    }

    // ==================== 批量插入 ====================

    /**
     * 批量插入演示
     *
     * ============================================================
     * 批量操作的优势
     * ------------------------------------------------------------
     * 单条插入：N 条数据 = N 次网络往返
     * 批量插入：N 条数据 = 1 次网络往返
     *
     * 性能提升：10-100 倍
     *
     * ============================================================
     * JDBC 批量操作流程
     * ------------------------------------------------------------
     * 1. 关闭自动提交（conn.setAutoCommit(false)）
     * 2. 循环添加：stmt.addBatch()
     * 3. 执行批量：stmt.executeBatch()
     * 4. 提交事务：conn.commit()
     * 5. 恢复自动提交
     *
     * ============================================================
     * 批量大小建议
     * ------------------------------------------------------------
     * - 太小：批量优势不明显
     * - 太大：内存压力大，可能超时
     * - 推荐：100-1000 条/批
     *
     * 注意：MySQL max_allowed_packet 限制单次发送数据量
     */
    private void batchInsertDemo() throws SQLException {
        System.out.println("\n[5] 批量插入演示");

        String sql = "INSERT INTO users (username, email, password, status) VALUES (?, ?, ?, ?)";

        try (Connection conn = MySqlConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // 关闭自动提交，手动控制事务
            conn.setAutoCommit(false);

            long startTime = System.currentTimeMillis();

            // 批量添加数据
            for (int i = 1; i <= 5; i++) {
                String username = "batch_user_" + System.currentTimeMillis() + "_" + i;
                stmt.setString(1, username);
                stmt.setString(2, username + "@example.com");
                stmt.setString(3, "password123");
                stmt.setInt(4, 1);

                // addBatch() 将 SQL 添加到批次，不立即执行
                stmt.addBatch();
            }

            // executeBatch() 执行批次中所有 SQL
            int[] results = stmt.executeBatch();

            // 提交事务
            conn.commit();

            // 恢复自动提交
            conn.setAutoCommit(true);

            long endTime = System.currentTimeMillis();

            // 计算总影响行数
            int totalRows = 0;
            for (int r : results) {
                if (r >= 0) {
                    totalRows += r;
                }
                // r = -2 表示命令成功但不返回更新计数
            }

            System.out.println("  [BATCH] 批量插入: " + totalRows + " 行");
            System.out.println("  [BATCH] 耗时: " + (endTime - startTime) + "ms");
        }
    }

    // ==================== 分页查询 ====================

    /**
     * 分页查询演示
     *
     * ============================================================
     * 分页方式对比
     * ------------------------------------------------------------
     * 方式1：LIMIT offset, count
     *   SELECT * FROM users LIMIT 10 OFFSET 20;
     *   - 优点：简单直观
     *   - 缺点：深层分页性能差（需扫描 offset+count 行）
     *
     * 方式2：LIMIT count OFFSET offset
     *   SELECT * FROM users LIMIT 20, 10;
     *   - MySQL 特有语法
     *   - 与方式1 等价
     *
     * 方式3：游标分页（推荐大表）
     *   SELECT * FROM users WHERE id > last_id LIMIT 10;
     *   - 性能稳定，不受分页深度影响
     *   - 不支持随机跳页
     *
     * ============================================================
     * 分页计算公式
     * ------------------------------------------------------------
     * 页码 page 从 1 开始
     * 每页大小 pageSize
     *
     * OFFSET = (page - 1) * pageSize
     * LIMIT = pageSize
     *
     * 示例：page=3, pageSize=10
     * OFFSET = (3-1) * 10 = 20
     * SQL: LIMIT 10 OFFSET 20
     *      = 获取第 21-30 条记录
     *
     * ============================================================
     * LIMIT 优化
     * ------------------------------------------------------------
     * LIMIT n 等价于 LIMIT 0, n
     * LIMIT n, m 等价于 LIMIT m OFFSET n
     *
     * 最大限制由 max_allowed_packet 决定
     */
    private void paginationDemo() throws SQLException {
        System.out.println("\n[6] 分页查询演示");

        // ==================== 传统分页 ====================
        int page = 1;           // 当前页码（从 1 开始）
        int pageSize = 3;       // 每页大小
        int offset = (page - 1) * pageSize;  // 计算偏移量

        String sql1 = "SELECT * FROM users ORDER BY id LIMIT ? OFFSET ?";
        try (Connection conn = MySqlConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql1)) {
            stmt.setInt(1, pageSize);
            stmt.setInt(2, offset);

            System.out.println("  [PAGE] 页码: " + page + ", 每页: " + pageSize);

            try (ResultSet rs = stmt.executeQuery()) {
                int count = 0;
                while (rs.next()) {
                    System.out.println("    - id=" + rs.getLong("id")
                            + ", username=" + rs.getString("username"));
                    count++;
                }
                System.out.println("  [PAGE] 本页数量: " + count);
            }
        }

        // ==================== MySQL 风格分页 ====================
        // LIMIT offset, count 是 MySQL 特有语法
        System.out.println("\n  [SQL] MySQL 风格分页:");
        System.out.println("    SELECT * FROM users ORDER BY id LIMIT 5 OFFSET 0;");

        // ==================== 游标分页（推荐）====================
        // 对于大表，使用游标分页性能更好
        // SELECT * FROM users WHERE id > 100 ORDER BY id LIMIT 10;
        System.out.println("\n  [SQL] 游标分页（推荐大表）:");
        System.out.println("    SELECT * FROM users WHERE id > ? ORDER BY id LIMIT ?;");
        System.out.println("    优点：性能稳定，不受分页深度影响");
    }
}
