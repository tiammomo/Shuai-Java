package com.shuai.database.mysql;

import com.shuai.database.mysql.MySqlConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * MySQL CRUD 操作演示
 */
public class CrudDemo {

    /**
     * 运行所有 CRUD 演示
     */
    public void runAllDemos() {
        System.out.println("\n--- MySQL CRUD 操作演示 ---");

        try {
            createDemo();
            readDemo();
            updateDemo();
            deleteDemo();
            batchInsertDemo();
            paginationDemo();
        } catch (Exception e) {
            System.err.println("CRUD 操作异常: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 插入操作 (Create)
     */
    private void createDemo() throws SQLException {
        System.out.println("\n[1] 插入操作演示");

        String sql = "INSERT INTO users (username, email, password, status) VALUES (?, ?, ?, ?)";

        try (Connection conn = MySqlConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // 方式1: 使用 PreparedStatement 防止 SQL 注入
            String username = "newuser_" + System.currentTimeMillis();
            stmt.setString(1, username);
            stmt.setString(2, username + "@example.com");
            stmt.setString(3, "password123");
            stmt.setInt(4, 1);

            int rows = stmt.executeUpdate();
            System.out.println("  插入行数: " + rows);

            // 获取自增 ID
            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                System.out.println("  生成的用户ID: " + generatedKeys.getLong(1));
            }
        }

        // 方式2: 直接执行 SQL（不推荐，存在 SQL 注入风险）
        // statement.executeUpdate("INSERT INTO users ...");
    }

    /**
     * 查询操作 (Read)
     */
    private void readDemo() throws SQLException {
        System.out.println("\n[2] 查询操作演示");

        // 方式1: 查询单条记录
        String sql1 = "SELECT * FROM users WHERE id = ?";
        try (Connection conn = MySqlConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql1)) {

            stmt.setLong(1, 1);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    System.out.println("  查询单条: id=" + rs.getLong("id")
                            + ", username=" + rs.getString("username")
                            + ", email=" + rs.getString("email"));
                }
            }
        }

        // 方式2: 查询多条记录
        String sql2 = "SELECT * FROM users WHERE status = ?";
        try (Connection conn = MySqlConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql2)) {

            stmt.setInt(1, 1);
            try (ResultSet rs = stmt.executeQuery()) {
                List<Map<String, Object>> users = new ArrayList<>();
                while (rs.next()) {
                    Map<String, Object> user = new HashMap<>();
                    user.put("id", rs.getLong("id"));
                    user.put("username", rs.getString("username"));
                    user.put("email", rs.getString("email"));
                    users.add(user);
                }
                System.out.println("  查询多条: 共找到 " + users.size() + " 个用户");
                if (!users.isEmpty()) {
                    System.out.println("  第一个用户: " + users.get(0));
                }
            }
        }
    }

    /**
     * 更新操作 (Update)
     */
    private void updateDemo() throws SQLException {
        System.out.println("\n[3] 更新操作演示");

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

        // 执行更新
        String updateSql = "UPDATE users SET email = ?, status = ? WHERE id = ?";
        try (Connection conn = MySqlConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(updateSql)) {
            stmt.setString(1, "updated@example.com");
            stmt.setInt(2, 1);
            stmt.setLong(3, testUserId);
            int rows = stmt.executeUpdate();
            System.out.println("  更新行数: " + rows);
        }

        // 验证更新结果
        String selectSql = "SELECT * FROM users WHERE id = ?";
        try (Connection conn = MySqlConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(selectSql)) {
            stmt.setLong(1, testUserId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    System.out.println("  更新后: email=" + rs.getString("email"));
                }
            }
        }
    }

    /**
     * 删除操作 (Delete)
     */
    private void deleteDemo() throws SQLException {
        System.out.println("\n[4] 删除操作演示");

        // 先插入一条记录用于删除测试
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

        // 执行删除
        String deleteSql = "DELETE FROM users WHERE id = ?";
        try (Connection conn = MySqlConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(deleteSql)) {
            stmt.setLong(1, testUserId);
            int rows = stmt.executeUpdate();
            System.out.println("  删除行数: " + rows);
        }
    }

    /**
     * 批量插入演示
     */
    private void batchInsertDemo() throws SQLException {
        System.out.println("\n[5] 批量插入演示");

        String sql = "INSERT INTO users (username, email, password, status) VALUES (?, ?, ?, ?)";

        try (Connection conn = MySqlConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // 批量添加
            for (int i = 1; i <= 3; i++) {
                String username = "batch_user_" + System.currentTimeMillis() + "_" + i;
                stmt.setString(1, username);
                stmt.setString(2, username + "@example.com");
                stmt.setString(3, "password123");
                stmt.setInt(4, 1);
                stmt.addBatch();  // 添加到批次
            }

            int[] results = stmt.executeBatch();  // 执行批次
            int totalRows = 0;
            for (int r : results) {
                totalRows += r;
            }
            System.out.println("  批量插入成功: " + totalRows + " 行");
        }
    }

    /**
     * 分页查询演示
     */
    private void paginationDemo() throws SQLException {
        System.out.println("\n[6] 分页查询演示");

        // 方式1: LIMIT offset, count（传统方式）
        int page = 1;
        int pageSize = 3;
        int offset = (page - 1) * pageSize;

        String sql1 = "SELECT * FROM users ORDER BY id LIMIT ? OFFSET ?";
        try (Connection conn = MySqlConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql1)) {
            stmt.setInt(1, pageSize);
            stmt.setInt(2, offset);
            try (ResultSet rs = stmt.executeQuery()) {
                System.out.println("  分页查询 (页码=" + page + ", 每页=" + pageSize + "):");
                int count = 0;
                while (rs.next()) {
                    System.out.println("    - id=" + rs.getLong("id")
                            + ", username=" + rs.getString("username"));
                    count++;
                }
                System.out.println("  本页数量: " + count);
            }
        }

        // 方式2: LIMIT count OFFSET offset（MySQL 风格）
        String sql2 = "SELECT * FROM users ORDER BY id LIMIT 5 OFFSET 0";
        System.out.println("  MySQL 风格分页 SQL: " + sql2);
    }
}
