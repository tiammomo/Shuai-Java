package com.shuai.database.shardingsphere.basic;

import com.shuai.database.shardingsphere.config.ShardingConfig;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * ShardingSphere 基本 CRUD 操作演示
 * 演示分片表的插入、查询、更新、删除操作
 */
public class ShardingBasicDemo {

    public static void run() throws Exception {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("       ShardingSphere 基本 CRUD 演示");
        System.out.println("=".repeat(50));

        // 创建分片数据源
        DataSource dataSource = ShardingConfig.createShardingDataSource();

        try (Connection conn = dataSource.getConnection()) {
            // 1. 插入演示
            insertDemo(conn);

            // 2. 查询演示
            queryDemo(conn);

            // 3. 更新演示
            updateDemo(conn);

            // 4. 删除演示
            deleteDemo(conn);

            // 5. 分片路由验证
            shardingRoutingDemo(conn);
        }

        System.out.println("\n[成功] 基本 CRUD 演示完成！");
    }

    /**
     * 插入演示
     */
    private static void insertDemo(Connection conn) throws SQLException {
        System.out.println("\n--- 插入操作 ---");

        String sql = "INSERT INTO t_order (order_id, user_id, amount, status, created_at) VALUES (?, ?, ?, ?, ?)";

        // 插入不同分片的数据（根据 order_id 取模路由到不同表）
        Object[][] testData = {
            {1001L, 1, new BigDecimal("199.99"), "PAID"},
            {1002L, 2, new BigDecimal("299.50"), "PENDING"},
            {1003L, 3, new BigDecimal("99.00"), "SHIPPED"},
            {1004L, 4, new BigDecimal("599.00"), "DELIVERED"},
            {1005L, 5, new BigDecimal("159.00"), "CANCELLED"},
            {1006L, 6, new BigDecimal("899.00"), "PAID"},
            {1007L, 7, new BigDecimal("449.00"), "PENDING"},
            {1008L, 8, new BigDecimal("229.00"), "SHIPPED"},
        };

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (Object[] row : testData) {
                stmt.setLong(1, (Long) row[0]);
                stmt.setInt(2, (Integer) row[1]);
                stmt.setBigDecimal(3, (BigDecimal) row[2]);
                stmt.setString(4, (String) row[3]);
                stmt.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));

