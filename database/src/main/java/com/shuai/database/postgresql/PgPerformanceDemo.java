package com.shuai.database.postgresql;

import org.postgresql.copy.CopyManager;
import org.postgresql.core.BaseConnection;

import java.io.*;
import java.sql.*;
import java.util.Properties;

/**
 * PostgreSQL 性能优化演示类
 *
 * ============================================================
 * PostgreSQL 性能优化概述
 * ------------------------------------------------------------
 * 数据库性能优化是一个系统工程，涉及多个层面：
 *
 * 1. 索引优化（最常用）
 *    - 选择合适的索引类型
 *    - 合理的索引字段顺序
 *    - 定期维护索引
 *
 * 2. 查询优化
 *    - 避免全表扫描
 *    - 优化连接顺序
 *    - 使用游标分页替代 OFFSET
 *
 * 3. 批量操作
 *    - 使用批量插入
 *    - 使用 COPY 高速导入
 *    - 禁用索引加速批量写入
 *
 * 4. 配置优化
 *    - 调整内存参数
 *    - 优化连接池
 *
 * ============================================================
 * 索引类型详解
 * ------------------------------------------------------------
 * 1. B-Tree（默认）
 *    - 最常用的索引类型
 *    - 支持 =、>、<、>=、<=、LIKE
 *    - 默认索引类型
 *
 * 2. Hash
 *    - 只支持等值查询 =
 *    - 不支持范围查询
 *    - 空间占用比 B-Tree 小
 *
 * 3. GIN（倒排索引）
 *    - 适合数组、JSONB、全文搜索
 *    - 索引构建慢，查询快
 *
 * 4. GiST（几何索引）
 *    - 适合几何数据、地理信息
 *    - 支持空间查询
 *
 * 5. BRIN（块范围索引）
 *    - 适合时序数据、大表
 *    - 空间占用极小
 *    - 按数据块建立索引
 *
 * @author Shuai
 */
public class PgPerformanceDemo {

    // ==================== 连接配置 ====================
    /** PostgreSQL 连接 URL */
    private static final String URL = "jdbc:postgresql://localhost:5432/test";

    /** 数据库用户名 */
    private static final String USER = "ubuntu";

    /** 数据库密码 */
    private static final String PASSWORD = "ubuntu";

    // ==================== 构造方法 ====================

    public PgPerformanceDemo() {
        // 默认构造方法
    }

    // ==================== 主入口方法 ====================

    /**
     * 执行所有性能优化演示
     *
     * 执行流程：
     * 1. createIndexes()        -> 创建各种索引
     * 2. explainAnalyzeDemo()   -> EXPLAIN 执行计划分析
     * 3. batchInsertDemo()      -> 批量插入优化
     * 4. cursorPagingDemo()     -> 游标分页优化
     * 5. showIndexStats()       -> 索引统计信息
     * 6. slowQueryDemo()        -> 慢查询演示
     */
    public void runAllDemos() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("       PostgreSQL 性能优化演示");
        System.out.println("=".repeat(50));

