package com.shuai.database.shardingsphere.basic;

import java.sql.*;

/**
 * 纯 JDBC 测试 - 验证 MySQL 主从集群连接
 * 用于验证 ShardingSphere 配置的数据源是否可用
 */
public class PureJdbcDemo {

    private static final String MASTER_URL = "jdbc:mysql://localhost:3307/sharding_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String SLAVE0_URL = "jdbc:mysql://localhost:3308/sharding_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String SLAVE1_URL = "jdbc:mysql://localhost:3309/sharding_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USERNAME = "sharding";
    private static final String PASSWORD = "sharding";

    public static void run() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("       纯 JDBC 测试 - MySQL 主从集群验证");
        System.out.println("=".repeat(50));

        // 测试主库
        testMasterConnection();

        // 测试从库
        testSlaveConnection();

        // 测试分片表
        testShardingTables();

        System.out.println("\n[成功] 纯 JDBC 测试完成！");
    }

    private static void testMasterConnection() {
        System.out.println("\n--- 主库连接测试 ---");

        try (Connection conn = DriverManager.getConnection(MASTER_URL, USERNAME, PASSWORD)) {
            System.out.println("  主库连接成功: " + conn.getMetaData().getURL());

            // 插入测试数据（直接插入物理表 t_order_0）
            String insertSql = "INSERT INTO t_order_0 (order_id, user_id, amount, status, created_at) VALUES (?, ?, ?, ?, NOW())";
            try (PreparedStatement stmt = conn.prepareStatement(insertSql)) {
                long orderId = System.currentTimeMillis() % 100000;
                stmt.setLong(1, orderId);
                stmt.setInt(2, 1);
                stmt.setBigDecimal(3, new java.math.BigDecimal("100.00"));
                stmt.setString(4, "TEST");
                int affected = stmt.executeUpdate();
                System.out.println("  插入测试数据: order_id=" + orderId + " -> t_order_0, 影响行数=" + affected);
            }
        } catch (SQLException e) {
            System.out.println("  主库连接失败: " + e.getMessage());
        }
    }

    private static void testSlaveConnection() {
        System.out.println("\n--- 从库连接测试 ---");

        // 测试从库1
        try (Connection conn = DriverManager.getConnection(SLAVE0_URL, USERNAME, PASSWORD)) {
            System.out.println("  从库1连接成功: " + conn.getMetaData().getURL());

            // 验证数据同步（主库插入的数据应该同步到从库）
            String selectSql = "SELECT SUM(cnt) as total FROM (SELECT COUNT(*) as cnt FROM t_order_0 UNION ALL SELECT COUNT(*) as cnt FROM t_order_1 UNION ALL SELECT COUNT(*) as cnt FROM t_order_2 UNION ALL SELECT COUNT(*) as cnt FROM t_order_3) as all_tables";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(selectSql)) {
                if (rs.next()) {
                    System.out.println("  从库1 t_order 分片表总记录数: " + rs.getInt("total"));
                }
            }
        } catch (Exception e) {
            System.out.println("  从库1连接失败（请确保从库已启动）: " + e.getMessage());
        }

        // 测试从库2
        try (Connection conn = DriverManager.getConnection(SLAVE1_URL, USERNAME, PASSWORD)) {
            System.out.println("  从库2连接成功: " + conn.getMetaData().getURL());

            String selectSql = "SELECT SUM(cnt) as total FROM (SELECT COUNT(*) as cnt FROM t_order_0 UNION ALL SELECT COUNT(*) as cnt FROM t_order_1 UNION ALL SELECT COUNT(*) as cnt FROM t_order_2 UNION ALL SELECT COUNT(*) as cnt FROM t_order_3) as all_tables";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(selectSql)) {
                if (rs.next()) {
                    System.out.println("  从库2 t_order 分片表总记录数: " + rs.getInt("total"));
                }
            }
        } catch (Exception e) {
            System.out.println("  从库2连接失败（请确保从库已启动）: " + e.getMessage());
        }
    }

    private static void testShardingTables() {
        System.out.println("\n--- 分片表测试 ---");

        try (Connection conn = DriverManager.getConnection(MASTER_URL, USERNAME, PASSWORD)) {
            // 检查分片表
            String[] tables = {"t_order_0", "t_order_1", "t_order_2", "t_order_3"};

            for (String table : tables) {
                try {
                    String countSql = "SELECT COUNT(*) as cnt FROM " + table;
                    try (Statement stmt = conn.createStatement();
                         ResultSet rs = stmt.executeQuery(countSql)) {
                        if (rs.next()) {
                            System.out.println("  " + table + ": " + rs.getInt("cnt") + " 条记录");
                        }
                    }
                } catch (SQLException e) {
                    System.out.println("  " + table + ": 表不存在或为空");
                }
            }

            // 插入数据到不同分片
            System.out.println("\n  插入数据到不同分片:");

            for (int i = 0; i < 4; i++) {
                try {
                    long orderId = 3000L + i;
                    int tableIndex = (int) (orderId % 4);
                    String insertSql = "INSERT INTO t_order_" + tableIndex + " (order_id, user_id, amount, status, created_at) VALUES (?, ?, ?, ?, NOW())";

                    try (PreparedStatement stmt = conn.prepareStatement(insertSql)) {
                        stmt.setLong(1, orderId);
                        stmt.setInt(2, i + 1);
                        stmt.setBigDecimal(3, new java.math.BigDecimal("100.00").multiply(new java.math.BigDecimal(i + 1)));
                        stmt.setString(4, "SHARD_TEST");
                        stmt.executeUpdate();

                        System.out.println("    order_id=" + orderId + " -> t_order_" + tableIndex);
                    }
                } catch (SQLException e) {
                    System.out.println("    插入失败: " + e.getMessage());
                }
            }
        } catch (SQLException e) {
            System.out.println("  分片表测试失败: " + e.getMessage());
        }
    }

    public static void main(String[] args) throws Exception {
        run();
    }
}