                int affected = stmt.executeUpdate();
                int tableIndex = (int) ((Long) row[0] % 4);
                System.out.println("  插入 order_id=" + row[0] + " -> t_order_" + tableIndex + " (影响行数: " + affected + ")");
            }
        }
    }

    /**
     * 查询演示
     */
    private static void queryDemo(Connection conn) throws SQLException {
        System.out.println("\n--- 查询操作 ---");

        // 查询全部
        String selectAllSql = "SELECT order_id, user_id, amount, status FROM t_order";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(selectAllSql)) {
            System.out.println("  查询全部订单:");
            int count = 0;
            while (rs.next() && count < 5) {
                System.out.printf("    order_id=%d, user_id=%d, amount=%s, status=%s%n",
                    rs.getLong("order_id"),
                    rs.getInt("user_id"),
                    rs.getBigDecimal("amount"),
                    rs.getString("status"));
                count++;
            }
            if (count == 5) {
                System.out.println("    ... (更多数据)");
            }
        }

        // 条件查询 - 精确查询单条
        String querySql = "SELECT order_id, user_id, amount, status FROM t_order WHERE order_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(querySql)) {
            stmt.setLong(1, 1001L);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    System.out.printf("  查询 order_id=1001: user_id=%d, amount=%s, status=%s%n",
                        rs.getInt("user_id"),
                        rs.getBigDecimal("amount"),
                        rs.getString("status"));
                }
            }
        }

        // 范围查询 - 跨分片查询
        String rangeSql = "SELECT order_id, user_id, amount, status FROM t_order WHERE order_id BETWEEN ? AND ?";
        try (PreparedStatement stmt = conn.prepareStatement(rangeSql)) {
            stmt.setLong(1, 1001L);
            stmt.setLong(2, 1005L);
            try (ResultSet rs = stmt.executeQuery()) {
                System.out.println("  范围查询 order_id BETWEEN 1001 AND 1005:");
                while (rs.next()) {
                    System.out.printf("    order_id=%d, user_id=%d, amount=%s%n",
                        rs.getLong("order_id"),
                        rs.getInt("user_id"),
                        rs.getBigDecimal("amount"));
                }
            }
        }
    }

    /**
     * 更新演示
     */
    private static void updateDemo(Connection conn) throws SQLException {
        System.out.println("\n--- 更新操作 ---");

        // 更新单个订单状态
        String updateSql = "UPDATE t_order SET status = ? WHERE order_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(updateSql)) {
            stmt.setString(1, "COMPLETED");
            stmt.setLong(2, 1001L);
            int affected = stmt.executeUpdate();
            int tableIndex = 1001 % 4;
            System.out.println("  更新 order_id=1001 状态为 COMPLETED -> t_order_" + tableIndex + " (影响行数: " + affected + ")");
        }

        // 批量更新 - 按状态更新
        String batchUpdateSql = "UPDATE t_order SET status = 'PROCESSING' WHERE status = ?";
        try (PreparedStatement stmt = conn.prepareStatement(batchUpdateSql)) {
            stmt.setString(1, "PENDING");
            int affected = stmt.executeUpdate();
            System.out.println("  将 PENDING 状态更新为 PROCESSING: " + affected + " 条记录");
        }
    }

    /**
     * 删除演示
     */
    private static void deleteDemo(Connection conn) throws SQLException {
        System.out.println("\n--- 删除操作 ---");

        // 先插入一条测试数据
        String insertSql = "INSERT INTO t_order (order_id, user_id, amount, status, created_at) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(insertSql)) {
            stmt.setLong(1, 9999L);
            stmt.setInt(2, 99);
            stmt.setBigDecimal(3, new BigDecimal("99.99"));
            stmt.setString(4, "TEST");
            stmt.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
            stmt.executeUpdate();
        }

        // 删除测试数据
        String deleteSql = "DELETE FROM t_order WHERE order_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(deleteSql)) {
            stmt.setLong(1, 9999L);
            int affected = stmt.executeUpdate();
            int tableIndex = 9999 % 4;
            System.out.println("  删除 order_id=9999 -> t_order_" + tableIndex + " (影响行数: " + affected + ")");
        }

        // 按条件删除
        String batchDeleteSql = "DELETE FROM t_order WHERE status = ?";
        try (PreparedStatement stmt = conn.prepareStatement(batchDeleteSql)) {
            stmt.setString(1, "TEST");
            int affected = stmt.executeUpdate();
            System.out.println("  删除状态为 TEST 的记录: " + affected + " 条");
        }
    }

    /**
     * 分片路由演示
     */
    private static void shardingRoutingDemo(Connection conn) throws SQLException {
        System.out.println("\n--- 分片路由验证 ---");

        // 验证不同 order_id 路由到不同表
        String[] testOrderIds = {"1001", "1002", "1003", "1004"};
        String explainSql = "SELECT * FROM t_order WHERE order_id = ?";

        System.out.println("  ShardingSphere 分片路由规则: t_order_${order_id % 4}");
        System.out.println("  ┌──────────┬───────────┬────────┐");
        System.out.println("  │ order_id │ 目标表    │ 路由结果 │");
        System.out.println("  ├──────────┼───────────┼────────┤");

        try (PreparedStatement stmt = conn.prepareStatement(explainSql)) {
            for (String orderId : testOrderIds) {
                long orderIdLong = Long.parseLong(orderId);
                int tableIndex = (int) (orderIdLong % 4);

                stmt.setLong(1, orderIdLong);
                try (ResultSet rs = stmt.executeQuery()) {
                    boolean found = rs.next();
                    String result = found ? "命中" : "未命中";
                    System.out.printf("  │ %-8s │ t_order_%d │ %s     │%n",
                        orderId, tableIndex, result);
                }
            }
        }
        System.out.println("  └──────────┴───────────┴────────┘");
    }

    public static void main(String[] args) throws Exception {
        run();
    }
}