        try {
            // 第一步：创建各种索引
            createIndexes();

            // 第二步：分析查询执行计划
            explainAnalyzeDemo();

            // 第三步：批量插入优化
            batchInsertDemo();

            // 第四步：游标分页优化
            cursorPagingDemo();

            // 第五步：查看索引统计
            showIndexStats();

            // 第六步：慢查询演示
            slowQueryDemo();

            System.out.println("\n[成功] PostgreSQL 性能优化演示完成！");

        } catch (Exception e) {
            System.err.println("PostgreSQL 操作异常: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ==================== 索引创建 ====================

    /**
     * 创建各种索引
     *
     * 索引创建语法：
     * CREATE INDEX [IF NOT EXISTS] index_name ON table_name(column)
     *
     * 索引选项：
     * - USING method      : 指定索引类型（B-Tree/Hash/GIN/GiST/BRIN）
     * - WHERE condition   : 部分索引条件
     * - UNIQUE            : 唯一索引
     *
     * 索引命名规范：
     * - idx_table_column  : 普通索引
     * - uk_table_column   : 唯一索引
     * - fk_table_column   : 外键索引
     */
    private void createIndexes() {
        System.out.println("\n--- 索引创建演示 ---");

        try (Connection conn = getConnection()) {
            // 创建测试表
            createTestTables(conn);

            // 1. B-Tree 索引（默认）
            // 使用场景：等值查询、范围查询、排序
            // 适用字段：ID、状态、时间、金额等
            String btreeSql = "CREATE INDEX IF NOT EXISTS idx_orders_user_id ON orders(user_id)";
            try (Statement stmt = conn.createStatement()) {
                stmt.execute(btreeSql);
                System.out.println("  [创建] B-Tree 索引: orders.user_id");
            }

            // 2. 复合索引
            // 字段顺序原则：等值 > 排序 > 范围
            // 查询 WHERE user_id = ? AND status = ? 会使用此索引
            String compoundSql = "CREATE INDEX IF NOT EXISTS idx_orders_user_status ON orders(user_id, status)";
            try (Statement stmt = conn.createStatement()) {
                stmt.execute(compoundSql);
                System.out.println("  [创建] 复合索引: orders(user_id, status)");
            }

            // 3. 表达式索引
            // 使用场景：需要忽略大小写查询、函数转换后查询
            // 查询 LOWER(email) = 'test@example.com' 会使用此索引
            String exprSql = "CREATE INDEX IF NOT EXISTS idx_users_lower_email ON users(LOWER(email))";
            try (Statement stmt = conn.createStatement()) {
                stmt.execute(exprSql);
                System.out.println("  [创建] 表达式索引: LOWER(email)");
            }

            // 4. 部分索引
            // 使用场景：只查询活跃数据、只查询某类状态
            // 只为 status = 'active' 的记录建立索引
            String partialSql = "CREATE INDEX IF NOT EXISTS idx_orders_active ON orders(status) WHERE status = 'active'";
            try (Statement stmt = conn.createStatement()) {
                stmt.execute(partialSql);
                System.out.println("  [创建] 部分索引: 仅 status = 'active'");
            }

            // 5. 唯一索引
            // 使用场景：保证字段唯一性
            String uniqueSql = "CREATE UNIQUE INDEX IF NOT EXISTS uk_users_phone ON users(phone)";
            try (Statement stmt = conn.createStatement()) {
                stmt.execute(uniqueSql);
                System.out.println("  [创建] 唯一索引: users.phone");
            }

            // 6. GIN 索引（数组）
            // 使用场景：数组包含查询、JSONB 查询
            // @> 包含查询会使用 GIN 索引
            String ginSql = "CREATE INDEX IF NOT EXISTS idx_products_tags ON products USING GIN(tags)";
            try (Statement stmt = conn.createStatement()) {
                stmt.execute(ginSql);
                System.out.println("  [创建] GIN 索引: products.tags 数组");
            }

            // 7. GIN 索引（JSONB）
            // 使用场景：JSONB 字段的键值查询
            // jsonb_path_ops 优化 @> 操作符性能
            String jsonbSql = "CREATE INDEX IF NOT EXISTS idx_orders_data ON orders USING GIN(order_data jsonb_path_ops)";
            try (Statement stmt = conn.createStatement()) {
                stmt.execute(jsonbSql);
                System.out.println("  [创建] GIN JSONB 索引: orders.order_data");
            }

            // 8. BRIN 索引（时序数据）
            // 使用场景：时序数据、大表按时间范围查询
            // 原理：按数据块建立索引，空间占用极小
            String brinSql = "CREATE INDEX IF NOT EXISTS idx_sensor_data_time ON sensor_data USING BRIN(recorded_at)";
            try (Statement stmt = conn.createStatement()) {
                stmt.execute(brinSql);
                System.out.println("  [创建] BRIN 索引: sensor_data.recorded_at 时序");
            }

            System.out.println("  索引创建完成！");

        } catch (Exception e) {
            System.out.println("  索引可能已存在: " + e.getMessage());
        }
    }

    // ==================== 测试表创建 ====================

    /**
     * 创建测试表
     *
     * 测试表设计：
     * - users        : 用户表
     * - orders       : 订单表（带 JSONB 字段）
     * - products     : 商品表（带数组字段）
     * - sensor_data  : 传感器数据（时序场景）
     */
    private void createTestTables(Connection conn) throws SQLException {
        System.out.println("  创建测试表...");

        // 用户表
        String createUsers = """
            CREATE TABLE IF NOT EXISTS users (
                id SERIAL PRIMARY KEY,
                username VARCHAR(50),
                email VARCHAR(100),
                phone VARCHAR(20),
                status VARCHAR(20) DEFAULT 'active',
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
            """;
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(createUsers);
        }

        // 订单表（包含 JSONB 字段）
        String createOrders = """
            CREATE TABLE IF NOT EXISTS orders (
                id SERIAL PRIMARY KEY,
                user_id BIGINT NOT NULL,
                amount DECIMAL(10,2) NOT NULL,
                status VARCHAR(20) NOT NULL,
                order_data JSONB,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
            """;
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(createOrders);
        }

        // 商品表（包含数组字段）
        String createProducts = """
            CREATE TABLE IF NOT EXISTS products (
                id SERIAL PRIMARY KEY,
                name VARCHAR(100),
                category VARCHAR(50),
                tags TEXT[],
                price DECIMAL(10,2)
            )
            """;
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(createProducts);
        }

