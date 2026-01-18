package com.shuai.database.shardingsphere.readwrite;

import com.shuai.database.shardingsphere.config.ShardingConfig;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;

/**
 * ShardingSphere 读写分离演示
 * 演示主从复制和读写分离路由
 */
public class ReadWriteDemo {

    public static void run() throws Exception {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("       ShardingSphere 读写分离演示");
        System.out.println("=".repeat(50));

        // 创建读写分离数据源
        DataSource dataSource = ShardingConfig.createReadWriteDataSource();

        try (Connection conn = dataSource.getConnection()) {
            // 1. 写操作（主库）
            writeDemo(conn);

            // 2. 读操作（从库）
            readDemo(conn);

            // 3. 强制主库读
            forceMasterReadDemo(conn);

            // 4. 架构说明
            architectureDemo();
        }

        System.out.println("\n[成功] 读写分离演示完成！");
    }

    /**
     * 写操作演示
     */
    private static void writeDemo(Connection conn) throws SQLException {
        System.out.println("\n--- 写操作（主库） ---");
        System.out.println("  路由规则: INSERT/UPDATE/DELETE -> ds_master");

        // 插入测试数据
        String insertSql = "INSERT INTO t_order (order_id, user_id, amount, status, created_at) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(insertSql)) {
            long orderId = System.currentTimeMillis() % 10000;
            stmt.setLong(1, orderId);
            stmt.setInt(2, 1);
            stmt.setBigDecimal(3, new java.math.BigDecimal("999.00"));
            stmt.setString(4, "PAID");
            stmt.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));

            int affected = stmt.executeUpdate();
            System.out.println("  插入订单 -> 主库 ds_master (影响行数: " + affected + ")");
            System.out.println("  order_id: " + orderId);
        }

        // 更新操作
        String updateSql = "UPDATE t_order SET status = 'PROCESSING' WHERE order_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(updateSql)) {
            stmt.setLong(1, System.currentTimeMillis() % 10000);
            int affected = stmt.executeUpdate();
            System.out.println("  更新订单 -> 主库 ds_master (影响行数: " + affected + ")");
        }
    }

    /**
     * 读操作演示
     */
    private static void readDemo(Connection conn) throws SQLException {
        System.out.println("\n--- 读操作（从库） ---");
        System.out.println("  路由规则: SELECT -> ds_slave0 / ds_slave1 (负载均衡)");

        String selectSql = "SELECT order_id, user_id, amount, status FROM t_order LIMIT 5";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(selectSql)) {
            System.out.println("  查询订单 -> 从库 ds_slave0/ds_slave1:");
            int count = 0;
            while (rs.next() && count < 3) {
                System.out.printf("    order_id=%d, amount=%s, status=%s%n",
                    rs.getLong("order_id"),
                    rs.getBigDecimal("amount"),
                    rs.getString("status"));
                count++;
            }
            if (count < 3) {
                System.out.println("    (数据较少)");
            }
        }
    }

    /**
     * 强制主库读演示
     */
    private static void forceMasterReadDemo(Connection conn) throws SQLException {
        System.out.println("\n--- 强制主库读 ---");
        System.out.println("  使用 Hint 或特殊配置强制 SELECT 走主库");
        System.out.println("  适用场景: 需要读取最新数据的强一致性查询");

        // 在 ShardingSphere 中可以通过 Hint 强制主库读
        // 这里演示普通查询（实际会走从库）
        String selectSql = "SELECT COUNT(*) as total FROM t_order";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(selectSql)) {
            if (rs.next()) {
                System.out.println("  统计订单总数 -> 从库 (总数: " + rs.getLong("total") + ")");
            }
        }

        System.out.println("  提示: 强制主库读需要在 ShardingSphere 配置中启用或在 SQL 中添加 Hint");
    }

    /**
     * 架构演示
     */
    private static void architectureDemo() {
        System.out.println("\n--- 读写分离架构 ---");
        System.out.println("  ┌─────────────────────────────────────┐");
        System.out.println("  │           Application               │");
        System.out.println("  └──────────────────┬──────────────────┘");
        System.out.println("                     │");
        System.out.println("                     ▼");
        System.out.println("  ┌─────────────────────────────────────┐");
        System.out.println("  │       ShardingSphere JDBC            │");
        System.out.println("  │  ┌───────────────────────────────┐   │");
        System.out.println("  │  │ 读写分离路由 (Round Robin)     │   │");
        System.out.println("  │  └───────────────────────────────┘   │");
        System.out.println("  └───────┬─────────────────┬───────────┘");
        System.out.println("          │                 │");
        System.out.println("     ┌────▼────┐      ┌────▼────┐");
        System.out.println("     │ ds_master│      │ds_slave0│");
        System.out.println("     │  (主库)  │      │ (从库)  │");
        System.out.println("     │   写     │      │   读    │");
        System.out.println("     └────┬────┘      └────┬────┘");
        System.out.println("          │                │");
        System.out.println("          └───────┬────────┘");
        System.out.println("                  │");
        System.out.println("                  ▼");
        System.out.println("     ┌──────────────────────┐");
        System.out.println("     │    MySQL 主从复制     │");
        System.out.println("     │   (binlog 同步)       │");
        System.out.println("     └──────────────────────┘");
    }

    public static void main(String[] args) throws Exception {
        run();
    }
}
