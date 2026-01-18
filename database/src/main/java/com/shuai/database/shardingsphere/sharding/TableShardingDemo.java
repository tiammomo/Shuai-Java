package com.shuai.database.shardingsphere.sharding;

import com.shuai.database.shardingsphere.config.ShardingConfig;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;

/**
 * ShardingSphere 分库分表演示
 * 演示水平分表、垂直分表、广播表
 */
public class TableShardingDemo {

    public static void run() throws Exception {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("       ShardingSphere 分库分表演示");
        System.out.println("=".repeat(50));

        // 创建分片数据源
        DataSource dataSource = ShardingConfig.createShardingDataSource();

        try (Connection conn = dataSource.getConnection()) {
            // 1. 水平分表演示
            horizontalShardingDemo(conn);

            // 2. 分片策略说明
            shardingStrategyDemo();

            // 3. 分片算法演示
            shardingAlgorithmDemo();
        }

        System.out.println("\n[成功] 分库分表演示完成！");
    }

    /**
     * 水平分表演示
     */
    private static void horizontalShardingDemo(Connection conn) throws SQLException {
        System.out.println("\n--- 水平分表 (Horizontal Sharding) ---");
        System.out.println("  策略: 按 order_id 取模分片到 4 张表");
        System.out.println("  表结构: t_order_0, t_order_1, t_order_2, t_order_3");
        System.out.println("  分片键: order_id");
        System.out.println("  分片算法: MOD (取模)");

        // 插入测试数据
        System.out.println("\n  插入测试数据:");
        String insertSql = "INSERT INTO t_order (order_id, user_id, amount, status, created_at) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(insertSql)) {
            for (int i = 0; i < 8; i++) {
                long orderId = 2000L + i;
                stmt.setLong(1, orderId);
                stmt.setInt(2, i + 1);
                stmt.setBigDecimal(3, new BigDecimal("100.00").multiply(new BigDecimal(i + 1)));
                stmt.setString(4, i % 2 == 0 ? "PAID" : "PENDING");
                stmt.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));

                stmt.executeUpdate();
                int tableIndex = (int) (orderId % 4);
                System.out.println("    order_id=" + orderId + " -> t_order_" + tableIndex);
            }
        }

        // 验证分片数据
        System.out.println("\n  验证分片数据分布:");
        for (int i = 0; i < 4; i++) {
            String countSql = "SELECT COUNT(*) as cnt FROM t_order_" + i;
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(countSql)) {
                if (rs.next()) {
                    System.out.println("    t_order_" + i + ": " + rs.getInt("cnt") + " 条记录");
                }
            }
        }
    }

    /**
     * 分片策略演示
     */
    private static void shardingStrategyDemo() {
        System.out.println("\n--- 分片策略 ---");
        System.out.println();
        System.out.println("  1. 标准分片策略 (StandardShardingStrategy)");
        System.out.println("     - 精确分片: =, IN");
        System.out.println("     - 范围分片: BETWEEN, >, <, >=, <=");
        System.out.println("     - 示例: t_order_${order_id % 4}");
        System.out.println();
        System.out.println("  2. 复合分片策略 (ComplexShardingStrategy)");
        System.out.println("     - 多个分片键");
        System.out.println("     - 示例: t_order_${order_id % 4}_${user_id % 2}");
        System.out.println();
        System.out.println("  3. 行表达式分片策略 (InlineShardingStrategy)");
        System.out.println("     - Groovy 表达式");
        System.out.println("     - 示例: t_order_${order_id % 4}");
        System.out.println();
        System.out.println("  4. Hint 分片策略 (HintShardingStrategy)");
        System.out.println("     - 强制指定分片");
        System.out.println("     - 适用于无法确定分片的查询");
    }

    /**
     * 分片算法演示
     */
    private static void shardingAlgorithmDemo() {
        System.out.println("\n--- 分片算法 ---");
        System.out.println();
        System.out.println("  1. 取模算法 (MOD)");
        System.out.println("     - 公式: table_${order_id % sharding_count}");
        System.out.println("     - 优点: 数据分布均匀");
        System.out.println("     - 缺点: 扩容需要数据迁移");
        System.out.println();
        System.out.println("  2. 哈希算法 (HASH_MOD)");
        System.out.println("     - 基于哈希值取模");
        System.out.println("     - 适用于字符串分片键");
        System.out.println();
        System.out.println("  3. 范围算法 (RANGE_BASED)");
        System.out.println("     - 按时间/ID 范围分片");
        System.out.println("     - 适用于时序数据");
        System.out.println("     - 示例: t_order_202401, t_order_202402");
        System.out.println();
        System.out.println("  4. 地理位置算法 (GEO)");
        System.out.println("     - 按地区分片");
        System.out.println("     - 适用于 LBS 应用");
    }

    public static void main(String[] args) throws Exception {
        run();
    }
}