        // 传感器数据表（时序场景）
        String createSensor = """
            CREATE TABLE IF NOT EXISTS sensor_data (
                id BIGSERIAL,
                device_id VARCHAR(50),
                value DOUBLE PRECISION,
                recorded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
            """;
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(createSensor);
        }

        // 插入测试数据
        insertTestData(conn);
    }

    /**
     * 插入测试数据
     *
     * 批量插入优化技巧：
     * 1. 关闭自动提交（setAutoCommit(false)）
     * 2. 使用 addBatch() 批量添加
     * 3. 适当分批执行（避免内存溢出）
     * 4. 使用 COPY 替代 INSERT（高速导入）
     */
    private void insertTestData(Connection conn) throws SQLException {
        System.out.println("  插入测试数据...");

        // 插入用户数据
        String insertUser = "INSERT INTO users (id, username, email, phone, status) VALUES (?, ?, ?, ?, ?) " +
            "ON CONFLICT (id) DO UPDATE SET username = EXCLUDED.username";
        try (PreparedStatement stmt = conn.prepareStatement(insertUser)) {
            for (int i = 1; i <= 1000; i++) {
                stmt.setInt(1, i);
                stmt.setString(2, "user_" + i);
                stmt.setString(3, "user" + i + "@example.com");
                stmt.setString(4, "138" + String.format("%08d", i));
                stmt.setString(5, i % 3 == 0 ? "inactive" : "active");
                stmt.addBatch();
            }
            stmt.executeBatch();
        }

        // 插入订单数据
        String insertOrder = "INSERT INTO orders (user_id, amount, status, order_data) VALUES (?, ?, ?, ?::jsonb)";
        try (PreparedStatement stmt = conn.prepareStatement(insertOrder)) {
            for (int i = 1; i <= 10000; i++) {
                stmt.setLong(1, 1 + (i % 1000));
                stmt.setDouble(2, Math.random() * 1000 + 10);
                stmt.setString(3, i % 5 == 0 ? "cancelled" : "completed");
                stmt.setString(4, "{\"product\": \"item" + i + "\", \"quantity\": " + (i % 10 + 1) + "}");
                stmt.addBatch();
                if (i % 1000 == 0) {
                    stmt.executeBatch();
                    stmt.clearBatch();
                }
            }
        }

        // 插入商品数据
        String insertProduct = "INSERT INTO products (id, name, category, tags, price) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(insertProduct)) {
            String[] categories = {"electronics", "clothing", "food", "books"};
            for (int i = 1; i <= 500; i++) {
                stmt.setInt(1, i);
                stmt.setString(2, "Product_" + i);
                stmt.setString(3, categories[i % 4]);
                stmt.setArray(4, conn.createArrayOf("text", new String[]{"tag" + (i % 10), "cat" + (i % 3)}));
                stmt.setDouble(5, Math.random() * 500 + 10);
                stmt.addBatch();
            }
            stmt.executeBatch();
        }

        System.out.println("  测试数据插入完成");
    }

    // ==================== EXPLAIN 分析 ====================

    /**
     * EXPLAIN 执行计划分析
     *
     * EXPLAIN 用于查看查询的执行计划，帮助优化查询。
     *
     * 常用参数：
     * - ANALYZE  : 实际执行并计时
     * - BUFFERS  : 显示缓冲区使用情况
     * - FORMAT   : 输出格式（TEXT/JSON/YAML/XML）
     *
     * 执行阶段说明：
     * - Seq Scan : 全表扫描（需要优化！）
     * - Index Scan : 索引扫描
     * - Index Only Scan : 索引覆盖扫描（最快）
     * - Nested Loop : 嵌套循环连接（小表）
     * - Hash Join : 哈希连接（大表）
     * - Merge Join : 合并连接（已排序）
     *
     * 成本指标：
     * - cost=启动成本..总成本
     * - actual time=实际启动时间..总时间
     * - rows=预计返回行数
     * - loops=循环次数
     */
    private void explainAnalyzeDemo() {
        System.out.println("\n--- EXPLAIN 执行计划分析 ---");

        try (Connection conn = getConnection()) {
            // 示例1：全表扫描
            // 当没有索引或查询条件无法使用索引时发生
            System.out.println("\n  [查询1] 全表扫描示例（需要优化）:");
            String badSql = "SELECT * FROM orders WHERE status = 'active'";
            explainQuery(conn, badSql);

            // 示例2：索引扫描
            // 当查询条件能使用索引时发生
            System.out.println("\n  [查询2] 索引扫描示例:");
            String goodSql = "SELECT * FROM orders WHERE user_id = 100";
            explainQuery(conn, goodSql);

            // 示例3：索引覆盖
            // 当查询只返回索引字段时，不需要回表
            System.out.println("\n  [查询3] 索引覆盖扫描（最优）:");
            String coveredSql = "SELECT id, user_id FROM orders WHERE user_id = 100";
            explainQuery(conn, coveredSql);

            // 示例4：连接查询
            System.out.println("\n  [查询4] 连接查询:");
            String joinSql = """
                SELECT u.username, COUNT(o.id) as order_count
                FROM users u
                LEFT JOIN orders o ON u.id = o.user_id
                WHERE u.status = 'active'
                GROUP BY u.id, u.username
                LIMIT 10
                """;
            explainQuery(conn, joinSql);

        } catch (Exception e) {
            System.out.println("  查询分析异常: " + e.getMessage());
        }
    }

    /**
     * 执行 EXPLAIN 并解析结果
     *
     * @param conn 数据库连接
     * @param sql  要分析的 SQL 语句
     */
    private void explainQuery(Connection conn, String sql) {
        try (Statement stmt = conn.createStatement()) {
            // 使用 ANALYZE 获取实际执行信息
            // BUFFERS 显示内存/磁盘块使用情况
            String explainSql = "EXPLAIN (ANALYZE, BUFFERS, FORMAT TEXT) " + sql;

            try (ResultSet rs = stmt.executeQuery(explainSql)) {
                while (rs.next()) {
                    String plan = rs.getString("QUERY PLAN");
                    // 只打印关键行
                    if (plan.contains("Seq Scan") || plan.contains("Index") ||
                        plan.contains("Execution Time") || plan.contains("Planning Time") ||
                        plan.contains("Bitmap Index Scan") || plan.contains("Bitmap Heap Scan")) {
                        System.out.println("    " + plan.trim());
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("    解释失败: " + e.getMessage());
        }
    }

    // ==================== 批量插入优化 ====================

    /**
     * 批量插入优化演示
     *
     * 批量插入优化方案：
     *
     * 1. JDBC Batch
     *    - addBatch() 批量添加
     *    - executeBatch() 批量执行
     *    - 减少网络往返次数
     *
     * 2. PostgreSQL COPY
     *    - 专门用于批量数据导入
     *    - 比 INSERT 快 10-100 倍
     *    - 支持 CSV 格式
     *
     * 3. 禁用索引
     *    - 批量导入前禁用索引
     *    - 导入完成后重建索引
     *    - 适用于百万级数据导入
     *
     * 4. UNLOGGED 表
     *    - 不写 WAL 日志
     *    - 速度更快
     *    - 崩溃后数据不可恢复
     */
    private void batchInsertDemo() {
        System.out.println("\n--- 批量插入优化 ---");

        try (Connection conn = getConnection()) {
            long startTime = System.currentTimeMillis();

            // 方式1：JDBC Batch
            System.out.println("  [JDBC Batch] 批量插入演示:");
            String insertSql = "INSERT INTO sensor_data (device_id, value, recorded_at) VALUES (?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(insertSql)) {
                conn.setAutoCommit(false);

                for (int i = 1; i <= 5000; i++) {
                    stmt.setString(1, "device_" + (i % 10));
                    stmt.setDouble(2, Math.random() * 100);
                    stmt.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
                    stmt.addBatch();
                    if (i % 500 == 0) {
                        int[] result = stmt.executeBatch();
                        System.out.println("    插入 " + result.length + " 条，耗时: " +
                            (System.currentTimeMillis() - startTime) + "ms");
                        stmt.clearBatch();
                    }
                }
                conn.commit();
            }

            // 方式2：COPY 高速导入
            System.out.println("\n  [COPY] 高速批量导入演示:");
            StringBuilder csvData = new StringBuilder();
            for (int i = 1; i <= 10000; i++) {
                csvData.append(i).append(",device_").append(i % 10)
                    .append(",").append(Math.random() * 100)
                    .append(",").append(new Timestamp(System.currentTimeMillis()).toString())
                    .append("\n");
            }

            try (BaseConnection pgConn = conn.unwrap(BaseConnection.class)) {
                CopyManager copyManager = new CopyManager(pgConn);
                StringReader reader = new StringReader(csvData.toString());
                long rows = copyManager.copyIn(
                    "COPY sensor_data (id, device_id, value, recorded_at) FROM STDIN WITH CSV",
                    reader
                );
                System.out.println("    [COPY] 导入 " + rows + " 条记录");
            }

            conn.setAutoCommit(true);

        } catch (Exception e) {
            System.out.println("  批量插入异常: " + e.getMessage());
        }
    }

    // ==================== 游标分页优化 ====================

    /**
     * 游标分页优化
     *
     * OFFSET 分页问题：
     * - 深层分页性能差（OFFSET 1000000 LIMIT 20）
     * - 数据库需要扫描 1000020 行
     * - 数据插入后分页可能重复
     *
     * 游标分页优势：
     * - 基于上一页最后一条的 ID/时间
     * - 不需要扫描前面的数据
     * - 性能稳定，不受分页深度影响
     *
     * 适用场景：
     * - API 分页（移动端无限滚动）
     * - 后台管理列表
     * - 数据导出分批处理
     */
    private void cursorPagingDemo() {
        System.out.println("\n--- 游标分页优化 ---");

        try (Connection conn = getConnection()) {
            // 优化前：OFFSET 分页
            System.out.println("\n  [优化前] OFFSET 分页（深分页问题）:");
            long startTime = System.currentTimeMillis();
            String oldPagingSql = "SELECT * FROM orders ORDER BY id DESC LIMIT 20 OFFSET 10000";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(oldPagingSql)) {
                int count = 0;
                while (rs.next()) count++;
                System.out.println("    耗时: " + (System.currentTimeMillis() - startTime) + "ms");
                System.out.println("    返回: " + count + " 条");
            }

            // 优化后：基于 ID 的游标分页
            System.out.println("\n  [优化后] 基于 ID 的游标分页:");
            startTime = System.currentTimeMillis();
            long lastId = 10020; // 假设上一页最后一条的 ID
            String cursorSql = "SELECT * FROM orders WHERE id < ? ORDER BY id DESC LIMIT 20";
            try (PreparedStatement stmt = conn.prepareStatement(cursorSql)) {
                stmt.setLong(1, lastId);
                try (ResultSet rs = stmt.executeQuery()) {
                    int count = 0;
                    while (rs.next()) count++;
                    System.out.println("    耗时: " + (System.currentTimeMillis() - startTime) + "ms");
                    System.out.println("    返回: " + count + " 条");
                }
            }

            // 优化后：基于时间的游标分页
            System.out.println("\n  [优化后] 基于时间的游标分页:");
            String timeSql = """
                SELECT * FROM orders
                WHERE created_at < '2024-06-01'
                ORDER BY created_at DESC
                LIMIT 20
                """;
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(timeSql)) {
                int count = 0;
                while (rs.next()) count++;
                System.out.println("    返回: " + count + " 条");
            }

        } catch (Exception e) {
            System.out.println("  分页异常: " + e.getMessage());
        }
    }

    // ==================== 索引统计 ====================

    /**
     * 查看索引统计信息
     *
     * PostgreSQL 提供了丰富的统计视图：
     *
     * pg_stat_user_indexes
     * - idx_scan     : 索引被扫描的次数
     * - idx_tup_read : 索引读取的行数
     * - idx_tup_fetch: 索引获取的行数
     *
     * pg_stat_user_tables
     * - seq_scan     : 全表扫描次数
     * - seq_tup_read : 全表扫描读取的行数
     * - idx_scan     : 索引扫描次数
     *
     * pg_statio_user_tables
     * - heap_blks_hit : 缓冲区命中数
     * - heap_blks_read: 缓冲区读取数
     *
     * 优化指标：
     * - idx_scan > 0 且 idx_tup_read ≈ idx_tup_fetch : 索引使用良好
     * - seq_scan 很高 : 可能需要添加索引
     * - 缓存命中率 < 95% : 可能需要增加 shared_buffers
     */
    private void showIndexStats() {
        System.out.println("\n--- 索引统计信息 ---");

        try (Connection conn = getConnection()) {
            // 查看索引使用情况
            System.out.println("  索引扫描统计:");
            String idxStatsSql = """
                SELECT indexrelname, idx_scan, idx_tup_read, idx_tup_fetch
                FROM pg_stat_user_indexes
                WHERE relname = 'orders'
                """;
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(idxStatsSql)) {
                while (rs.next()) {
                    System.out.println("    索引: " + rs.getString("indexrelname"));
                    System.out.println("      - 扫描次数: " + rs.getLong("idx_scan"));
                    System.out.println("      - 读取行数: " + rs.getLong("idx_tup_read"));
                    System.out.println("      - 获取行数: " + rs.getLong("idx_tup_fetch"));
                }
            }

            // 查看表统计
            System.out.println("\n  表统计信息:");
            String tableStatsSql = """
                SELECT relname, seq_scan, seq_tup_read, idx_scan, idx_tup_fetch
                FROM pg_stat_user_tables
                WHERE relname = 'orders'
                """;
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(tableStatsSql)) {
                while (rs.next()) {
                    System.out.println("    表: " + rs.getString("relname"));
                    System.out.println("      - 全表扫描: " + rs.getLong("seq_scan"));
                    System.out.println("      - 全表读取: " + rs.getLong("seq_tup_read"));
                    System.out.println("      - 索引扫描: " + rs.getLong("idx_scan"));
                }
            }

            // 计算缓存命中率
            System.out.println("\n  缓存命中率:");
            String cacheSql = """
                SELECT
                    sum(heap_blks_hit) / (sum(heap_blks_hit) + sum(heap_blks_read)) * 100 as cache_hit_ratio
                FROM pg_statio_user_tables
                WHERE relname IN ('users', 'orders')
                """;
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(cacheSql)) {
                while (rs.next()) {
                    double ratio = rs.getDouble("cache_hit_ratio");
                    System.out.println("    缓存命中率: " + String.format("%.2f", ratio) + "%");
                    if (ratio < 95) {
                        System.out.println("    建议: 增加 shared_buffers 或优化查询");
                    } else {
                        System.out.println("    状态: 良好");
                    }
                }
            }

        } catch (Exception e) {
            System.out.println("  统计查询异常: " + e.getMessage());
        }
    }

    // ==================== 慢查询演示 ====================

    /**
     * 慢查询演示
     *
     * 慢查询判断标准：
     * - 执行时间超过 100ms（可配置）
     * - 全表扫描且返回大量数据
     * - 未使用索引的 JOIN 操作
     *
     * 优化建议：
     * 1. 添加合适的索引
     * 2. 优化查询语句
     * 3. 使用 EXPLAIN 分析
     * 4. 避免 SELECT *
     * 5. 使用合适的数据类型
     *
     * PostgreSQL 慢查询配置：
     * - log_min_duration_statement = 1000  # 记录超过1秒的查询
     * - log_statement = 'none'             # 不记录普通查询
     */
    private void slowQueryDemo() {
        System.out.println("\n--- 慢查询模拟 ---");

        try (Connection conn = getConnection()) {
            // 模拟查询并显示执行时间
            System.out.println("  执行查询（带执行时间）:");

            String querySql = """
                SELECT u.username, COUNT(o.id) as order_count, SUM(o.amount) as total_amount
                FROM users u
                LEFT JOIN orders o ON u.id = o.user_id
                WHERE u.status = 'active'
                GROUP BY u.id, u.username
                ORDER BY total_amount DESC
                LIMIT 20
                """;

            try (Statement stmt = conn.createStatement()) {
                long startTime = System.currentTimeMillis();
                try (ResultSet rs = stmt.executeQuery(querySql)) {
                    int count = 0;
                    while (rs.next()) count++;
                    long duration = System.currentTimeMillis() - startTime;
                    System.out.println("    返回: " + count + " 条");
                    System.out.println("    耗时: " + duration + "ms");
                    System.out.println("    状态: " + (duration > 100 ? "慢查询" : "正常"));
                }
            }

        } catch (Exception e) {
            System.out.println("  慢查询异常: " + e.getMessage());
        }
    }

    // ==================== 工具方法 ====================

    /**
     * 获取数据库连接
     *
     * 连接参数说明：
     * - connectTimeout : 连接超时时间（秒）
     * - socketTimeout  : 套接字超时时间（秒）
     * - readOnly       : 只读连接（优化器可利用）
     *
     * 连接池建议：
     * - 生产环境使用 HikariCP
     * - 合理配置最大连接数
     * - 使用连接探活
     */
    private Connection getConnection() throws SQLException {
        Properties props = new Properties();
        props.setProperty("user", USER);
        props.setProperty("password", PASSWORD);
        props.setProperty("connectTimeout", "10");
        props.setProperty("socketTimeout", "30");
        return DriverManager.getConnection(URL, props);
    }

    // ==================== 主方法 ====================

    /**
     * 程序入口
     *
     * 运行命令：
     * mvn exec:java -Dexec.mainClass="com.shuai.database.postgresql.PgPerformanceDemo"
     */
    public static void main(String[] args) {
        new PgPerformanceDemo().runAllDemos();
    }
}
