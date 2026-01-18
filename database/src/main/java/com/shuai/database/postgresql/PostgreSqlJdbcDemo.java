package com.shuai.database.postgresql;

import org.postgresql.copy.CopyManager;
import org.postgresql.core.BaseConnection;

import java.io.*;
import java.sql.*;
import java.util.Properties;

/**
 * PostgreSQL JDBC 操作演示
 */
public class PostgreSqlJdbcDemo {

    private static final String URL = "jdbc:postgresql://localhost:5432/test";
    private static final String USER = "ubuntu";
    private static final String PASSWORD = "ubuntu";

    public void runAllDemos() {
        System.out.println("\n--- PostgreSQL JDBC 操作演示 ---");

        try {
            basicConnectionDemo();
            crudDemo();
            batchOperationDemo();
            copyManagerDemo();
            arrayTypeDemo();
        } catch (Exception e) {
            System.err.println("PostgreSQL 操作异常: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 基本连接演示
     */
    private void basicConnectionDemo() throws SQLException {
        System.out.println("\n[1] 基本连接演示");

        // 方式1: 直接连接
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            System.out.println("  连接成功: " + conn.getClass().getSimpleName());
            System.out.println("  数据库版本: " + conn.getMetaData().getDatabaseProductVersion());
            System.out.println("  驱动版本: " + conn.getMetaData().getDriverVersion());
        }

        // 方式2: 使用 Properties 配置
        Properties props = new Properties();
        props.setProperty("user", USER);
        props.setProperty("password", PASSWORD);
        props.setProperty("connectTimeout", "10");
        props.setProperty("socketTimeout", "30");

        try (Connection conn = DriverManager.getConnection(URL, props)) {
            System.out.println("  使用 Properties 连接成功");
        }
    }

    /**
     * CRUD 操作演示
     */
    private void crudDemo() throws SQLException {
        System.out.println("\n[2] CRUD 操作演示");

        // Create - 插入
        String insertSql = "INSERT INTO users (username, email, password, status) VALUES (?, ?, ?, ?) RETURNING id";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(insertSql)) {

            String username = "pg_user_" + System.currentTimeMillis();
            stmt.setString(1, username);
            stmt.setString(2, username + "@example.com");
            stmt.setString(3, "password123");
            stmt.setInt(4, 1);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    System.out.println("  插入成功，用户ID: " + rs.getLong("id"));
                }
            }
        }

        // Read - 查询
        String selectSql = "SELECT * FROM users WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(selectSql)) {

            stmt.setInt(1, 1);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    System.out.println("  查询结果: id=" + rs.getInt("id")
                            + ", username=" + rs.getString("username")
                            + ", email=" + rs.getString("email"));
                }
            }
        }

        // Update - 更新
        String updateSql = "UPDATE users SET email = ? WHERE username = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(updateSql)) {

            stmt.setString(1, "updated@example.com");
            stmt.setString(2, "shuai");
            int rows = stmt.executeUpdate();
            System.out.println("  更新行数: " + rows);
        }

        // Delete - 删除
        String deleteSql = "DELETE FROM users WHERE username LIKE ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(deleteSql)) {

            stmt.setString(1, "pg_user_%");
            int rows = stmt.executeUpdate();
            System.out.println("  删除行数: " + rows);
        }
    }

    /**
     * 批量操作演示
     */
    private void batchOperationDemo() throws SQLException {
        System.out.println("\n[3] 批量操作演示");

        String sql = "INSERT INTO users (username, email, password, status) VALUES (?, ?, ?, ?)";

        try (Connection conn = getConnection()) {
            // 关闭自动提交，手动管理事务
            conn.setAutoCommit(false);

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                for (int i = 1; i <= 3; i++) {
                    String username = "batch_user_" + System.currentTimeMillis() + "_" + i;
                    stmt.setString(1, username);
                    stmt.setString(2, username + "@example.com");
                    stmt.setString(3, "password123");
                    stmt.setInt(4, 1);
                    stmt.addBatch();
                }

                int[] results = stmt.executeBatch();
                int totalRows = 0;
                for (int r : results) {
                    totalRows += r;
                }
                System.out.println("  批量插入成功: " + totalRows + " 行");
            }

            conn.commit();
        }

        // 使用 COPY 进行高速批量导入
        System.out.println("\n  PostgreSQL COPY 高速导入:");
        System.out.println("  使用 CopyManager 进行批量数据导入导出");
    }

    /**
     * CopyManager 演示
     */
    private void copyManagerDemo() throws SQLException, IOException {
        System.out.println("\n[4] CopyManager 演示");

        try (Connection conn = getConnection()) {
            BaseConnection pgConn = conn.unwrap(BaseConnection.class);
            CopyManager copyManager = new CopyManager(pgConn);

            // 从 CSV 导入（不含表头，包含所有 NOT NULL 列）
            String csvData = "10,User10,user10@example.com,password123,1\n11,User11,user11@example.com,password123,1\n";
            StringReader reader = new StringReader(csvData);
            long rows = copyManager.copyIn("COPY users(id, username, email, password, status) FROM STDIN WITH CSV", reader);
            System.out.println("  COPY 导入行数: " + rows);

            // 导出到 CSV
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            copyManager.copyOut("COPY (SELECT id, username, email FROM users ORDER BY id DESC LIMIT 5) TO STDOUT WITH CSV", baos);
            System.out.println("  COPY 导出结果:");
            System.out.println("  " + baos.toString().replace("\n", "\n  "));
        }
    }

    /**
     * 数组类型演示
     */
    private void arrayTypeDemo() throws SQLException {
        System.out.println("\n[5] PostgreSQL 数组类型演示");

        // 创建带数组字段的表
        String createSql = """
            CREATE TABLE IF NOT EXISTS user_tags (
                id SERIAL PRIMARY KEY,
                user_id INTEGER REFERENCES users(id),
                tags TEXT[],
                scores INTEGER[]
            )
            """;
        String insertSql = "INSERT INTO user_tags (user_id, tags, scores) VALUES (?, ?, ?)";
        String selectSql = "SELECT * FROM user_tags WHERE tags @> ARRAY['java']";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createSql);
        }

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(insertSql)) {
            stmt.setInt(1, 1);
            stmt.setArray(2, conn.createArrayOf("text", new String[]{"java", "spring", "database"}));
            stmt.setArray(3, conn.createArrayOf("int", new Integer[]{90, 85, 95}));
            stmt.executeUpdate();
            System.out.println("  数组插入成功");
        }

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(selectSql);
             ResultSet rs = stmt.executeQuery()) {
            System.out.println("  查询包含 'java' 标签的用户:");
            while (rs.next()) {
                Array tags = rs.getArray("tags");
                System.out.println("    tags: " + java.util.Arrays.toString((String[]) tags.getArray()));
            }
        }
    }

    /**
     * 获取数据库连接
     */
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
